package lu.even.manual_timing.verticles;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.Json;
import lu.even.manual_timing.Events;
import lu.even.manual_timing.domain.RegistrationRequest;

import java.util.UUID;

public class RegisterVerticle extends AbstractVerticle {
  @Override
  public void start() {
    vertx.eventBus().<String>consumer(Events.REGISTER.getName(), this::onMessage);
    System.out.println("Register verticle started");
  }

  private <T> void onMessage(Message<T> tMessage) {
    RegistrationRequest message =
      Json.decodeValue(tMessage.body().toString(), RegistrationRequest.class);

    String uuid = UUID.randomUUID().toString();
    System.out.println("registered" + message + " with id=" + uuid);
    tMessage.reply(uuid);
  }

}
