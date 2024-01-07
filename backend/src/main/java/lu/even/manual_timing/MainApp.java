package lu.even.manual_timing;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.vertx.core.Vertx;
import lu.even.manual_timing.domain.PoolConfig;
import lu.even.manual_timing.verticles.*;

import java.io.File;
import java.io.IOException;

public class MainApp {
  public static void main(String[] args) throws IOException {
    Config config = getConfig();
    Vertx vertx = Vertx.vertx();
    vertx.deployVerticle(new PoolConfigVerticle(config.pool()));
    vertx.deployVerticle(new UserVerticle());
    vertx.deployVerticle(new SwimmingEventVerticle());
    vertx.deployVerticle(new InscriptionVerticle());
    vertx.deployVerticle(new ManualTimeVerticle());
    vertx.deployVerticle(new TimingDatabaseVerticle(config.databaseUrl()));
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
    File configFile = new File("configFile.json");
    if (configFile.exists()) {
      return mapper.readValue(configFile, Config.class);
    } else {
      mapper.writeValue(configFile, new Config(8765,"jdbc:h2:file:/manualTime",new PoolConfig(new int[]{1,2,3},25)));
      throw new Error("No config file found, creating default in " + configFile.getAbsolutePath() + " review it, then start again");
    }
  }
}
