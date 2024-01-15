package lu.even.manual_timing.verticles;

import io.vertx.core.eventbus.Message;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.jdbc.JDBCClient;
import lu.even.manual_timing.domain.ManualTime;
import lu.even.manual_timing.events.EventAction;
import lu.even.manual_timing.events.EventMessage;
import lu.even.manual_timing.events.EventTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.stream.Collectors;

public class TimingDatabaseVerticle extends AbstractTimingVerticle {
  public static final String DATASOURCE_NAME = "manual-time";
  protected static final Logger logger = LoggerFactory.getLogger(TimingDatabaseVerticle.class);
  private final String url;
  private JDBCClient client;

  public TimingDatabaseVerticle(String url) {
    super(EventTypes.DATABASE);
    setRespond(false);
    this.url = url;
  }

  @Override
  public void start() throws Exception {
    super.start();
    logger.debug("Connecting to:{}", this.url);
    JsonObject config = new JsonObject()
      .put("url", this.url)
      .put("datasourceName", DATASOURCE_NAME)
      //.put("user", "sa")
      //.put("password", "")
      .put("max_pool_size", 16);
    client = JDBCClient.create(vertx, config);
    loadTimes();
  }

  //Load times from database and send them to Manual Time verticle for loading
  private void loadTimes() {
    client.query("select * from times", arh -> {
      if (arh.failed()) {
        logger.warn("query succeeded:{} because:{}", arh.succeeded(), arh.cause().getMessage());
        createTable(client);
      } else {
        var times = arh.result().getRows().stream().map(
          row -> new ManualTime()
            .setEvent(row.getInteger("EVENT"))
            .setHeat(row.getInteger("HEAT"))
            .setLane(row.getInteger("LANE"))
            .setDistance(row.getInteger("DISTANCE"))
            .setTime(row.getString("TIME"))
        ).collect(Collectors.toList());
        logger.info("Loaded times:{}", times);
        sendMessage(EventTypes.MANUAL_TIME, EventAction.REPLACE_TIMES, times, -1, -1, -1,-1, null);
      }
    });
  }

  private void createTable(JDBCClient client) {
    client.getConnection(con -> {
      if (con.succeeded()) {
        con.result().execute("create table times(event int, heat int, lane int, distance int, time varchar)",
          rh -> logger.info("table times created:{}", rh.succeeded(), rh.cause())
        );
        con.result().close();
      } else {
        logger.error("Could not connect:{}", url, con.cause());
      }
    });

  }

  @Override
  protected void onMessage(EventTypes eventType, Message<EventMessage> message) {
    if (message.body().action() == EventAction.SAVE_TIME) {
      ManualTime time = Json.decodeValue(message.body().body(), ManualTime.class);
      save(time);
      answer(message, "ok");
    }
  }

  //Save time in DB
  private void save(ManualTime time) {
    JsonArray params = new JsonArray()
      .add(time.getTime())
      .add(time.getEvent())
      .add(time.getHeat())
      .add(time.getLane())
      .add(time.getDistance());
    client.updateWithParams(
      "update times set time=? where event=? and heat=? and lane=? and distance=?",
      params,
      uh -> {
        if (uh.succeeded()) {
          if (uh.result().getUpdated() == 1) {
            logger.info("Updated: {}", time);
          } else {
            client.updateWithParams(
              "insert into times(time,event,heat,lane,distance)values(?,?,?,?,?)",
              params,
              uh2 -> {
                if (uh2.succeeded()) {
                  logger.info("inserted:{}", time);
                } else {
                  logger.error("Could not insert:{}", time, uh2.cause());
                }
              });
          }
        } else {
          logger.error("Could not update:{}", time, uh.cause());
        }
      }
    );
  }
}
