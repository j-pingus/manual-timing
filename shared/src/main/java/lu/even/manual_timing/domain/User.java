package lu.even.manual_timing.domain;

import java.util.Objects;

public class User {
  private String uuid;
  private String password;
  private String name;
  private String role;
  private int lane;

  public User() {

  }

  public User(String uuid, String name, String role, int lane, String password) {
    this.uuid = uuid;
    this.name = name;
    this.role = role;
    this.lane = lane;
    this.password = password;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    User user = (User) o;
    return Objects.equals(uuid, user.uuid);
  }

  @Override
  public int hashCode() {
    return Objects.hash(uuid);
  }

  @Override
  public String toString() {
    return "User{" +
      "name='" + name + '\'' +
      ", role='" + role + '\'' +
      ", lane=" + lane +
      '}';
  }

  public String getUuid() {
    return uuid;
  }

  public User setUuid(String uuid) {
    this.uuid = uuid;
    return this;
  }

  public String getName() {
    return name;
  }

  public User setName(String name) {
    this.name = name;
    return this;
  }

  public String getRole() {
    return role;
  }

  public User setRole(String role) {
    this.role = role;
    return this;
  }

  public int getLane() {
    return lane;
  }

  public User setLane(int lane) {
    this.lane = lane;
    return this;
  }

  public String getPassword() {
    return password;
  }

  public User setPassword(String password) {
    this.password = password;
    return this;
  }
}
