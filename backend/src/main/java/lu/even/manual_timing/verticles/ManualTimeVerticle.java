package lu.even.manual_timing.verticles;

import io.vertx.core.json.Json;
import lu.even.manual_timing.domain.ManualTime;
import lu.even.manual_timing.events.EventAction;
import lu.even.manual_timing.events.EventMessage;
import lu.even.manual_timing.events.EventTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
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
  protected Object onMessage(EventTypes eventType, EventMessage message) {
    return switch (message.action()) {
      case POST -> save(message.body());
      case GET_BY_EVENT_LANE -> getByEventLane(message.eventId(), message.laneId());
      case GET_BY_EVENT_HEAT -> getByEventHeat(message.eventId(), message.heatId());
      default -> null;
    };
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

  private String save(String timingJson) {
    ManualTime manualTime = Json.decodeValue(timingJson, ManualTime.class);
    times.remove(manualTime);
    times.add(manualTime);
    logger.info("time:{}",manualTime);
    sendMessage(EventTypes.DATABASE, EventAction.SAVE_TIME,timingJson, manualTime.getEvent(), manualTime.getHeat(), manualTime.getLane());
    sendMessage(EventAction.REFRESH_TIMES, manualTime.getTime(), manualTime.getEvent(), manualTime.getHeat(), manualTime.getLane());
    return "";
  }
}
