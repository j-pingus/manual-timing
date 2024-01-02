package lu.even.manual_timing.verticles;

import io.vertx.core.json.Json;
import lu.even.manual_timing.domain.SwimmingEvent;
import lu.even.manual_timing.events.EventMessage;
import lu.even.manual_timing.events.EventTypes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SwimmingEventVerticle extends AbstractTimingVerticle {
    //FIXME: replace with DB
    private static SwimmingEvent[] DUMMY = new SwimmingEvent[]{
            new SwimmingEvent(1, 5, false, "50m backstrocke men"),
            new SwimmingEvent(2, 4, false, "50m backstrocke women"),
            new SwimmingEvent(3, 7, false, "50m freestyle men"),
            new SwimmingEvent(4, 2, false, "50m freestyle women"),
    };
    private List<SwimmingEvent> events = new ArrayList<>();

    public SwimmingEventVerticle() {
        super(EventTypes.EVENT);
        this.events.addAll(Arrays.asList(DUMMY));
    }

    @Override
    protected Object onMessage(EventTypes eventType, EventMessage message) {
        return switch (message.action()) {
            case GET_ALL -> this.events;
            case REPLACE_EVENTS -> {this.events = Json.<List>decodeValue(message.body(), List.class);
                System.out.println("replaced:"+this.events);
                yield "";
            }
            case PUT -> replaceEvent(Json.decodeValue(message.body(), SwimmingEvent.class));
            default -> null;
        };
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
