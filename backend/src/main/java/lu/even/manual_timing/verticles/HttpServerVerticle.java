package lu.even.manual_timing.verticles;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.EventBus;
import io.vertx.ext.bridge.PermittedOptions;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.ext.web.handler.sockjs.SockJSBridgeOptions;
import io.vertx.ext.web.handler.sockjs.SockJSHandler;
import lu.even.manual_timing.Events;
import lu.even.manual_timing.verticles.routes.PoolConfigRoute;
import lu.even.manual_timing.verticles.routes.RegistrationRoute;

public class HttpServerVerticle extends AbstractVerticle {
  private final int port;

  public HttpServerVerticle(int port) {
    this.port = port;
  }

  @Override
  public void start(Promise<Void> startPromise) {
    Router router = Router.router(vertx);
    SockJSHandler sockJSHandler = SockJSHandler.create(vertx);
    SockJSBridgeOptions socksOptions = new SockJSBridgeOptions()
      .addOutboundPermitted(new PermittedOptions().setAddress(Events.MESSAGE.getName()));
    sockJSHandler.bridge(socksOptions, event -> {
      System.out.println(event.type());
      System.out.println(event.getRawMessage());
    });
    router.route("message/*").handler(sockJSHandler);
    EventBus bus = vertx.eventBus();
    // Body handler for parsing request bodies
    router.route().handler(BodyHandler.create());
    router.route().handler(StaticHandler.create());
    PoolConfigRoute.route(router, bus);
    RegistrationRoute.route(router, bus);
    router.route().failureHandler(handler -> {
      System.out.println("Error hapenned during routing:" + handler.failure());

      handler.response().end(handler.failure() != null ? handler.failure().getMessage() : "Internal error");
    });
    vertx.createHttpServer().requestHandler(router).listen(port, http -> {
      if (http.succeeded()) {
        startPromise.complete();
        System.out.println("HTTP server started on port " + port);
      } else {
        startPromise.fail(http.cause());
      }
    });
  }
}
