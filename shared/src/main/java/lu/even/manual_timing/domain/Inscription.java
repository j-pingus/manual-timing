package lu.even.manual_timing.domain;

import java.util.Objects;

public class Inscription {
  int event;
  int heat;
  int lane;
  String name;
  String nation;
  String entrytime;
  String clubcode;
  String agetext;

  public Inscription(int event, int heat, int lane, String name, String nation, String entrytime, String clubcode, String agetext) {
    this.event = event;
    this.heat = heat;
    this.lane = lane;
    this.name = name;
    this.nation = nation;
    this.entrytime = entrytime;
    this.clubcode = clubcode;
    this.agetext = agetext;
  }

  public Inscription() {
  }

  @Override
  public String toString() {
    return "Inscription{" +
      "event=" + event +
      ", heat=" + heat +
      ", lane=" + lane +
      ", name='" + name + '\'' +
      ", nation='" + nation + '\'' +
      ", entrytime='" + entrytime + '\'' +
      ", clubcode='" + clubcode + '\'' +
      ", agetext='" + agetext + '\'' +
      '}';
  }

  public String getAgetext() {
    return agetext;
  }

  public void setAgetext(String agetext) {
    this.agetext = agetext;
  }

  public String getNation() {
    return nation;
  }

  public void setNation(String nation) {
    this.nation = nation;
  }

  public String getEntrytime() {
    return entrytime;
  }

  public void setEntrytime(String entrytime) {
    this.entrytime = entrytime;
  }

  public String getClubcode() {
    return clubcode;
  }

  public void setClubcode(String clubcode) {
    this.clubcode = clubcode;
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
}
