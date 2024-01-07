package lu.even.manual_timing.events;

public enum EventTypes {
    POOL_CONFIG("timing.pool.config"),
    REGISTER("timing.register"),
    MESSAGE("timing.message"),
    EVENT("timing.event"),
    LANE("timing.lane"),
    INSCRIPTION("timing.inscription"),
    DATABASE("timing.database"),
    MANUAL_TIME("timing.manual.time");
    private final String name;

    EventTypes(String name) {
        this.name = name;
    }

    public static EventTypes getByName(String name) {
        for (EventTypes event : EventTypes.values()) {
            if (event.name.equals(name)) {
                return event;
            }
        }
        return null;
    }

    public String getName() {
        return name;
    }
}
