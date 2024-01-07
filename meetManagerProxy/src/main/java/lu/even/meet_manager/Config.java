package lu.even.meet_manager;

import lu.even.RemoteServerConfig;

public record Config(int port, RemoteServerConfig meetmanager, RemoteServerConfig timingApplication) {
}

