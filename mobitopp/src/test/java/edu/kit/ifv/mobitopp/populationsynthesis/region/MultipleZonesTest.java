package edu.kit.ifv.mobitopp.populationsynthesis.region;

import static edu.kit.ifv.mobitopp.populationsynthesis.RegionalLevel.community;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;

import edu.kit.ifv.mobitopp.data.DemandZone;
import edu.kit.ifv.mobitopp.data.ZoneId;
import edu.kit.ifv.mobitopp.data.demand.Demography;
import edu.kit.ifv.mobitopp.populationsynthesis.ExampleDemandZones;

public class MultipleZonesTest {

	@Test
	void containsZone() throws Exception {
		DemandZone someZone = ExampleDemandZones.create().getSomeZone();
		DemandZone otherZone = ExampleDemandZones.create().getOtherZone();
		Demography someDemography = someZone.nominalDemography();
		ZoneId notAvailableZone = new ZoneId("undefined", otherZone.getId().getMatrixColumn() + 1);
		MultipleZones multipleZones = new MultipleZones("1", community, someDemography, someZone, otherZone);

		assertAll(() -> assertTrue(multipleZones.contains(someZone.getId())),
				() -> assertTrue(multipleZones.contains(otherZone.getId())),
				() -> assertFalse(multipleZones.contains(notAvailableZone)));
	}

	@Test
	void canBeEmpty() throws Exception {
		Demography someDemography = ExampleDemandZones.create().getSomeZone().nominalDemography();
		MultipleZones zones = new MultipleZones("1", community, someDemography);

		assertThat(zones.zones()).isEmpty();
	}
}
