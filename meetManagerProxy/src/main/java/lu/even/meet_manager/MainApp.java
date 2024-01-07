package lu.even.meet_manager;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.vertx.core.Vertx;
import lu.even.RemoteServerConfig;
import lu.even.meet_manager.verticles.HttpServerVerticle;
import lu.even.meet_manager.verticles.MeetManagerVerticle;

import java.io.File;
import java.io.IOException;

public class MainApp {
  public static void main(String[] args) throws IOException {
    Config config = getConfig();
    Vertx vertx = Vertx.vertx();
    vertx.deployVerticle(new MeetManagerVerticle(config.meetmanager(), config.timingApplication()));
    vertx.deployVerticle(new HttpServerVerticle(config.port()));
    //Uncomment to view on server log messages being sent to the browser
        /*vertx.eventBus().consumer(EventTypes.MESSAGE.getName(),h->{
            System.out.println(
                  "message:"+
            h.body()
            );
        });

         */
  }

  private static Config getConfig() throws IOException {
    ObjectMapper mapper = new ObjectMapper();
    File configFile = new File("configProxy.json");
    if (configFile.exists()) {
      return mapper.readValue(configFile, Config.class);
    } else {
      mapper.writeValue(configFile, new Config(8766,new RemoteServerConfig("localhost",8585),new RemoteServerConfig("localhost",8765)));
      throw new Error("No config file found, creating default in " + configFile.getAbsolutePath() + " review it, then start again");
    }
  }

}
