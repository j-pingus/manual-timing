package lu.even.meet_manager.verticles;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.Message;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.http.HttpClientResponse;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.Json;
import lu.even.RemoteServerConfig;
import lu.even.manual_timing.domain.Inscription;
import lu.even.manual_timing.domain.SwimmingEvent;
import lu.even.meet_manager.domain.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;
import java.util.Date;
import java.util.stream.Collectors;

public class MeetManagerVerticle extends AbstractVerticle {
  public static final String EVENT_TYPE = "meet.manager";
  public static final String EVENT_TYPE_HEAT = "meet.manager.heat";
  protected static final Logger logger = LoggerFactory.getLogger(MeetManagerVerticle.class);
  RemoteServerConfig meetManagerConfig;

  public MeetManagerVerticle(RemoteServerConfig meetManagerConfig, RemoteServerConfig manualTimeConfig) {
    this.meetManagerConfig = meetManagerConfig;
    this.manualTimeConfig = manualTimeConfig;
  }

  RemoteServerConfig manualTimeConfig;
  private HttpClient meetManagerClient;
  private HttpClient manualTimeClient;


  public void start() throws Exception {
    vertx.eventBus().consumer(EVENT_TYPE, this::onEventMessage);
    vertx.eventBus().consumer(EVENT_TYPE_HEAT, this::onHeatMessage);
    logger.info("verticle '{}' started", this);
    var options = new HttpClientOptions()
      .setDefaultHost(meetManagerConfig.host())
      .setDefaultPort(meetManagerConfig.port());
    meetManagerClient = vertx.createHttpClient(options);
    var options2 = new HttpClientOptions()
      .setDefaultHost(manualTimeConfig.host())
      .setDefaultPort(manualTimeConfig.port());
    manualTimeClient = vertx.createHttpClient(options2);
  }

  private void onEventMessage(Message<String> stringMessage) {
    Object response = this.loadEvents();
    if (response != null) {
      stringMessage.reply(Json.encode(response));
    } else {
      stringMessage.fail(500, "Failed");
    }
  }
  private void onHeatMessage(Message<String> stringMessage) {
    Object response = this.loadHeat(stringMessage.body());
    if (response != null) {
      stringMessage.reply(Json.encode(response));
    } else {
      stringMessage.fail(500, "Failed");
    }
  }

  @Override
  public String toString() {
    return "MeetManagerVerticle{" +
      "meetManagerConfig=" + meetManagerConfig +
      ", manualTimeConfig=" + manualTimeConfig +
      '}';
  }

  private String loadHeat(String eventHeat) {
    String[] codes =eventHeat.split("_");
    return this.loadHeat(Integer.parseInt(codes[0]),Integer.parseInt(codes[1]));
  }
  private String loadHeat(int eventId, int heatId) {
    meetManagerClient.request(HttpMethod.GET, "/heats/" + eventId + "/" + heatId)
      .compose(req -> req.send().compose(HttpClientResponse::body))
      .onSuccess(hc -> {
        var heat = Json.decodeValue(hc, HeatDetails.class);
        var inscriptions = heat.entries().stream()
          .map(e -> new Inscription(eventId, heatId, e.lane(), e.nametext()))
          .collect(Collectors.toList());
        logger.info("Sending inscriptions:{}",inscriptions);
        //post data
        manualTimeClient.request(HttpMethod.POST,"/api/inscriptions/"+eventId+"/heat/"+heatId)
          .compose(req->req.send(Json.encode(inscriptions)));
      })
      .onFailure(Throwable::printStackTrace);
    return "";
  }

  private String loadEvents() {
    meetManagerClient.request(HttpMethod.GET, "/events")
      .compose(req -> req.send().compose(HttpClientResponse::body))
      .onSuccess(hc -> {
        Events events = Json.decodeValue(hc, Events.class);
        var swimmingEvents = events.getDynamicValues().values().stream()
          .filter(e -> !e.heats().isEmpty())
          .map(e -> new SwimmingEvent(
            toNumber(e.number()),
            e.heats().size(),
            e.isrelay(),
            describe(e)
          ))
          .sorted(Comparator.comparingInt(SwimmingEvent::id))
          .collect(Collectors.toList());
        logger.info("Sending events:{}",swimmingEvents);
        manualTimeClient.request(HttpMethod.POST,"/api/events")
          .compose(req->req.send(Json.encode(swimmingEvents)));

        swimmingEvents.forEach(event -> {
          for (int heat = 1; heat <= event.heats(); heat++) {
            vertx.eventBus().publish(
              EVENT_TYPE_HEAT,
              event.id() + "_" + heat
            );
          }
        });

      })
      .onFailure(Throwable::printStackTrace);
    return "events loaded "+new Date();
  }

  private String describe(Event e) {
    return
      (e.isrelay() ? "Relay " : "") +
        e.distance() + " " +
        decodeStroke(e.stroke()) + " " +
        decodeGender(e.gender());
  }

  private String decodeGender(String gender) {
    return Gender.getByCode(gender).getDescription();
  }

  private String decodeStroke(String stroke) {
    return Stroke.getByCode(stroke).getDescription();
  }

  private int toNumber(String number) {
    if (number != null)
      return Integer.parseInt(number);
    return -1;
  }
}
