package lu.even;

import lu.even.manual_timing.domain.PoolConfig;

public class Config {
  public Config(){}

  public PoolConfig getPool() {
    return pool;
  }

  public void setPool(PoolConfig pool) {
    this.pool = pool;
  }

  public RemoteServerConfig getMeetmanager() {
    return meetmanager;
  }

  public void setMeetmanager(RemoteServerConfig meetmanager) {
    this.meetmanager = meetmanager;
  }

  public int getPort() {
    return port;
  }

  public void setPort(int port) {
    this.port = port;
  }

  public PoolConfig pool= new PoolConfig(new int[]{1, 2, 3, 4, 5}, 25);
  public RemoteServerConfig meetmanager = new RemoteServerConfig();
  public RemoteServerConfig timingApplication = new RemoteServerConfig();
  public int port =8765;
}

