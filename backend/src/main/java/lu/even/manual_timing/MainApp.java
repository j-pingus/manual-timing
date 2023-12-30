package lu.even.manual_timing;

import io.vertx.core.Vertx;
import lu.even.manual_timing.verticles.HttpServerVerticle;
import lu.even.manual_timing.verticles.PoolConfigVerticle;
import lu.even.manual_timing.verticles.RegistrationVerticle;

public class MainApp {
    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(new HttpServerVerticle(8765));
        vertx.deployVerticle(new PoolConfigVerticle());
        vertx.deployVerticle(new RegistrationVerticle());
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
