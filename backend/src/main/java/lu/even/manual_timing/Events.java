package lu.even.manual_timing;

public enum Events {
  POOL_CONFIG("timing.pool.config"),
  REGISTER("timing.register"),
  MESSAGE("timing.message");
  private final String name;

  Events(String name){
    this.name=name;
  }

  public String getName() {
    return name;
  }
}
