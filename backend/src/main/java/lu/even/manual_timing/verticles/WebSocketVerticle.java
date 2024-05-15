package lu.even.manual_timing.verticles;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
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

public class WebSocketVerticle extends AbstractVerticle {
  protected static final Logger logger = LoggerFactory.getLogger(WebSocketVerticle.class);
  private ServerWebSocket ctx;
  private final int port;
  private EventBus bus;

  public WebSocketVerticle(int port) {
    this.port = port;
  }

  private void onMessage(Message<EventMessage> message) {
    switch (message.body().action()) {
      case REFRESH -> ctx.writeTextMessage(Json.encode(message.body()), result -> {
        if (result.succeeded()) {
          logger.info("Writing refresh in webSocket");
        }
      });
      case PUBLISH -> this.publishResults(message.body());
    }
    message.reply("ok");
  }

  private void publishResults(EventMessage message) {
    bus.<String>request(
      EventTypes.MANUAL_TIME.getName(),
      new EventMessage(GET_BY_EVENT_HEAT, message.body(),
        message.eventId(), message.heatId(), message.laneId(), message.distance(),
        message.authorization()),
      handler -> {
        logger.info("Preparing publish for websocket, websocket.closed={}", ctx.isClosed());

        ctx.writeTextMessage(
          Json.encode(
            new EventMessage(message.action(),
              handler.result().body(),
              message.eventId(), message.heatId(), message.laneId(), message.distance(),
              message.authorization())
          ), result -> {
            if (result.succeeded()) {
              logger.info("Writing publish in websocket");
            } else {
              logger.error("Could not write to the socket", result.cause());
            }
          }
        );
      }
    );
  }

  private void configure(HttpServer server) {
    bus.<EventMessage>consumer(
      EventTypes.SPLASH.getName(),
      this::onMessage
    );
    server.webSocketHandler((ctx) -> {
      logger.info("New client? closed:{}", ctx.isClosed());
      this.ctx = ctx;
      ctx.accept();
      ctx.closeHandler(h -> logger.error("Closing, Am I closed now?:{}", ctx.isClosed()));
      ctx.endHandler(h -> logger.error("Ending, Am I closed now?:{}", ctx.isClosed()));
    });
  }

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    HttpServer httpServer = vertx.createHttpServer();
    this.bus = vertx.eventBus();
    configure(httpServer);
    httpServer.listen(port, http -> {

      if (http.succeeded()) {
        startPromise.complete();
        logger.info("Websocket server started on port:{} ", port);
      } else {
        startPromise.fail(http.cause());
      }
    });
    super.start(startPromise);
  }
}
