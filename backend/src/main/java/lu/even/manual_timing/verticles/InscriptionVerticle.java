package lu.even.manual_timing.verticles;

import io.vertx.core.json.Json;
import lu.even.manual_timing.domain.Inscription;
import lu.even.manual_timing.events.EventAction;
import lu.even.manual_timing.events.EventMessage;
import lu.even.manual_timing.events.EventTypes;

import java.util.*;
import java.util.stream.Collectors;

public class InscriptionVerticle extends AbstractTimingVerticle {
    Map<Integer, Map<Integer, List<Inscription>>> inscriptions = new HashMap<>();

    public InscriptionVerticle() {
        super(EventTypes.INSCRIPTION);
    }

    @Override
    protected Object onMessage(EventTypes eventType, EventMessage message) {
        return switch (message.action()) {
            case GET_BY_EVENT_LANE -> this.getByLane(message.eventId(), message.laneId());
            case GET_BY_EVENT_HEAT -> this.getByHeat(message.eventId(), message.heatId());
            case REPLACE_INSCRIPTIONS -> this.load(message.body(), message.eventId(), message.heatId());
            case POST -> this.save(message.body());
            default -> null;
        };
    }

    private String load(String body, int event, int heat) {
        Inscription[] list = Json.decodeValue(body, Inscription[].class);
        get(event).put(heat, Arrays.asList(list));
        System.out.println("loaded inscriptions: " + get(event, heat));
        return "";
    }

    private List<Inscription> get(int eventId, int heatId) {
        var event = get(eventId);
        if (!event.containsKey(heatId)) {
            event.put(heatId, new ArrayList<>());
        }
        return event.get(heatId);
    }

    private Map<Integer, List<Inscription>> get(int eventId) {
        if (!inscriptions.containsKey(eventId)) {
            inscriptions.put(eventId, new HashMap<>());
        }
        return inscriptions.get(eventId);
    }

    private Object save(String inscriptionJson) {
        Inscription inscription = Json.decodeValue(inscriptionJson, Inscription.class);
        var inscriptions = get(inscription.getEvent(), inscription.getHeat());
        inscriptions.remove(inscription);
        inscriptions.add(inscription);
        this.sendMessage(EventAction.REFRESH_INSCRIPTIONS, "", inscription.getEvent(), inscription.getHeat(), inscription.getLane());
        return "";
    }

    private List<Inscription> getByHeat(int event, int heat) {
        return get(event, heat);
    }

    private List<Inscription> getByLane(int event, int lane) {
        return get(event).entrySet()
                .stream()
                .flatMap(e -> e.getValue().stream())
                .filter(i -> i.getLane() == lane)
                .collect(Collectors.toList());
    }
}
