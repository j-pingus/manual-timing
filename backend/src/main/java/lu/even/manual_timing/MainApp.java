package lu.even.manual_timing;

import io.vertx.core.Vertx;
import lu.even.manual_timing.verticles.*;

public class MainApp {
    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(new PoolConfigVerticle());
        vertx.deployVerticle(new UserVerticle());
        vertx.deployVerticle(new SwimmingEventVerticle());
        vertx.deployVerticle(new InscriptionVerticle());
        vertx.deployVerticle(new ManualTimeVerticle());
        vertx.deployVerticle(new HttpServerVerticle(8765));
        //Uncomment to view on server log messages being sent to the browser
        /*vertx.eventBus().consumer(EventTypes.MESSAGE.getName(),h->{
            System.out.println(
                  "message:"+
            h.body()
            );
        });

         */
    }
}
