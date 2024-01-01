package lu.even.manual_timing.domain;

import java.util.Objects;

public class ManualTime {
    int event;
    int heat;
    int lane;
    String time;

    public ManualTime() {
    }

    public int getEvent() {
        return event;
    }

    public void setEvent(int event) {
        this.event = event;
    }

    public int getHeat() {
        return heat;
    }

    public void setHeat(int heat) {
        this.heat = heat;
    }

    @Override
    public String toString() {
        return "ManualTime{" +
                "event=" + event +
                ", heat=" + heat +
                ", lane=" + lane +
                ", time='" + time + '\'' +
                '}';
    }

    public int getLane() {
        return lane;
    }

    public void setLane(int lane) {
        this.lane = lane;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ManualTime that = (ManualTime) o;
        return event == that.event && heat == that.heat && lane == that.lane;
    }

    @Override
    public int hashCode() {
        return Objects.hash(event, heat, lane);
    }
}
