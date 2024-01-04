package lu.even.manual_timing;

import lu.even.manual_timing.domain.PoolConfig;

public class Config {
  public Config(){}

  public PoolConfig getPool() {
    return pool;
  }

  public void setPool(PoolConfig pool) {
    this.pool = pool;
  }

  public MeetManagerConfig getMeetmanager() {
    return meetmanager;
  }

  public void setMeetmanager(MeetManagerConfig meetmanager) {
    this.meetmanager = meetmanager;
  }

  public int getPort() {
    return port;
  }

  public void setPort(int port) {
    this.port = port;
  }

  PoolConfig pool= new PoolConfig(new int[]{1, 2, 3, 4, 5}, 25);
  MeetManagerConfig meetmanager = new MeetManagerConfig();
  int port =8765;
}
class MeetManagerConfig{
  public MeetManagerConfig() {
  }

  public String getHost() {
    return host;
  }

  public void setHost(String host) {
    this.host = host;
  }

  public int getPort() {
    return port;
  }

  public void setPort(int port) {
    this.port = port;
  }

  String host="localhost";
  int port=8585;
}
