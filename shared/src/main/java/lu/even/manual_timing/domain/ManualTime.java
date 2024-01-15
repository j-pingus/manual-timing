package lu.even.manual_timing.domain;

import java.util.Objects;

public class ManualTime {

  int event;
  int heat;
  int lane;
  int distance;
  String time;

  public ManualTime() {
  }

  public int getDistance() {
    return distance;
  }

  public ManualTime setDistance(int distance) {
    this.distance = distance;
    return this;
  }

  public int getEvent() {
    return event;
  }

  public ManualTime setEvent(int event) {
    this.event = event;
    return this;
  }

  public int getHeat() {
    return heat;
  }

  public ManualTime setHeat(int heat) {
    this.heat = heat;
    return this;
  }

  @Override
  public String toString() {
    return "ManualTime{" +
      "event=" + event +
      ", heat=" + heat +
      ", lane=" + lane +
      ", distance=" + distance +
      "m, time='" + time + '\'' +
      '}';
  }

  public int getLane() {
    return lane;
  }

  public ManualTime setLane(int lane) {
    this.lane = lane;
    return this;
  }

  public String getTime() {
    return time;
  }

  public ManualTime setTime(String time) {
    this.time = time;
    return this;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ManualTime that = (ManualTime) o;
    return event == that.event && heat == that.heat && lane == that.lane && distance == that.distance;
  }

  @Override
  public int hashCode() {
    return Objects.hash(event, heat, lane, distance);
  }
}
