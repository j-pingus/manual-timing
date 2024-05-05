package lu.even.manual_timing.verticles;

import io.vertx.core.AsyncResult;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.ServerWebSocket;
import io.vertx.core.json.Json;
import lu.even.manual_timing.events.EventMessage;
import lu.even.manual_timing.events.EventTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static lu.even.manual_timing.events.EventAction.GET_BY_EVENT_HEAT;

public class WebsocketServer {
  protected static final Logger logger = LoggerFactory.getLogger(WebsocketServer.class);
  private final EventBus bus;

  public WebsocketServer(Vertx vertx) {
    this.bus = vertx.eventBus();
  }

  private void onMessage(Message<EventMessage> message, ServerWebSocket ctx) {
    switch (message.body().action()) {
      case REFRESH -> {
        logger.info("Writing refresh in webSocket");
        ctx.writeTextMessage(Json.encode(message.body()), AsyncResult::result);
      }
      case PUBLISH -> this.publishResults(message.body(), ctx);
    }
    message.reply("ok");
  }

  private void publishResults(EventMessage message, ServerWebSocket ctx) {
    bus.<String>request(
      EventTypes.MANUAL_TIME.getName(),
      new EventMessage(GET_BY_EVENT_HEAT, message.body(),
        message.eventId(), message.heatId(), message.laneId(), message.distance(),
        message.authorization()),
      handler -> {
        logger.info("Writing publish in websocket");
        ctx.writeTextMessage(
          Json.encode(
            new EventMessage(message.action(),
              handler.result().body(),
              message.eventId(), message.heatId(), message.laneId(), message.distance(),
              message.authorization())
          ), AsyncResult::result
        );
      }
    );
  }

  public void configure(HttpServer server) {
    server.webSocketHandler((ctx) -> bus.<EventMessage>consumer(
      EventTypes.SPLASH.getName(),
      handler -> this.onMessage(handler, ctx)
    ));
  }
}
