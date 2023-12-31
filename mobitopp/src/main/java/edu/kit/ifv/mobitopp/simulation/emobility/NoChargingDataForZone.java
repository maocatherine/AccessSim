package edu.kit.ifv.mobitopp.simulation.emobility;

import static edu.kit.ifv.mobitopp.util.collections.StreamUtils.warn;

import java.util.stream.Stream;

import edu.kit.ifv.mobitopp.simulation.ActivityType;
import edu.kit.ifv.mobitopp.simulation.ChargingListener;
import edu.kit.ifv.mobitopp.simulation.Household;
import edu.kit.ifv.mobitopp.simulation.activityschedule.ActivityIfc;
import edu.kit.ifv.mobitopp.time.Time;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NoChargingDataForZone implements ChargingDataForZone {

	public static final NoChargingDataForZone noCharging = new NoChargingDataForZone();

	private static final double noPrivateCharging = 0.0;

	private NoChargingDataForZone() {
	}
	
	@Override
	public Stream<ChargingFacility> facilities() {
		return Stream.empty();
	}

	@Override
	public int numberOfChargingPoints() {
		return 0;
	}

	@Override
	public int numberOfAvailableChargingPoints() {
		return 0;
	}

	@Override
	public double privateChargingProbability() {
		return noPrivateCharging;
	}

	@Override
	public boolean isChargingPointAvailable() {
		return false;
	}

	@Override
	public boolean canElectricCarCharge(ActivityType activityType, Household household) {
		return false;
	}

	@Override
	public void startCharging(
			ElectricCar car, Household household, ActivityIfc activity, Time time,
			float kwh) {
		throw warn(new IllegalStateException("Charging is not allowed here."), log);
	}

	@Override
	public float stopCharging(ElectricCar car, Time currentTime) {
		throw warn(new IllegalStateException("Can not stop charging when charging is not allowed."), log);
	}

	@Override
	public void register(ChargingListener chargingListener) {
	}

}
