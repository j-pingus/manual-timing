package lu.even.manual_timing.events;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.MessageCodec;
import io.vertx.core.json.Json;

public class EventMessageCodec implements MessageCodec<EventMessage, EventMessage> {
    public static final String CODEC_NAME = "eventMessage";
    @Override
    public void encodeToWire(Buffer buffer, EventMessage eventMessage) {
        String encoded = Json.encode(eventMessage);
        buffer.appendInt(encoded.length());
        buffer.appendString(encoded);
    }

    @Override
    public EventMessage decodeFromWire(int pos, Buffer buffer) {
        int length = buffer.getInt(pos);
        pos += 4;
        return Json.decodeValue(buffer.slice(pos, pos + length), EventMessage.class);
    }

    @Override
    public EventMessage transform(EventMessage eventMessage) {
        return eventMessage;
    }

    @Override
    public String name() {
        return CODEC_NAME;
    }

    @Override
    public byte systemCodecID() {
        return 50;
    }
}
