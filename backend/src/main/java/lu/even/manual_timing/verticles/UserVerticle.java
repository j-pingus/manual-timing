package lu.even.manual_timing.verticles;

import io.vertx.core.eventbus.Message;
import io.vertx.core.json.Json;
import lu.even.manual_timing.domain.User;
import lu.even.manual_timing.events.EventAction;
import lu.even.manual_timing.events.EventMessage;
import lu.even.manual_timing.events.EventTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class UserVerticle extends AbstractTimingVerticle {
  protected static final Logger logger = LoggerFactory.getLogger(UserVerticle.class);
  private final List<User> users;
  private final String secret;

  public UserVerticle(String secret) {
    super(EventTypes.REGISTER);
    this.users = new ArrayList<>();
    this.secret = secret;
  }

  @Override
  protected void onMessage(EventTypes eventType, Message<EventMessage> message) {
    switch (message.body().action()) {
      case POST -> answer(message, save(message.body().body()));
      case GET -> answer(message, find(message.body().authorization().substring(8)));
      case GET_BY_LANE -> answer(message, this.users.stream().filter(
        user -> user.getLane() == message.body().laneId()
          && user.getRole().equals("referee")
      ).collect(Collectors.toList()));
    }
  }

  private Object find(String uuid) {
    var ret =  this.users.stream()
      .filter(u -> u.getUuid().equals(uuid))
      .findFirst()
      .orElse(new User());
    logger.info("find '{}' found:'{}'",uuid,ret);
    return ret;
  }

  private Object save(String body) {
    User request = Json.decodeValue(body, User.class);
    this.users.remove(request);
    if (request.getRole().equals("control") || secret.equals(request.getPassword())) {
      String uuid = UUID.randomUUID().toString();
      request.setUuid(uuid);
      request.setPassword(null);
      this.users.add(request);
      logger.info("User registered:{}", request);
      sendMessage(EventAction.LOGIN, request);
      return request;
    } else {
      logger.info("Bad secret:{}", request);
      request.setPassword(null);
      request.setUuid(null);
      return request;
    }
  }
}
