package lu.even.manual_timing.verticles;

import io.vertx.core.json.Json;
import lu.even.manual_timing.domain.User;
import lu.even.manual_timing.events.EventAction;
import lu.even.manual_timing.events.EventMessage;
import lu.even.manual_timing.events.EventTypes;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class UserVerticle extends AbstractTimingVerticle {
  private final List<User> users;

  public UserVerticle() {
    super(EventTypes.REGISTER);
    this.users = new ArrayList<>();
  }

  @Override
  protected Object onMessage(EventTypes eventTypes, EventMessage message) {
    return switch (message.action()) {
      case POST -> saveNew(message.body());
      case PUT -> this.save(message.body());
      case GET_BY_LANE -> this.users.stream().filter(
        user -> user.getLane() == message.laneId()
          && user.getRole().equals("referee")
      ).collect(Collectors.toList());

      default -> null;
    };
  }

  private Object saveNew(String body) {
    User request = Json.decodeValue(body, User.class);
    String uuid = UUID.randomUUID().toString();
    request.setUuid(uuid);
    this.users.add(request);
    logger.info("User registered:{}", request);
    sendMessage(EventAction.LOGIN, request);
    return uuid;
  }

  private Object save(String body) {
    User request =
      Json.decodeValue(body, User.class);
    this.users.remove(request);
    this.users.add(request);
    logger.info("User moved:{}", request);
    sendMessage(EventAction.USER_MOVE, request);
    return "";
  }
}
