package lu.even.manual_timing.verticles;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.Json;
import lu.even.manual_timing.events.EventAction;
import lu.even.manual_timing.events.EventMessage;
import lu.even.manual_timing.events.EventTypes;

public abstract class AbstractTimingVerticle<T> extends AbstractVerticle {
    private EventTypes eventType;

    public AbstractTimingVerticle(EventTypes eventType) {
        this.eventType = eventType;
    }

    @Override
    public void start() throws Exception {
        vertx.eventBus().<EventMessage>consumer(this.eventType.getName(), this::onMessage);
        System.out.println("verticle " + this.getClass().getSimpleName() + " started");
    }

    private void onMessage(Message<EventMessage> eventMessage) {
        T response = this.onMessage(eventMessage.body());
        if (response != null) {
            eventMessage.reply(Json.encode(response));
        }else{
            eventMessage.fail(500, "Unsuported operation in verticle");
        }
    }

    protected abstract T onMessage(EventMessage message);

    /**
     * This message will reach the frontend
     *
     * @param object the message you want to send can be simple string or complex object
     */
    void sendMessage(EventAction action, Object body) {
        vertx.eventBus().publish(EventTypes.MESSAGE.getName(), new EventMessage(action,Json.encode(body)));
    }
}
