package edu.kit.ifv.mobitopp.data.local.serialiser;

import static edu.kit.ifv.mobitopp.util.TestUtil.assertValue;
import static edu.kit.ifv.mobitopp.util.TestUtil.assertValues;
import static java.lang.String.valueOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.kit.ifv.mobitopp.data.Attractivities;
import edu.kit.ifv.mobitopp.data.Value;
import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.data.ZoneClassificationType;
import edu.kit.ifv.mobitopp.data.ZoneId;
import edu.kit.ifv.mobitopp.data.ZoneProperties;
import edu.kit.ifv.mobitopp.data.areatype.AreaType;
import edu.kit.ifv.mobitopp.data.areatype.AreaTypeRepository;
import edu.kit.ifv.mobitopp.data.areatype.BicRepository;
import edu.kit.ifv.mobitopp.data.areatype.ZoneAreaType;
import edu.kit.ifv.mobitopp.dataimport.DefaultRegionType;
import edu.kit.ifv.mobitopp.dataimport.RegionType;
import edu.kit.ifv.mobitopp.populationsynthesis.ExampleSetup;
import edu.kit.ifv.mobitopp.simulation.Location;
import edu.kit.ifv.mobitopp.simulation.LocationParser;
import edu.kit.ifv.mobitopp.simulation.emobility.ChargingDataForZone;

public class DefaultZoneFormatTest {

	private static final int oid = 1;
	private static final String id = "12345";
	private static final String name = "zone name";
	private static final AreaType areaType = ZoneAreaType.CITYOUTSKIRT;
	private static final RegionType regionType = new DefaultRegionType(1);
	private static final ZoneClassificationType classification = ZoneClassificationType.studyArea;
	private static final int parkingPlaces = 2;
	private static final Location centroidLocation = ExampleSetup.location;
	private static final boolean isDestination = true;
	private static final double relief = 1.0d;
	private DefaultZoneFormat format;
	private Zone zone;

	@BeforeEach
	public void initialise() {
		AreaTypeRepository areaTypeRepository = new BicRepository();
		Attractivities attractivity = new Attractivities();
		ChargingDataForZone charging = mock(ChargingDataForZone.class);
		ZoneId zoneId = new ZoneId(id, oid);
    ZoneProperties zoneProperties = ZoneProperties
				.builder()
				.name(name)
				.areaType(areaType)
				.regionType(regionType)
				.classification(classification)
				.parkingPlaces(parkingPlaces)
				.isDestination(isDestination)
				.centroidLocation(centroidLocation)
				.relief(relief)
				.zoneProperties(allProperties())
				.build();
		zone = new Zone(zoneId, zoneProperties, attractivity, charging);
		ChargingDataResolver chargingData = mock(ChargingDataResolver.class);
		when(chargingData.chargingDataFor(oid)).thenReturn(charging);
		Map<Integer, Attractivities> attractivities = Collections.singletonMap(zoneId(), attractivity);
		format = new DefaultZoneFormat(chargingData, attractivities, areaTypeRepository);
	}

  protected LinkedHashMap<String, Value> allProperties() {
    LocationParser locationParser = new LocationParser();
    LinkedHashMap<String, Value> properties = new LinkedHashMap<>();
    properties.put("id", new Value(id));
    properties.put("matrixColumn", new Value(String.valueOf(oid)));
    properties.put("name", new Value(name));
    properties.put("areaType", new Value(String.valueOf(areaType.getTypeAsInt())));
    properties.put("regionType", new Value(String.valueOf(regionType.code())));
    properties.put("classification", new Value(String.valueOf(classification)));
    properties.put("parkingPlaces", new Value(String.valueOf(parkingPlaces)));
    properties.put("isDestination", new Value(String.valueOf(isDestination)));
    properties.put("centroidLocation", new Value(locationParser.serialise(centroidLocation)));
    properties.put("relief", new Value(String.valueOf(relief)));
    return properties;
  }

	private int zoneId() {
		return Integer.parseInt(id);
	}

	@Test
	public void prepare() {
		List<String> prepared = format.prepare(zone);
		
		assertThat(prepared, contains(valueOf(oid),
				id, 
				name, 
				valueOf(areaType.getTypeAsInt()),
				valueOf(regionType.code()),
				valueOf(classification),
				valueOf(parkingPlaces),
				serialised(centroidLocation),
				valueOf(isDestination),
				valueOf(relief)));
	}

	private String serialised(Location location) {
		return new LocationParser().serialise(location);
	}
	
	@Test
	public void parse() {
		Optional<Zone> parsed = format.parse(format.prepare(zone));
		
		Zone parsedZone = parsed.get();
		assertValue(Zone::getId, parsedZone, zone);
		assertValue(Zone::getName, parsedZone, zone);
		assertValue(Zone::getAreaType, parsedZone, zone);
		assertValue(Zone::getRegionType, parsedZone, zone);
		assertValue(Zone::getClassification, parsedZone, zone);
		assertValue(Zone::getNumberOfParkingPlaces, parsedZone, zone);
		assertValue(Zone::centroidLocation, parsedZone, zone);
    assertValue(Zone::attractivities, parsedZone, zone);
    assertValues(Zone::getProperties, parsedZone, zone);
	}
}
