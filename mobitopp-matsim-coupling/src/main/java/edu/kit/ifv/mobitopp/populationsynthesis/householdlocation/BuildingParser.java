package edu.kit.ifv.mobitopp.populationsynthesis.householdlocation;

import edu.kit.ifv.mobitopp.data.ZoneId;
import edu.kit.ifv.mobitopp.data.ZoneRepository;
import edu.kit.ifv.mobitopp.populationsynthesis.opportunities.RoadLocator;
import edu.kit.ifv.mobitopp.populationsynthesis.opportunities.RoadPosition;
import edu.kit.ifv.mobitopp.simulation.ActivityType;
import edu.kit.ifv.mobitopp.simulation.Location;
import edu.kit.ifv.mobitopp.util.dataimport.CsvFile;
import edu.kit.ifv.mobitopp.util.dataimport.Row;
import lombok.RequiredArgsConstructor;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import static edu.kit.ifv.mobitopp.util.collections.StreamUtils.warn;

@RequiredArgsConstructor
public class BuildingParser {

    private static final String zoneId = "zoneId";
    private static final String buildingId = "BLD_PID";
    private static final String locationX = "CNTRD_LONG";
    private static final String locationY = "CNTRD_LAT";
    private static final String rooms = "Rooms";
    private static final String address = "ADDRESS";

    private final ZoneRepository zoneRepository;
    private final RoadLocator roadLocator;

    public Building parse(Row row) {
        ZoneId zone = zoneOf(row);
        String buildingId = buildingIdOf(row);
        Location location = locationOf(row, zone);
        int rooms = roomsOf(row);
        String address = addressOf(row);
        return new Building(buildingId, address, rooms, location, zone);
    }

    private String buildingIdOf(Row row) {return row.get(buildingId);}

    private ZoneId zoneOf(Row row) {
        return zoneRepository.getId(row.get(zoneId));
    }

    private Location locationOf(Row row, ZoneId zone) {
        double x = row.valueAsDouble(locationX);
        double y = row.valueAsDouble(locationY);
        Point2D.Double coordinate = new Point2D.Double(x, y);
        RoadPosition position = roadLocator.getRoadPosition(zone, coordinate);
        int link = position.getLink();
        double roadPosition = position.getPosition();
        return new Location(coordinate, link, roadPosition);
    }

    private int roomsOf(Row row) {
        float roomsFloat = row.valueAsFloat(rooms);
        int roomsInt = Math.round(roomsFloat);
        return roomsInt;
    }

    private String addressOf(Row row) {
        return row.get(address);
    }


}
