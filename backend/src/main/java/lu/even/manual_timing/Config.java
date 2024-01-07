package lu.even.manual_timing;

import lu.even.RemoteServerConfig;
import lu.even.manual_timing.domain.PoolConfig;

public record Config(int port,String databaseUrl,PoolConfig pool) { }

