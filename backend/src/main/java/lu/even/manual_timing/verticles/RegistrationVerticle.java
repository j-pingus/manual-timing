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

public class RegistrationVerticle extends AbstractTimingVerticle {
    private List<User> users;

    public RegistrationVerticle() {
        super(EventTypes.REGISTER);
        this.users = new ArrayList<>();
    }

    @Override
    protected Object onMessage(EventTypes eventTypes, EventMessage message) {
        return switch (message.action()) {
            case POST -> {
                User request =
                        Json.decodeValue(message.body(), User.class);
                String uuid = UUID.randomUUID().toString();
                request.setUuid(uuid);
                this.users.add(request);
                System.out.println("registered:" + request + " with id:" + uuid);
                sendMessage(EventAction.LOGIN, request);
                yield uuid;
            }
            case PUT -> {
                User request =
                        Json.decodeValue(message.body(), User.class);
                this.users.remove(request);
                this.users.add(request);
                sendMessage(EventAction.USER_MOVE, request);
                yield "";
            }
            case GET_BY_LANE -> {
                int lane = Integer.parseInt(message.id());
                yield this.users.stream().filter(user -> user.getLane() == lane && user.getRole().equals("referee")).collect(Collectors.toList());
            }
            default -> null;
        };
    }

}
