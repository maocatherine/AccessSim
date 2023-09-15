package edu.kit.ifv.mobitopp.populationsynthesis.householdlocation;

import edu.kit.ifv.mobitopp.data.ZoneId;
import edu.kit.ifv.mobitopp.simulation.Location;
import edu.kit.ifv.mobitopp.visum.VisumPoint2;

import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.Objects;

public class Building
        implements Serializable{

    public final String id;
    public final String address;
    public final int rooms;
    public final Location location;
    public final ZoneId zoneId;


    public Building(String id, String address, int rooms, Location location, ZoneId zoneId) {
        this.id = id;
        this.address = address;
        this.rooms = rooms;
        this.location = location;
        this.zoneId = zoneId;
    }

    public String getId() {
        return id;
    }

    public String getAddress() {
        return address;
    }

    public int getRooms() {
        return rooms;
    }

    public Location getLocation() {
        return location;
    }

    public ZoneId getZoneId() {
        return zoneId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Building building = (Building) o;
        return rooms == building.rooms && zoneId == building.zoneId && Objects.equals(id, building.id) && Objects.equals(address, building.address) && Objects.equals(location, building.location);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, address, rooms, location, zoneId);
    }
}
