package lu.even.meet_manager.verticles;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.EventBus;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.StaticHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpServerVerticle extends AbstractVerticle {
  private static final Logger logger = LoggerFactory.getLogger(HttpServerVerticle.class);
  private final int port;
  private Router router;
  private EventBus bus;

  public HttpServerVerticle(int port) {
    this.port = port;
  }

  @Override
  public void start(Promise<Void> startPromise) {
    router = Router.router(vertx);
    bus = vertx.eventBus();
    this.routeGet("/api/load",MeetManagerVerticle.EVENT_TYPE);
    //this.routeGet("/api/stress",StressManagerVerticle.EVENT_TYPE);
    // Body handler for parsing request bodies
    router.route().handler(BodyHandler.create());
    //Handle static content in webcontent resource folder
    router.route().handler(StaticHandler.create());

    router.route().failureHandler(handler -> {
      logger.error("Error happened during routing", handler.failure());
      handler.response().end(handler.failure() != null ? handler.failure().getMessage() : "Internal error");
    });
    vertx.createHttpServer().requestHandler(router).listen(port, http -> {
      if (http.succeeded()) {
        startPromise.complete();
        logger.info("HTTP server started on port:{} ", port);
      } else {
        startPromise.fail(http.cause());
      }
    });
  }
  public void routeGet(String url, String eventType) {
    router.get(url).handler(getRoutingHandler(eventType));
  }
  private Handler<RoutingContext> getRoutingHandler(String eventType) {
    return event ->
      //
      bus.<String>request(
        eventType,
        event.body().asString(),
        reply -> {
          if (reply.succeeded()) {
            event.response().end(reply.result().body());
          } else {
            event.fail(500);
          }
        });
  }
}
