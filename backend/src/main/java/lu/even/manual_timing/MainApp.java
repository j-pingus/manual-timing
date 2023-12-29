package lu.even.manual_timing;

import io.vertx.core.Vertx;
import lu.even.manual_timing.verticles.HttpServerVerticle;
import lu.even.manual_timing.verticles.PoolConfigVerticle;

public class MainApp {
  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();
    vertx.deployVerticle(new HttpServerVerticle());
    vertx.deployVerticle(new PoolConfigVerticle());
  }
}
