package lu.even.manual_timing.verticles;

import io.vertx.core.eventbus.Message;
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
  protected void onMessage(EventTypes eventType, Message<EventMessage> message) {
    try {
      switch (message.body().action()) {
        case GET_BY_EVENT_LANE -> answer(message,this.getByLane(message.body().eventId(), message.body().laneId()));
        case GET_BY_EVENT_HEAT -> answer(message,this.getByHeat(message.body().eventId(), message.body().heatId()));
        case REPLACE -> answer(message,this.load(message.body().body(), message.body().eventId(), message.body().heatId()));
        case POST -> answer(message,this.save(message.body().body()));
        case DUMP -> answer(message,this.dumpMe());
        case LOAD -> answer(message,this.loadMe());
      }
    } catch (IOException e) {
      logger.error("Error happened", e);
      message.fail(500,"Cannot perform I/O see server logs");
    }
  }


  private Object dumpMe() throws IOException{
    var data = this.inscriptions.values().stream()
      .flatMap(e->e.values().stream())
      .flatMap(Collection::stream)
      .collect(Collectors.toList());
    return dump(data,"inscriptions");
  }
  private Object loadMe()throws IOException{
    var data = load("inscriptions",Inscription[].class);
    Arrays.stream(data).forEach(this::save);
    return data;
  }
  private String load(String body, int event, int heat) {
    Inscription[] list = Json.decodeValue(body, Inscription[].class);
    get(event).put(heat, Arrays.asList(list));
    logger.info("loaded inscriptions: " + get(event, heat));
    sendMessage(EventAction.REFRESH,"inscriptions",event,heat,-1,-1);
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
    var inscriptions = get(inscription.event(), inscription.heat());
    inscriptions.remove(inscription);
    inscriptions.add(inscription);
    this.sendMessage(EventAction.REFRESH, "inscriptions", inscription.event(), inscription.heat(), inscription.lane(),-1);
    return "";
  }

  private List<Inscription> getByHeat(int event, int heat) {
    return get(event, heat);
  }

  private List<Inscription> getByLane(int event, int lane) {
    return get(event).entrySet()
      .stream()
      .flatMap(e -> e.getValue().stream())
      .filter(i -> i.lane() == lane)
      .collect(Collectors.toList());
  }
}
