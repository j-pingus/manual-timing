package lu.even.manual_timing.events;

import java.util.Arrays;

public enum EventTypes {
    POOL_CONFIG("timing.pool.config"),
    REGISTER("timing.register"),
    MESSAGE("timing.message"),
    LANE("timing.lane");
    private final String name;

    EventTypes(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
    public static EventTypes getByName(String name){
        for(EventTypes event:EventTypes.values()){
           if(event.name.equals(name)){
               return event;
           }
        }
        return null;
    }
}
