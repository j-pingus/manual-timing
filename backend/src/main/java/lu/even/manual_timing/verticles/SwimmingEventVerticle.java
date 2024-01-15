package lu.even.manual_timing.verticles;

import io.vertx.core.eventbus.Message;
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
  protected void onMessage(EventTypes eventType, Message<EventMessage> message) {
    try {
      switch (message.body().action()) {
        case GET -> answer(message, getEvent(message.body().eventId()));
        case GET_ALL -> answer(message, this.events);
        case REPLACE_EVENTS -> {
          System.out.println(message.body().body());
          this.events = Arrays.asList(
            Json.decodeValue(message.body().body(), SwimmingEvent[].class));
          logger.info("replaced:{}", this.events);
          answer(message, "");
        }
        case PUT -> answer(message, replaceEvent(Json.decodeValue(message.body().body(), SwimmingEvent.class)));
        case DUMP -> answer(message, dump(this.events, "events"));
        case LOAD -> answer(message, this.events = Arrays.asList(load("events", SwimmingEvent[].class)));
      }
    } catch (IOException e) {
      logger.error("Error happened", e);
      message.fail(500, "Cannot perform I/O on server see logs");
    }
  }

  private Object getEvent(int event) {
    return this.events.stream().filter(e -> e.id() == event).findAny().orElse(
      new SwimmingEvent(-1, -1, false, "Not found", "99:99", "99/99/9999", new int[]{}));
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
