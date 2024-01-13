package lu.even.meet_manager.verticles;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.Message;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.http.HttpClientResponse;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.Json;
import lu.even.RemoteServerConfig;
import lu.even.manual_timing.domain.Inscription;
import lu.even.manual_timing.domain.PoolConfig;
import lu.even.manual_timing.domain.SwimmingEvent;
import lu.even.meet_manager.domain.*;
import lu.even.meet_manager.utils.DistanceConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

public class MeetManagerVerticle extends AbstractVerticle {
  public static final String EVENT_TYPE = "meet.manager";
  public static final String EVENT_TYPE_HEAT = "meet.manager.heat";
  protected static final Logger logger = LoggerFactory.getLogger(MeetManagerVerticle.class);
  RemoteServerConfig meetManagerConfig;
  private PoolConfig poolConfig;

  public MeetManagerVerticle(RemoteServerConfig meetManagerConfig, RemoteServerConfig manualTimeConfig) {
    this.meetManagerConfig = meetManagerConfig;
    this.manualTimeConfig = manualTimeConfig;
  }

  RemoteServerConfig manualTimeConfig;
  private HttpClient meetManagerClient;
  private HttpClient manualTimeClient;

  interface OnSuccess {
    void proceed(PoolConfig config);
  }

  interface OnFailure {
    void fail(Throwable exception);
  }

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    manualTimeClient = createClient(manualTimeConfig);
    meetManagerClient = createClient(meetManagerConfig);
    retrieveOptions((config) -> {
      this.poolConfig = config;
      vertx.eventBus().consumer(EVENT_TYPE, this::onEventMessage);
      vertx.eventBus().consumer(EVENT_TYPE_HEAT, this::onEventHeatMessage);
      logger.info("verticle '{}' started", this);
      startPromise.complete();
    }, startPromise::fail);
  }

  private HttpClient createClient(RemoteServerConfig httpConfig) {
    var httpOptions = new HttpClientOptions()
      .setDefaultHost(httpConfig.host())
      .setDefaultPort(httpConfig.port())
      .setSsl(httpConfig.ssl());
    return vertx.createHttpClient(httpOptions);
  }

  private void retrieveOptions(OnSuccess onSuccessCallback, OnFailure onFailureCallback) {
    manualTimeClient.request(HttpMethod.GET, "/api/poolconfig")
      .compose(req -> req.send().compose(HttpClientResponse::body))
      .onSuccess(hc -> {
        var poolConfig = Json.decodeValue(hc, PoolConfig.class);
        onSuccessCallback.proceed(poolConfig);
      })
      .onFailure(onFailureCallback::fail)
    ;
  }

  private void onEventMessage(Message<String> stringMessage) {
    Object response = this.loadEvents();
    if (response != null) {
      stringMessage.reply(Json.encode(response));
    } else {
      stringMessage.fail(500, "Failed");
    }
  }

  private void onEventHeatMessage(Message<EventHeatMessage> eventHeatMessage) {
    Object response = this.loadEventHeat(eventHeatMessage.body());
    if (response != null) {
      eventHeatMessage.reply(Json.encode(response));
    } else {
      eventHeatMessage.fail(500, "Failed");
    }
  }

  @Override
  public String toString() {
    return "MeetManagerVerticle{" +
      "meetManagerConfig=" + meetManagerConfig +
      ", manualTimeConfig=" + manualTimeConfig +
      '}';
  }

  private String loadEventHeat(EventHeatMessage eventHeatMessage) {
    meetManagerClient.request(HttpMethod.GET, "/heats/" + eventHeatMessage.event + "/" + eventHeatMessage.heat)
      .compose(req -> req.send().compose(HttpClientResponse::body))
      .onSuccess(hc -> {
        var heat = Json.decodeValue(hc, HeatDetails.class);
        var inscriptions = heat.entries().stream()
          .map(e -> new Inscription(
            eventHeatMessage.event, eventHeatMessage.heat, e.lane(), e.nametext(), e.nation(), e.entrytime(), e.clubcode(), e.agetext()))
          .collect(Collectors.toList());
        loadIfDifferent(inscriptions, eventHeatMessage);
      })
      .onFailure(Throwable::printStackTrace);
    return "";
  }

  Map<EventHeatMessage, List<Inscription>> cacheInscriptions = new HashMap<>();

  private void loadIfDifferent(List<Inscription> inscriptions, EventHeatMessage eventHeatMessage) {

    if (cacheInscriptions.containsKey(eventHeatMessage) && cacheInscriptions.get(eventHeatMessage).equals(inscriptions)) {
      logger.info("Not sending {} same as before",eventHeatMessage);
      return;
    }
    logger.info("Sending inscriptions:{}", inscriptions);
    //post data
    manualTimeClient.request(HttpMethod.POST, "/api/inscriptions/" + eventHeatMessage.event + "/heat/" + eventHeatMessage.heat)
      .compose(req -> req.send(Json.encode(inscriptions))
        .onSuccess(hc -> {
          cacheInscriptions.put(eventHeatMessage, inscriptions);
        })
      );

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
            describe(e),
            e.time(),
            e.date(),
            DistanceConverter.getIntermediates(e.distance(), this.poolConfig.length(),this.poolConfig.bothEndsTiming())
          ))
          .sorted(Comparator.comparingInt(SwimmingEvent::id))
          .collect(Collectors.toList());
        loadIfDifferent(swimmingEvents);

        swimmingEvents.forEach(event -> {
          for (int heat = 1; heat <= event.heats(); heat++) {
            vertx.eventBus().publish(
              EVENT_TYPE_HEAT,
              new EventHeatMessage(event.id(), heat)
            );
          }
        });

      })
      .onFailure(Throwable::printStackTrace);
    return "events loaded " + new Date();
  }
  private List<SwimmingEvent> eventCache;
  private void loadIfDifferent(List<SwimmingEvent> swimmingEvents) {
    if(eventCache!=null && eventCache.equals(swimmingEvents)){
      logger.info("Not sending events as they are the same");
      return;
    }
    logger.info("Sending events:{}", swimmingEvents);
    manualTimeClient.request(HttpMethod.POST, "/api/events")
      .compose(req -> req.send(Json.encode(swimmingEvents))
        .onSuccess(hc->this.eventCache=swimmingEvents)
      );
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

  record EventHeatMessage(int event, int heat) implements Serializable {
  }

}
