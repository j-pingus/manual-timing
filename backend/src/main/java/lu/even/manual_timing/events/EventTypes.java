package lu.even.manual_timing.events;

public enum EventTypes {
  POOL_CONFIG("timing.pool.config"),
  REGISTER("timing.register"),
  MESSAGE("timing.message");
  private final String name;

  EventTypes(String name){
    this.name=name;
  }

  public String getName() {
    return name;
  }
}
