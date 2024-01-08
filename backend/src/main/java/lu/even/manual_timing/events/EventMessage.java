package lu.even.manual_timing.events;

import java.io.Serializable;

public record EventMessage(EventAction action, String body,int eventId,int heatId,int laneId,String authorization) implements Serializable {
}
