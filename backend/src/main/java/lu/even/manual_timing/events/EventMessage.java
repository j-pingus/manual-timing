package lu.even.manual_timing.events;

import java.io.Serializable;

public record EventMessage(EventAction action, String body) implements Serializable {
}
