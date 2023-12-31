package edu.kit.ifv.mobitopp.data.tourbasedactivitypattern;

import edu.kit.ifv.mobitopp.data.PatternActivity;
import edu.kit.ifv.mobitopp.simulation.ActivityType;
import edu.kit.ifv.mobitopp.time.Time;

@SuppressWarnings("serial")
public class ExtendedPatternActivity extends PatternActivity {
	
	public final int tournr;
	public final boolean isMainActivity;
	public final boolean isInSupertour;
	
	public static final ExtendedPatternActivity STAYATHOME_ACTIVITY = new ExtendedPatternActivity(-1, false, false, ActivityType.HOME, -1, Time.start, 9*24*60);

	public ExtendedPatternActivity(
			int tournr,
			boolean isMainActivity,
			boolean isInSupertour,
			ActivityType activityType, 
			int observedTripDuration,
			Time startTime, 
			int duration
			) {
		super(activityType, observedTripDuration, startTime, duration);
		
		this.tournr=tournr;
		this.isMainActivity=isMainActivity;
		this.isInSupertour=isInSupertour;
	}
	
	public static ExtendedPatternActivity fromActivity(int tournr, boolean isMainActivity, boolean isInSupertour, Activity activity) {
	
				return new ExtendedPatternActivity(tournr, isMainActivity, isInSupertour,
						activity.activityType(),
						(int) activity.expectedTripDuration().toMinutes(),
						activity.plannedStart(),
						(int) activity.plannedDuration().toMinutes()
					);
	}
	
	public static ExtendedPatternActivity fromActivity(int tournr, boolean isMainActivity, Activity activity) {

			return fromActivity(tournr, isMainActivity, false, activity);
	}
	
	public int tourNumber() {
		return tournr;
	}
	
	public boolean isMainActivity() {
		return isMainActivity;
	}

	@Override
	public String toString() {
		return "ExtendedPatternActivity [tournr=" + tournr 
							+ ", isMainActivity=" + isMainActivity 
							+ ", isInSupertour=" + isInSupertour 
							+ ", " + super.toString() + "]\n";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + (isMainActivity ? 1231 : 1237);
		result = prime * result + tournr;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		ExtendedPatternActivity other = (ExtendedPatternActivity) obj;
		if (isMainActivity != other.isMainActivity)
			return false;
		if (tournr != other.tournr)
			return false;
		return true;
	}

	public boolean isInSupertour() {
		return isInSupertour;
	}


}
