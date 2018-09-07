package edu.kit.ifv.mobitopp.populationsynthesis.ipu;

import java.util.List;

public interface Iteration {

	List<Household> adjustHouseholdWeights(List<Household> households);

	double calculateGoodnessOfFitFor(List<Household> households);

}