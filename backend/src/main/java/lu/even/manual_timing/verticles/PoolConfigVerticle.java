package lu.even.manual_timing.verticles;

import io.vertx.core.eventbus.Message;
import lu.even.manual_timing.domain.PoolConfig;
import lu.even.manual_timing.events.EventMessage;
import lu.even.manual_timing.events.EventTypes;

public class PoolConfigVerticle extends AbstractTimingVerticle {
  private PoolConfig poolConfig;

  public PoolConfigVerticle(PoolConfig config) {
    super(EventTypes.POOL_CONFIG);
    //Default pool configuration
    this.poolConfig = config;
  }

  @Override
  protected void onMessage(EventTypes eventType, Message<EventMessage> message) {
    switch (message.body().action()) {
      case GET -> answer(message, poolConfig);
    }
    ;
  }


}
