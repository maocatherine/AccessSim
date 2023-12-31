package edu.kit.ifv.mobitopp.simulation.destinationChoice;

import static edu.kit.ifv.mobitopp.util.collections.StreamUtils.warn;

import java.util.HashMap;
import java.util.Map;

import edu.kit.ifv.mobitopp.data.areatype.AreaType;
import edu.kit.ifv.mobitopp.data.areatype.ZoneAreaType;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CommunityTypeMapping {

	private static final int defaultType = 4;

	private final Map<AreaType, Integer> typeMapping;

	public CommunityTypeMapping() {
		typeMapping = new HashMap<>();
		typeMapping.put(ZoneAreaType.RURAL, 4);
		typeMapping.put(ZoneAreaType.PROVINCIAL, 4);
		typeMapping.put(ZoneAreaType.CITYOUTSKIRT, 3);
		typeMapping.put(ZoneAreaType.METROPOLITAN, 3);
		typeMapping.put(ZoneAreaType.CONURBATION, 1);
		typeMapping.put(ZoneAreaType.DEFAULT, 4);
	}

	public int getCommunityTypeFor(AreaType areaType) {
		return typeMapping.getOrDefault(areaType, warn(areaType, "community type", defaultType, log));
	}

}
