package lu.even.manual_timing;

import io.vertx.core.Vertx;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import lu.even.manual_timing.verticles.HttpServerVerticle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.IOException;

@ExtendWith(VertxExtension.class)
public class TestHttpServerVerticle {

  @BeforeEach
  void deploy_verticle(Vertx vertx, VertxTestContext testContext) throws IOException {
    vertx.deployVerticle(new HttpServerVerticle(9999, null), testContext.succeeding(id -> testContext.completeNow()));
  }

  @Test
  void verticle_deployed(Vertx vertx, VertxTestContext testContext) throws Throwable {
    testContext.completeNow();
  }
}
