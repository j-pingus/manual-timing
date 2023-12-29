package lu.even.manual_timing;

public enum Event {
  POOL_CONFIG("pool.config");
  private final String name;

  Event(String name){
    this.name=name;
  }

  public String getName() {
    return name;
  }
}
