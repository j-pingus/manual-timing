package lu.even.manual_timing.verticles;

import io.vertx.core.json.Json;
import lu.even.manual_timing.domain.RegistrationRequest;
import lu.even.manual_timing.events.EventAction;
import lu.even.manual_timing.events.EventMessage;
import lu.even.manual_timing.events.EventTypes;

import java.util.UUID;

public class RegistrationVerticle extends AbstractTimingVerticle<String> {
    public RegistrationVerticle() {
        super(EventTypes.REGISTER);
    }

    @Override
    protected String onMessage(EventMessage message) {
        return switch (message.action()) {
            case POST -> {
                RegistrationRequest request =
                        Json.decodeValue(message.body(), RegistrationRequest.class);
                String uuid = UUID.randomUUID().toString();
                System.out.println("registered:" + request + " with id:" + uuid);
                sendMessage(EventAction.LOGIN, request);
                yield uuid;

            }
            default -> null;
        };
    }

}
