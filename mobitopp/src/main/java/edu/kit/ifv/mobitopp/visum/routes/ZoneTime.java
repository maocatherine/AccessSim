package edu.kit.ifv.mobitopp.visum.routes;

import java.util.Objects;

import edu.kit.ifv.mobitopp.time.RelativeTime;

public class ZoneTime {

  private final String zone;
  private final RelativeTime time;

  public ZoneTime(String zone, RelativeTime time) {
    super();
    this.zone = zone;
    this.time = time;
  }

  public String zone() {
    return zone;
  }

  public RelativeTime time() {
    return time;
  }

  public ZoneTime addTime(RelativeTime extension) {
    return new ZoneTime(zone, time.plus(extension));
  }

  @Override
  public int hashCode() {
    return Objects.hash(time, zone);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    ZoneTime other = (ZoneTime) obj;
    return Objects.equals(time, other.time) && Objects.equals(zone, other.zone);
  }

  @Override
  public String toString() {
    return zone + "=" + time.toDuration().toString();
  }

}
