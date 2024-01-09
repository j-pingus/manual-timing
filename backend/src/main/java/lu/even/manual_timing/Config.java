package lu.even.manual_timing;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lu.even.manual_timing.domain.PoolConfig;
import lu.even.manual_timing.domain.SslConfig;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Config(int port, String secret, String databaseUrl, PoolConfig pool, SslConfig ssl) {
}

