package edu.kit.ifv.mobitopp.visum.reader;

import static edu.kit.ifv.mobitopp.util.collections.StreamUtils.warn;
import static java.util.stream.Collectors.groupingBy;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import edu.kit.ifv.mobitopp.util.dataimport.Row;
import edu.kit.ifv.mobitopp.visum.NetfileLanguage;
import edu.kit.ifv.mobitopp.visum.StandardAttributes;
import edu.kit.ifv.mobitopp.visum.VisumConnector;
import edu.kit.ifv.mobitopp.visum.VisumConnector.Direction;
import edu.kit.ifv.mobitopp.visum.VisumNode;
import edu.kit.ifv.mobitopp.visum.VisumTransportSystemSet;
import edu.kit.ifv.mobitopp.visum.VisumTransportSystems;
import edu.kit.ifv.mobitopp.visum.VisumZone;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class VisumConnectorReader extends VisumBaseReader {

  private final Map<Integer, VisumNode> nodes;
  private final Map<Integer, VisumZone> zones;
  private final VisumTransportSystems allSystems;

  public VisumConnectorReader(
      NetfileLanguage language, Map<Integer, VisumNode> nodes, Map<Integer, VisumZone> zones,
      VisumTransportSystems allSystems) {
    super(language);
    this.nodes = nodes;
    this.zones = zones;
    this.allSystems = allSystems;
  }

  public Map<Integer, List<VisumConnector>> readConnectors(Stream<Row> content) {
    return content.map(this::createConnector).collect(groupingBy(c -> c.zone.id));
  }

  private VisumConnector createConnector(Row row) {
    VisumZone zone = zones.get(row.valueAsInteger(attribute(StandardAttributes.zoneNumber)));
    VisumNode node = nodes.get(nodeNumberOf(row));

    VisumTransportSystemSet systemSet = transportSystemsOf(row, allSystems);
    Direction direction = directionOf(row);
    float distance = lengthOf(row);
    int travelTimeInSeconds = travelTimeCarOf(row);
    return new VisumConnector(zone, node, direction, systemSet, distance,
        travelTimeInSeconds);
  }

  private Integer travelTimeCarOf(Row row) {
    return parseTime(row.get(travelTimeCarAttribute()));
  }

  private String travelTimeCarAttribute() {
    return attribute(StandardAttributes.travelTimeCar);
  }

  private Direction directionOf(Row row) {
    return getDirection(row.get(direction()));
  }
  
  private Direction getDirection(String direction) {
  	String fromOrigin = attribute(StandardAttributes.fromOrigin);
		String toDestination = attribute(StandardAttributes.toDestination);
		if (!direction.equals(fromOrigin) && !direction.equals(toDestination)) {
			throw warn(new IllegalArgumentException(String
					.format("VisumConnector: direction does not match (%s|%s)", fromOrigin, toDestination)), log);
  	}
  	
  	return direction.equals(fromOrigin) ? Direction.ORIGIN : Direction.DESTINATION;
  }

}
