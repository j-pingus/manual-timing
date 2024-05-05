package lu.even.meet_manager.verticles;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;

import java.util.concurrent.TimeUnit;

public class WebsocketClient extends AbstractVerticle {
  @Override
  public void start() {
    startClient(vertx);
  }
  public void startClient(Vertx vertx){
    HttpClient client = vertx.createHttpClient();
    client.webSocket(8765, "localhost", "/")
      .onSuccess((ctx) -> ctx.textMessageHandler((msg) -> {
        System.out.println("message " + msg);
        ctx.writeTextMessage("ack");
      }).exceptionHandler((e) -> {
        System.out.println("Closed, restarting in 10 seconds");
        restart(client, 5);
      }).closeHandler((__) -> {
        System.out.println("Closed, restarting in 10 seconds");
        restart(client, 10);
      }));

  }
  private void restart(HttpClient client, int delay) {
    client.close();
    vertx.setTimer(TimeUnit.SECONDS.toMillis(delay), (__) -> {
      startClient(vertx);
    });
  }
}
