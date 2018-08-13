package edu.kit.ifv.mobitopp.simulation.impedance;

import edu.kit.ifv.mobitopp.simulation.ImpedanceIfc;
import edu.kit.ifv.mobitopp.simulation.Mode;
import edu.kit.ifv.mobitopp.time.Time;

public class IncreasedDrivingCost extends ImpedanceDecorator {
	
	private final float travelCostFactor;

	public IncreasedDrivingCost(ImpedanceIfc impedance, float travelCostFactor) {
		super(impedance);
		
		this.travelCostFactor = travelCostFactor;
	}
	
	public float getTravelCost(int source, int target, Mode mode, Time date) {
		
		if(mode == Mode.CAR) {
			return travelCostFactor * super.getTravelCost(source, target, mode, date);
		}
		
		return super.getTravelCost(source, target, mode, date);
	}

}
