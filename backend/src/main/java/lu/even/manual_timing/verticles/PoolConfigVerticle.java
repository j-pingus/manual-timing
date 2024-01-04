package lu.even.manual_timing.verticles;

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
    protected PoolConfig onMessage(EventTypes eventTypes, EventMessage message) {
        return switch (message.action()) {
            case GET -> poolConfig;
            default -> null;
        };
    }


}
