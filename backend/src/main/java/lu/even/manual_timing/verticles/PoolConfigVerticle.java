package lu.even.manual_timing.verticles;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import lu.even.manual_timing.Event;
import lu.even.manual_timing.domain.PoolConfig;

public class PoolConfigVerticle  extends AbstractVerticle {
  @Override
  public void start() throws Exception {
    vertx.eventBus().consumer(Event.POOL_CONFIG.getName(),this::onMessage);
  }

  private <T> void onMessage(Message<T> tMessage) {
    JsonObject message = (JsonObject) tMessage.body();
    System.out.println(message);
    //FIXME: do not hardcode
    tMessage.reply(Json.encode(new PoolConfig(5)));
  }
}
