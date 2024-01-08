package lu.even.manual_timing;

import lu.even.manual_timing.domain.PoolConfig;

public record Config(int port,String secret, String databaseUrl, PoolConfig pool) {
}

