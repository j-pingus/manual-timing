package lu.even.manual_timing.verticles;

import io.vertx.core.eventbus.Message;
import io.vertx.core.json.Json;
import lu.even.manual_timing.domain.ManualTime;
import lu.even.manual_timing.domain.User;
import lu.even.manual_timing.events.EventAction;
import lu.even.manual_timing.events.EventMessage;
import lu.even.manual_timing.events.EventTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ManualTimeVerticle extends AbstractTimingVerticle {
  List<ManualTime> times;
  private static final Logger logger = LoggerFactory.getLogger(ManualTimeVerticle.class);

  public ManualTimeVerticle() {
    super(EventTypes.MANUAL_TIME);
    times = new ArrayList<>();
  }

  @Override
  protected void onMessage(EventTypes eventType, Message<EventMessage> message) {
    switch (message.body().action()) {
      case POST -> save(message);
      case REPLACE_TIMES -> answer(message, loadTimes(message.body().body()));
      case GET_BY_EVENT_LANE -> answer(message, getByEventLane(message.body().eventId(), message.body().laneId()));
      case GET_BY_EVENT_HEAT -> answer(message, getByEventHeat(message.body().eventId(), message.body().heatId()));
    }
  }


  private Object loadTimes(String body) {
    this.times = new ArrayList<>();
    this.times.addAll(Arrays.asList(Json.decodeValue(body, ManualTime[].class)));
    return "OK";
  }

  private List<ManualTime> getByEventLane(int event, int lane) {
    return times.stream().filter(
      t -> t.getLane() == lane
        && t.getEvent() == event
    ).collect(Collectors.toList());
  }

  private List<ManualTime> getByEventHeat(int event, int heat) {
    return times.stream().filter(
      t -> t.getHeat() == heat
        && t.getEvent() == event
    ).collect(Collectors.toList());
  }

  private void save(Message<EventMessage> message) {
    vertx.eventBus().<String>request(EventTypes.REGISTER.getName(), new EventMessage(EventAction.GET, null, -1, -1, -1, message.body().authorization())
      , messageAsyncResult -> {
        if (messageAsyncResult.succeeded()) {
          User user = Json.decodeValue(messageAsyncResult.result().body(), User.class);
          if ("referee".equals(user.getRole())) {
            ManualTime manualTime = Json.decodeValue(message.body().body(), ManualTime.class);
            times.remove(manualTime);
            times.add(manualTime);
            logger.info("time:{}", manualTime);
            sendMessage(EventTypes.DATABASE, EventAction.SAVE_TIME, manualTime, manualTime.getEvent(), manualTime.getHeat(), manualTime.getLane(), message.body().authorization());
            sendMessage(EventAction.REFRESH_TIMES, manualTime.getTime(), manualTime.getEvent(), manualTime.getHeat(), manualTime.getLane());
            message.reply("ok");
            return;
          }
        }
        message.fail(401, "unauthorized");
      }
    );
  }
}
