package lu.even.manual_timing.domain;

import java.util.Objects;

public class Inscription{
    int event;
    int heat;
    int lane;
    String name;

    public int getEvent() {
        return event;
    }

    public Inscription(int event, int heat, int lane, String name) {
        this.event = event;
        this.heat = heat;
        this.lane = lane;
        this.name = name;
    }

    @Override
    public String toString() {
        return "Inscription{" +
                "event=" + event +
                ", heat=" + heat +
                ", lane=" + lane +
                ", name='" + name + '\'' +
                '}';
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

    public int getLane() {
        return lane;
    }

    public void setLane(int lane) {
        this.lane = lane;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Inscription that = (Inscription) o;
        return event == that.event && heat == that.heat && lane == that.lane;
    }

    @Override
    public int hashCode() {
        return Objects.hash(event, heat, lane);
    }

    public Inscription() {
    }
}
