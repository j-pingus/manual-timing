package lu.even.manual_timing.verticles;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.Json;
import lu.even.manual_timing.events.EventAction;
import lu.even.manual_timing.events.EventMessage;
import lu.even.manual_timing.events.EventTypes;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

public abstract class AbstractTimingVerticle extends AbstractVerticle {
  protected static final Logger logger = LoggerFactory.getLogger(AbstractTimingVerticle.class);
  private EventTypes[] eventTypes;

  public AbstractTimingVerticle(EventTypes... eventTypes) {
    this.eventTypes = eventTypes;
  }

  @Override
  public void start() throws Exception {
    for (EventTypes eventType : eventTypes) {
      vertx.eventBus().<EventMessage>consumer(eventType.getName(), this::onMessage);
    }
    logger.info("verticle '{}' started", this.getClass().getSimpleName());
  }

  private void onMessage(Message<EventMessage> eventMessage) {
    Object response = this.onMessage(
      EventTypes.getByName(eventMessage.address()),
      eventMessage.body());
    if (response != null) {
      eventMessage.reply(Json.encode(response));
    } else {
      eventMessage.fail(500, "Unsuported operation");
    }
  }

  protected abstract Object onMessage(EventTypes eventType, EventMessage message);

  /**
   * This message will reach the frontend
   *
   * @param body the message you want to send can be simple string or complex object
   */
  void sendMessage(EventAction action, Object body) {
    this.sendMessage(action, body, -1, -1, -1);
  }

  void sendMessage(EventAction action, Object body, int event, int heat, int lane) {
    vertx.eventBus().publish(EventTypes.MESSAGE.getName(), new EventMessage(action, Json.encode(body), event, heat, lane));
  }
  String dump(Object object, String id) throws IOException {
    FileUtils.writeStringToFile(new File(id + ".json"), Json.encode(object), Charset.defaultCharset());
    return "dumped " + id;
  }

  <T> T load(String id, Class<T> classToDecode) throws IOException {
    String jsonData = FileUtils.readFileToString(new File(id + ".json"), Charset.defaultCharset());
    return Json.decodeValue(jsonData, classToDecode);
  }

}
