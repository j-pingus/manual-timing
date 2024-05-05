package lu.even.meet_manager;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.vertx.core.Vertx;
import lu.even.RemoteServerConfig;
import lu.even.meet_manager.verticles.HttpServerVerticle;
import lu.even.meet_manager.verticles.MeetManagerVerticle;
import lu.even.meet_manager.verticles.WebsocketClient;

import java.io.File;
import java.io.IOException;

public class MainApp {
  public static void main(String[] args) throws IOException {
    Config config = getConfig();
    Vertx vertx = Vertx.vertx();
    vertx.deployVerticle(new MeetManagerVerticle(config.meetmanager(), config.timingApplication()));
    //vertx.deployVerticle(new StressManagerVerticle(config.timingApplication(), args[0]));
    vertx.deployVerticle(new HttpServerVerticle(config.port()));
    vertx.deployVerticle(new WebsocketClient());
  }

  private static Config getConfig() throws IOException {
    ObjectMapper mapper = new ObjectMapper();
    File configFile = new File("configProxy.json");
    if (configFile.exists()) {
      return mapper.readValue(configFile, Config.class);
    } else {
      mapper.writeValue(configFile, new Config(8766, new RemoteServerConfig("localhost", 8585, false), new RemoteServerConfig("timing.cnw.lu", 443, true)));
      throw new Error("No config file found, creating default in " + configFile.getAbsolutePath() + " review it, then start again");
    }
  }

}
