package edu.kit.ifv.mobitopp.data.local.serialiser;

import static edu.kit.ifv.mobitopp.util.TestUtil.assertValue;
import static java.lang.String.valueOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;

import edu.kit.ifv.mobitopp.data.Attractivities;
import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.data.ZoneClassificationType;
import edu.kit.ifv.mobitopp.data.ZoneId;
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
	private DefaultZoneFormat format;
	private Zone zone;

	@Before
	public void initialise() {
		AreaTypeRepository areaTypeRepository = new BicRepository();
		Attractivities attractivity = new Attractivities();
		ChargingDataForZone charging = mock(ChargingDataForZone.class);
		ZoneId zoneId = new ZoneId(id, oid);
    zone = new Zone(zoneId, name, areaType, regionType, classification, parkingPlaces,
        centroidLocation, isDestination, attractivity, charging);
		ChargingDataResolver chargingData = mock(ChargingDataResolver.class);
		when(chargingData.chargingDataFor(oid)).thenReturn(charging);
		Map<Integer, Attractivities> attractivities = Collections.singletonMap(zoneId(), attractivity);
		format = new DefaultZoneFormat(chargingData, attractivities, areaTypeRepository);
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
				valueOf(isDestination)));
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
	}
}
