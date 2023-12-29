package lu.even.manual_timing;

import io.vertx.core.Vertx;
import lu.even.manual_timing.verticles.HttpServerVerticle;
import lu.even.manual_timing.verticles.PoolConfigVerticle;
import lu.even.manual_timing.verticles.RegisterVerticle;

public class MainApp {
  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();
    vertx.deployVerticle(new HttpServerVerticle(8765));
    vertx.deployVerticle(new PoolConfigVerticle());
    vertx.deployVerticle(new RegisterVerticle());
  }
}
