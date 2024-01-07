package lu.even;

public class RemoteServerConfig {
  public String host = "localhost";
  public int port = 8585;

  public RemoteServerConfig() {
  }

  @Override
  public String toString() {
    return "RemoteServerConfig{" +
      "host='" + host + '\'' +
      ", port=" + port +
      '}';
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
}
