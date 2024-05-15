package lu.even.meet_manager;

import lu.even.RemoteServerConfig;

public record Config(
  int port,
  String meetManagerPath,
  RemoteServerConfig meetmanager,
  RemoteServerConfig timingApplication,
  RemoteServerConfig websocket) {
}

