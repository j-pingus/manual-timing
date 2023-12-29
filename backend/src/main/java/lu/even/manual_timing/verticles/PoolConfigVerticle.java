package lu.even.manual_timing.verticles;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import lu.even.manual_timing.Events;
import lu.even.manual_timing.domain.PoolConfig;

public class PoolConfigVerticle  extends AbstractVerticle {
  @Override
  public void start(){
    vertx.eventBus().consumer(Events.POOL_CONFIG.getName(),this::onMessage);
    System.out.println("Pool config verticle started");
  }

  private <T> void onMessage(Message<T> tMessage) {
    JsonObject message = (JsonObject) tMessage.body();
    System.out.println(message);
    //FIXME: do not hardcode
    tMessage.reply(Json.encode(new PoolConfig(new int[]{0,1,2,3,4},25)));
  }
}
