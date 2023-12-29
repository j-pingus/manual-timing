package lu.even.manual_timing.verticles;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import lu.even.manual_timing.Event;
import lu.even.manual_timing.domain.PoolConfig;

public class HttpServerVerticle extends AbstractVerticle {
  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    Router router = Router.router(vertx);

    // Body handler for parsing request bodies
    router.route().handler(BodyHandler.create());

    router.get("/poolconfig").handler(
      rc->{
        vertx.eventBus().<String>request(Event.POOL_CONFIG.getName(),new JsonObject(), reply -> {
          if (reply.succeeded()) {
            rc.response().end(reply.result().body());
          } else {
            rc.fail(500);
          }
        });
      }
    );
    router.route().failureHandler(handler->{
      System.out.println("Error hapenned during routing:"+handler.failure());

      handler.response().end(handler.failure()!=null?handler.failure().getMessage():"Internal error");
    });
    vertx.createHttpServer().requestHandler(router).listen(8888, http -> {
      if (http.succeeded()) {
        startPromise.complete();
        System.out.println("HTTP server started on port 8888");
      } else {
        startPromise.fail(http.cause());
      }
    });
  }
}
