package lu.even.manual_timing.domain;

public record PoolConfig(int[] lanes, int length,int minTimeSeconds,boolean bothEndsTiming) {
}
