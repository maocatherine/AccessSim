package edu.kit.ifv.mobitopp.simulation.person;

import java.util.Optional;
import java.util.stream.Stream;

import edu.kit.ifv.mobitopp.simulation.Mode;
import edu.kit.ifv.mobitopp.simulation.ZoneAndLocation;
import edu.kit.ifv.mobitopp.simulation.activityschedule.ActivityIfc;
import edu.kit.ifv.mobitopp.time.Time;

public interface FinishedTrip {

	int getOid();

	int getLegId();

	ZoneAndLocation origin();

	ZoneAndLocation destination();

	Mode mode();

	Time startDate();

	Time endDate();

	Time plannedEndDate();

	int plannedDuration();

	ActivityIfc previousActivity();

	ActivityIfc nextActivity();

	Statistic statistic();

	Optional<String> vehicleId();

	Stream<FinishedTrip> trips();

}