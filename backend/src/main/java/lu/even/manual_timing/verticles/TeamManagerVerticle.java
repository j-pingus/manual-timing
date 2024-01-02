package lu.even.manual_timing.verticles;

import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.http.HttpClientResponse;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.Json;
import lu.even.manual_timing.domain.SwimmingEvent;
import lu.even.manual_timing.events.EventAction;
import lu.even.manual_timing.events.EventMessage;
import lu.even.manual_timing.events.EventTypes;
import lu.even.meet_manager.domain.Event;
import lu.even.meet_manager.domain.Events;
import lu.even.meet_manager.domain.Gender;
import lu.even.meet_manager.domain.Stroke;

import java.util.Comparator;
import java.util.stream.Collectors;

public class TeamManagerVerticle extends AbstractTimingVerticle {
    private final String host;
    private final int port;
    private HttpClient client;
    public TeamManagerVerticle(String host, int port) {
        super(EventTypes.MEET_MANAGER);
        this.host = host;
        this.port = port;
    }

    @Override
    public void start() throws Exception {
        super.start();
        var options = new HttpClientOptions()
                .setDefaultHost(host)
                .setDefaultPort(port);
        client = vertx.createHttpClient(options);
    }

    @Override
    protected Object onMessage(EventTypes eventType, EventMessage message) {
        return switch (message.action()) {
            case LOAD_EVENTS -> loadEvents();
            default -> null;
        };
    }

    private String loadEvents() {
        client.request(HttpMethod.GET, "/events")
                .compose(req -> req.send().compose(HttpClientResponse::body))
                .onSuccess(hc -> {
                    Events events = Json.decodeValue(hc, Events.class);
                    var swimmingEvents = events.getDynamicValues().values().stream()
                            .filter(e->e.heats().size()>0)
                            .map(e->new SwimmingEvent(
                                    toNumber(e.number()),
                                    e.heats().size(),
                                    e.isrelay(),
                                    describe(e)
                            ))
                            .sorted(Comparator.comparingInt(SwimmingEvent::id))
                            .collect(Collectors.toList());
                    vertx.eventBus().publish(
                            EventTypes.EVENT.getName(),
                            new EventMessage(EventAction.REPLACE_EVENTS,Json.encode(swimmingEvents),-1,-1,-1));
                });
        return "";
    }

    private String describe(Event e) {
        return e.distance()+ " "+
                decodeStroke(e.stroke())+" "+
                decodeGender(e.gender());
    }

    private String decodeGender(String gender) {
        return Gender.getByCode(gender).getDescription();
    }

    private String decodeStroke(String stroke) {
        return Stroke.getByCode(stroke).getDescription();
    }

    private int toNumber(String number) {
        if(number!=null)
            return Integer.parseInt(number);
        return -1;
    }
}
