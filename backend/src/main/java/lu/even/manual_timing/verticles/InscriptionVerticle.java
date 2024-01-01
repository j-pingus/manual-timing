package lu.even.manual_timing.verticles;

import io.vertx.core.json.Json;
import lu.even.manual_timing.domain.Inscription;
import lu.even.manual_timing.events.EventAction;
import lu.even.manual_timing.events.EventMessage;
import lu.even.manual_timing.events.EventTypes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class InscriptionVerticle extends AbstractTimingVerticle {
    //FIXME: replace with database
    private static final Inscription[] DUMMY = {
            new Inscription(1, 1, 2, "nageur a"),
            new Inscription(1, 1, 3, "nageur b"),
            new Inscription(1, 1, 4, "nageur c"),
            new Inscription(1, 2, 1, "nageur anticonstitutionellement Jacques-Henry"),
            new Inscription(1, 2, 2, "nageur e"),
            new Inscription(1, 2, 3, "nageur f"),
            new Inscription(1, 2, 4, "nageur g"),
            new Inscription(1, 2, 5, "nageur h"),
            new Inscription(1, 3, 1, "nageur i"),
            new Inscription(1, 3, 2, "nageur j"),
            new Inscription(1, 3, 3, "nageur k"),
            new Inscription(1, 3, 4, "nageur l"),
            new Inscription(1, 3, 5, "nageur m"),
            new Inscription(1, 4, 1, "nageur n"),
            new Inscription(1, 4, 2, "nageur o"),
            new Inscription(1, 4, 3, "nageur p"),
            new Inscription(1, 4, 4, "nageur q"),
            new Inscription(1, 4, 5, "nageur r"),
            new Inscription(1, 5, 1, "nageur s"),
            new Inscription(1, 5, 2, "nageur t"),
            new Inscription(1, 5, 3, "nageur u"),
            new Inscription(1, 5, 4, "nageur v"),
            new Inscription(1, 5, 5, "nageur w"),
            new Inscription(2, 1, 1, "<<test event 2 lane 1>>"),
            new Inscription(3, 1, 1, "<<test event 3 lane 1>>"),
            new Inscription(4, 1, 1, "<<test event 4 lane 1>>"),
    };
    List<Inscription> inscriptions = new ArrayList<>(Arrays.asList(DUMMY));

    public InscriptionVerticle() {
        super(EventTypes.INSCRIPTION);
    }

    @Override
    protected Object onMessage(EventTypes eventType, EventMessage message) {
        return switch (message.action()) {
            case GET_BY_EVENT_LANE -> this.getByLane(message.eventId(), message.laneId());
            case GET_BY_EVENT_HEAT -> this.getByHeat(message.eventId(), message.heatId());
            case POST -> this.save(message.body());
            default -> null;
        };
    }

    private Object save(String inscriptionJson) {
        Inscription inscription = Json.decodeValue(inscriptionJson, Inscription.class);
        this.inscriptions.remove(inscription);
        this.inscriptions.add(inscription);
        this.sendMessage(EventAction.REFRESH_INSCRIPTIONS, "", inscription.getEvent(), inscription.getHeat(), inscription.getLane());
        return "";
    }

    private List<Inscription> getByHeat(int event, int heat) {
        return this.inscriptions.stream().filter(
                i -> i.getHeat() == heat && i.getEvent() == event
        ).collect(Collectors.toList());
    }

    private List<Inscription> getByLane(int event, int lane) {
        return this.inscriptions.stream().filter(
                i -> i.getLane() == lane && i.getEvent() == event
        ).collect(Collectors.toList());
    }
}
