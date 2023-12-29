package lu.even.manual_timing.verticles.routes;

import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import lu.even.manual_timing.Events;

public class PoolConfigRoute {
  public static void route(Router router, EventBus bus){
    router.get("/api/poolconfig").handler(
      rc-> bus.<String>request(Events.POOL_CONFIG.getName(),new JsonObject(), reply -> {
        if (reply.succeeded()) {
          rc.response().end(reply.result().body());
        } else {
          rc.fail(500);
        }
      })
    );

  }
}
