package lu.even.manual_timing.verticles;

import io.vertx.core.json.Json;
import lu.even.manual_timing.domain.SwimmingEvent;
import lu.even.manual_timing.events.EventMessage;
import lu.even.manual_timing.events.EventTypes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SwimmingEventVerticle extends AbstractTimingVerticle {

  private List<SwimmingEvent> events = new ArrayList<>();

  public SwimmingEventVerticle() {
    super(EventTypes.EVENT);
  }

  @Override
  protected Object onMessage(EventTypes eventType, EventMessage message) {
    try {
      return switch (message.action()) {
        case GET_ALL -> this.events;
        case REPLACE_EVENTS -> {
          this.events = Arrays.asList(
            Json.decodeValue(message.body(), SwimmingEvent[].class));
          logger.info("replaced:{}", this.events);
          yield "";
        }
        case PUT -> replaceEvent(Json.decodeValue(message.body(), SwimmingEvent.class));
        case DUMP -> dump(this.events, "events");
        case LOAD -> this.events = Arrays.asList(load("events", SwimmingEvent[].class));
        default -> null;
      };
    } catch (IOException e) {
      logger.error("Error happened", e);
      return null;
    }
  }

  private Object replaceEvent(SwimmingEvent event) {
    int id = -1;
    for (int i = 0; i < events.size(); i++) {
      if (events.get(i).id() == event.id()) {
        id = i;
      }
    }
    if (id > -1) {
      events.remove(id);
      events.add(id, event);
    } else {
      events.add(event);
    }
    return "ok";
  }

}
