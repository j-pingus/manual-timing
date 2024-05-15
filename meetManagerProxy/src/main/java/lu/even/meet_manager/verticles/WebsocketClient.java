package lu.even.meet_manager.verticles;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.http.WebSocketClient;
import io.vertx.core.json.Json;
import lu.even.RemoteServerConfig;
import lu.even.manual_timing.domain.ManualTime;
import lu.even.manual_timing.events.EventMessage;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class WebsocketClient extends AbstractVerticle {
  private static final Logger logger = LoggerFactory.getLogger(WebsocketClient.class);
  private final File path;
  private final RemoteServerConfig websocketServerConfig;

  public WebsocketClient(RemoteServerConfig websocketServerConfig, String path) {
    this.websocketServerConfig = websocketServerConfig;
    this.path = new File(path);
    if (!this.path.exists()) {
      this.path.mkdirs();
    }
  }

  @Override
  public void start() {
    startClient(vertx);
  }

  public void startClient(Vertx vertx) {
    WebSocketClient client =
      vertx.createWebSocketClient();
    client.connect(websocketServerConfig.port(),
        websocketServerConfig.host(), "/")
      .onSuccess((ctx) -> {
        logger.info("Websocket client connected:{}", websocketServerConfig);
        ctx.textMessageHandler((msg) -> {
          EventMessage message = Json.decodeValue(msg, EventMessage.class);
          System.out.println("message " + message.action());
          switch (message.action()) {
            case PUBLISH -> this.publish(message);
            case REFRESH -> this.refresh(message);
          }
          ctx.writeTextMessage("ack");
        }).exceptionHandler((e) -> {
          logger.error("Closed, restarting in 10 seconds", e);
          restart(client, 10);
        }).closeHandler((__) -> {
          logger.warn("Closed, restarting in 10 seconds");
          restart(client, 10);
        }).endHandler((__) -> {
            logger.info("Ended, restarting in 10 seconds");
            restart(client, 10);
          }
        );
      });
  }

  private void refresh(EventMessage message) {
  }

  private void publish(EventMessage message) {
    List<ManualTime> times = List.of(Json.decodeValue(message.body(), ManualTime[].class));
    var distances = times.stream().map(ManualTime::getDistance).sorted().distinct().toList();
    var lanes = times.stream().map(ManualTime::getLane).sorted().distinct().toList();
    try {
      StringBuffer sb = new StringBuffer();
      sb.append("LANE,");
      sb.append(distances.stream().map(d -> "TIME" + d).collect(Collectors.joining(",")));
      sb.append("\n");
      lanes.forEach(l -> {
        sb.append(l + ",");
        sb.append(
          distances.stream().map(d ->
            times.stream()
              .filter(t -> t.getDistance() == d)
              .filter(t -> t.getLane() == l)
              .map(ManualTime::getTime)
              .findFirst().orElse("")
          ).collect(Collectors.joining(",")));
        sb.append("\n");
      });
      FileUtils.writeStringToFile(
        new File(path, "test.csv"),
        sb.toString(),
        Charset.defaultCharset());
    } catch (IOException e) {
      logger.error("Could not write file", e);
    }
  }

  private void restart(WebSocketClient client, int delay) {
    client.close();
    vertx.setTimer(TimeUnit.SECONDS.toMillis(delay), (__) -> startClient(vertx));
  }
}
