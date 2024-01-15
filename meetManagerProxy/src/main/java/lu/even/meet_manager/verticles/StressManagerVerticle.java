package lu.even.meet_manager.verticles;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.Message;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientResponse;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.Json;
import lu.even.RemoteServerConfig;
import lu.even.manual_timing.domain.Inscription;
import lu.even.manual_timing.domain.ManualTime;
import lu.even.manual_timing.domain.SwimmingEvent;
import lu.even.manual_timing.domain.User;
import lu.even.meet_manager.utils.HttpClientUtil;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;

public class StressManagerVerticle extends AbstractVerticle {
  public static final String EVENT_TYPE = "stress";
  public static final String EVENT_TYPE_FAKE = "stress.fake";
  private final RemoteServerConfig manualTimeConfig;
  private final String secret;
  private final Inscription[] inscriptions;
  private final SwimmingEvent[] events;
  int time = 1;
  private HttpClient client;
  private String userId;

  public StressManagerVerticle(RemoteServerConfig manualTimeConfig, String secret) throws IOException {
    this.manualTimeConfig = manualTimeConfig;
    this.secret = secret;
    this.inscriptions = Json.decodeValue(FileUtils.readFileToString(new File("inscriptions.json"), Charset.defaultCharset()), Inscription[].class);
    this.events = Json.decodeValue(FileUtils.readFileToString(new File("events.json"), Charset.defaultCharset()), SwimmingEvent[].class);
  }

  @Override
  public void start() throws Exception {
    super.start();
    this.client = HttpClientUtil.createClient(vertx, this.manualTimeConfig);
    vertx.eventBus().consumer(EVENT_TYPE, this::handleMessage);
    vertx.eventBus().consumer(EVENT_TYPE_FAKE, this::handleFake);
  }

  private void handleFake(Message<Inscription> objectMessage) {
    Inscription inscription = objectMessage.body();
    Arrays.stream(getEvent(inscription.event()).distances()).forEach(d -> {
      var time = new ManualTime()
        .setTime("" + this.time++)
        .setDistance(d)
        .setEvent(inscription.event())
        .setLane(inscription.lane())
        .setHeat(inscription.heat());
      this.client.request(HttpMethod.POST, "/api/time")
        .compose(req -> {
          req
            .headers().add("Authorization", "Bearer :" + this.userId);
          return req.send(Json.encode(time)).compose(HttpClientResponse::body);
        }).onSuccess(
          hc -> System.out.println(hc.toString())
        );
    });
  }

  private SwimmingEvent getEvent(int event) {
    return Arrays.stream(events).filter(e -> e.id() == event).findFirst().orElseThrow();
  }

  private void handleMessage(Message<Object> objectMessage) {
    this.client.request(HttpMethod.POST, "/api/user")
      .compose(req -> req.send(Json.encode(new User()
        .setPassword(secret)
        .setName("StressLoader")
        .setLane(-1)
        .setRole("referee")
      )).compose(HttpClientResponse::body)).onSuccess(
        hc -> {
          var user = Json.decodeValue(hc, User.class);
          this.userId = user.getUuid();
          System.out.println(this.userId + " logged in");
          Arrays.stream(inscriptions).forEach(i -> {
            vertx.eventBus().publish(EVENT_TYPE_FAKE, i);
          });
        }
      );
    objectMessage.reply("Ok stress initiated");
  }
}
