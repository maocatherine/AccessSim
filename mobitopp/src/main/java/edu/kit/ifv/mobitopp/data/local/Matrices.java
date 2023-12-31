package edu.kit.ifv.mobitopp.data.local;

import edu.kit.ifv.mobitopp.data.CostMatrix;
import edu.kit.ifv.mobitopp.data.FixedDistributionMatrix;
import edu.kit.ifv.mobitopp.data.TravelTimeMatrix;
import edu.kit.ifv.mobitopp.simulation.ActivityType;
import edu.kit.ifv.mobitopp.simulation.StandardMode;
import edu.kit.ifv.mobitopp.time.Time;

public interface Matrices {

	TravelTimeMatrix travelTimeFor(StandardMode mode, Time date);

	CostMatrix travelCostFor(StandardMode mode, Time date);

	CostMatrix distanceMatrix(Time date);

	CostMatrix parkingCostMatrix(Time date);

	CostMatrix parkingStressMatrix(Time date);

	CostMatrix constantMatrix(Time date);

	FixedDistributionMatrix fixedDistributionMatrixFor(ActivityType activityType);

}
