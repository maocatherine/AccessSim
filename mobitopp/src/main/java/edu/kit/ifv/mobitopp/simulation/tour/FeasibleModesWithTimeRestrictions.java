package edu.kit.ifv.mobitopp.simulation.tour;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.data.ZoneId;
import edu.kit.ifv.mobitopp.simulation.ImpedanceIfc;
import edu.kit.ifv.mobitopp.simulation.Mode;
import edu.kit.ifv.mobitopp.simulation.StandardMode;
import edu.kit.ifv.mobitopp.simulation.activityschedule.ActivityIfc;
import edu.kit.ifv.mobitopp.time.Time;

public class FeasibleModesWithTimeRestrictions implements FeasibleModesModel {
	
	public final double availableTimeFactor;
	public final int availableTimeOffset;
	public final ImpedanceIfc impedance;

	public FeasibleModesWithTimeRestrictions(
		ImpedanceIfc impedance,
		double availableTimeFactor, 
		int availableTimeOffset
		) {
		this.impedance = impedance;
		this.availableTimeFactor = availableTimeFactor;
		this.availableTimeOffset = availableTimeOffset;
	}

	@Override
	public Set<Mode> feasibleModes(
		ActivityIfc previousActivity, 
		Tour tour, 
		Set<Mode> choiceSet
	) {
		assert !choiceSet.isEmpty();
	

		ActivityIfc mainActivity = tour.mainActivity();
		
		assert previousActivity != null;
		assert mainActivity != null;
		
		List<ActivityIfc> additionalActivities = tour.activitiesBetween(previousActivity, mainActivity);
		
		int activityTime = 0;
		
		for (ActivityIfc act : additionalActivities) {
			activityTime += act.duration();		
		}
		
		return feasibleModes(previousActivity, mainActivity, activityTime, choiceSet);

	}

	@Override
	public Set<Mode> feasibleModes(
		ActivityIfc previousActivity, 
		ActivityIfc otherActivity, 
		int additionalActivityTime, 
		Set<Mode> choiceSet
	) {
		assert !choiceSet.isEmpty();
		

		if (choiceSet.size() == 1) {
			return choiceSet;
		}
		
		Time endOfPreviousActivity = previousActivity.calculatePlannedEndDate();
		Time startOfOtherActivity = otherActivity.startDate();
		
		int availableTime = (int) startOfOtherActivity.differenceTo(endOfPreviousActivity).toMinutes() - additionalActivityTime;
		
		
		Set<Mode> feasibleModes = new LinkedHashSet<Mode>();
		
		assert previousActivity.isLocationSet();
		assert otherActivity.isLocationSet();
		
		Zone origin = previousActivity.zone();
		Zone destination = otherActivity.zone();
		
		assert origin != null;
		assert destination != null;
		
		Mode fastestMode = null;
		double fastestTravelTime = Double.POSITIVE_INFINITY;
		
		for(Mode mode : choiceSet) {
			
			ZoneId originId = origin.getId();
      ZoneId destinationId = destination.getId();
      double time = this.impedance
          .getTravelTime(originId, destinationId, mode, endOfPreviousActivity);
			
			if (time <= fastestTravelTime) {
	
				if (time < fastestTravelTime || fastestMode == StandardMode.PASSENGER) {
					fastestMode = mode;
				} 
	
				fastestTravelTime = time;
			}
			
			if (time < Math.max(availableTime, 0.0)*availableTimeFactor + availableTimeOffset) {
				feasibleModes.add(mode);
			} 
			
		}
		
		assert fastestMode != null;
	
		if (feasibleModes.isEmpty()) {
			feasibleModes.add(fastestMode);
		}
		
		return feasibleModes;
		
	}
}