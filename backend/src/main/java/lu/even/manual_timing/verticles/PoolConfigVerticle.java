package lu.even.manual_timing.verticles;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.Json;
import lu.even.manual_timing.domain.PoolConfig;
import lu.even.manual_timing.events.EventAction;
import lu.even.manual_timing.events.EventMessage;
import lu.even.manual_timing.events.EventTypes;

public class PoolConfigVerticle extends AbstractTimingVerticle<PoolConfig> {
    private PoolConfig poolConfig;

    public PoolConfigVerticle() {
        super(EventTypes.POOL_CONFIG);
        //Default pool configuration
        this.poolConfig = new PoolConfig(new int[]{0, 1, 2, 3, 4}, 25);
    }

    @Override
    protected PoolConfig onMessage(EventMessage message) {
        return switch (message.action()){
            case GET -> poolConfig;
            default -> null;
        };
    }


}
