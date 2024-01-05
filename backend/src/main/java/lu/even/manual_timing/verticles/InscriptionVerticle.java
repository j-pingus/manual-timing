package lu.even.manual_timing.verticles;

import io.vertx.core.json.Json;
import lu.even.manual_timing.domain.Inscription;
import lu.even.manual_timing.events.EventAction;
import lu.even.manual_timing.events.EventMessage;
import lu.even.manual_timing.events.EventTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class InscriptionVerticle extends AbstractTimingVerticle {
  private static final Logger logger = LoggerFactory.getLogger(InscriptionVerticle.class);
  Map<Integer, Map<Integer, List<Inscription>>> inscriptions = new HashMap<>();

  public InscriptionVerticle() {
    super(EventTypes.INSCRIPTION);
  }

  @Override
  protected Object onMessage(EventTypes eventType, EventMessage message) {
    try {
      return switch (message.action()) {
        case GET_BY_EVENT_LANE -> this.getByLane(message.eventId(), message.laneId());
        case GET_BY_EVENT_HEAT -> this.getByHeat(message.eventId(), message.heatId());
        case REPLACE_INSCRIPTIONS -> this.load(message.body(), message.eventId(), message.heatId());
        case POST -> this.save(message.body());
        case DUMP -> this.dumpMe();
        case LOAD -> this.loadMe();
        default -> null;
      };
    } catch (IOException e) {
      logger.error("Error happened", e);
      return null;
    }
  }

  private Object dumpMe() throws IOException{
    var data = this.inscriptions.values().stream()
      .flatMap(e->e.values().stream())
      .flatMap(l->l.stream())
      .collect(Collectors.toList());
    return dump(data,"inscriptions");
  }
  private Object loadMe()throws IOException{
    var data = load("inscriptions",Inscription[].class);
    Arrays.stream(data).forEach(i->this.save(i));
    return data;
  }
  private String load(String body, int event, int heat) {
    Inscription[] list = Json.decodeValue(body, Inscription[].class);
    get(event).put(heat, Arrays.asList(list));
    logger.info("loaded inscriptions: " + get(event, heat));
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
    return save(Json.decodeValue(inscriptionJson, Inscription.class));
  }
  private Object save(Inscription inscription) {
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
