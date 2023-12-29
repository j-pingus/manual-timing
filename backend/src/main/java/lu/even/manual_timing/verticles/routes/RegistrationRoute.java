package lu.even.manual_timing.verticles.routes;

import io.vertx.core.eventbus.EventBus;
import io.vertx.ext.web.Router;
import lu.even.manual_timing.Events;

public class RegistrationRoute {
  public static void route(Router router, EventBus bus) {
    router.post("/api/registration").handler(
      rc -> bus.<String>request(Events.REGISTER.getName(), rc.body().asString(), reply -> {
        if (reply.succeeded()) {
          rc.response().end(reply.result().body());
        } else {
          rc.fail(500);
        }
      })
    );

  }
}
