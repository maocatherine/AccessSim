package edu.kit.ifv.mobitopp.data.tourbasedactivitypattern;

import java.util.ArrayList;
import java.util.List;

import edu.kit.ifv.mobitopp.simulation.ActivityType;
import edu.kit.ifv.mobitopp.time.RelativeTime;
import edu.kit.ifv.mobitopp.time.SimpleTime;

public class TourBasedActivityPattern {
	
	private List<TourBasedActivityPatternElement>  elements;
	
	public TourBasedActivityPattern(List<TourBasedActivityPatternElement> elements) {
		this.elements  = new ArrayList<TourBasedActivityPatternElement>(elements);
	}
	
	@Override
	public String toString() {
		String result = "TourBasedActivityPattern(";
			
		for(TourBasedActivityPatternElement elem :elements ) {
			result += "\n\t" + elem.toString();
		}
			result += "\n)\n";
			
		return result;
	}
	
	public List<Activity> asActivities() {
		
		List<Activity> activities = new ArrayList<Activity>();
		
		if (elements.isEmpty()) {
			activities.add(new SimpleActivity(ActivityType.HOME, SimpleTime.ofHours(0), RelativeTime.INFINITE, RelativeTime.INFINITE));
			return activities;
		}
		

		for(TourBasedActivityPatternElement elem : elements) {
			
			List<Activity> elemActivities = elem.asActivities();
			
			activities.addAll(elemActivities);
		}
		
		return activities;
	}
	

}
