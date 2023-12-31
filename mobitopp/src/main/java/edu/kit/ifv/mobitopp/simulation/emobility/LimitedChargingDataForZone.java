package edu.kit.ifv.mobitopp.simulation.emobility;

import static edu.kit.ifv.mobitopp.util.collections.StreamUtils.warn;

import java.util.List;

import edu.kit.ifv.mobitopp.dataimport.DefaultPower;
import edu.kit.ifv.mobitopp.simulation.Location;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LimitedChargingDataForZone extends BaseChargingDataForZone {

	private static final long serialVersionUID = 1L;

	public LimitedChargingDataForZone(
			List<ChargingFacility> chargingFacilities, DefaultPower defaultPower) {
		super(chargingFacilities, defaultPower);
	}

	public LimitedChargingDataForZone(
			List<ChargingFacility> chargingFacilities, double privateChargingProbabilty,
			DefaultPower defaultPower) {
		super(chargingFacilities, privateChargingProbabilty, defaultPower);
	}

	@Override
	public int numberOfAvailableChargingPoints(List<ChargingFacility> chargingFacilities) {
		int cnt = 0;
		for (ChargingFacility facility : chargingFacilities) {
			if (facility.isFree()) {
				cnt++;
			}
		}
		return cnt;
	}

	@Override
	protected ChargingFacility freshChargingPoint(Location location, DefaultPower defaultPower) {
		throw warn(new IllegalArgumentException("no free charging point found"), log);
	}

}
