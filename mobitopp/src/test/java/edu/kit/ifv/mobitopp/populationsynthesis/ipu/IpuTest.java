package edu.kit.ifv.mobitopp.populationsynthesis.ipu;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import edu.kit.ifv.mobitopp.result.Logger;
import edu.kit.ifv.mobitopp.util.panel.HouseholdOfPanelDataId;

public class IpuTest {

	private List<WeightedHousehold> households;
	private List<WeightedHousehold> afterFirstIteration;
	private List<WeightedHousehold> afterSecondIteration;
	private List<WeightedHousehold> afterThirdIteration;
	private Iteration iteration;
	private Logger logger;

	@Before
	public void initialise() {
		households = createHouseholds(1.0d);
		afterFirstIteration = createHouseholds(2.0d);
		afterSecondIteration = createHouseholds(3.0d);
		afterThirdIteration = createHouseholds(4.0d);
		iteration = mock(Iteration.class);
		logger = mock(Logger.class);

		when(iteration.adjustWeightsOf(households)).thenReturn(afterFirstIteration);
		when(iteration.adjustWeightsOf(afterFirstIteration)).thenReturn(afterSecondIteration);
		when(iteration.adjustWeightsOf(afterSecondIteration)).thenReturn(afterThirdIteration);
		when(iteration.calculateGoodnessOfFitFor(households)).thenReturn(1.0d);
	}

	private List<WeightedHousehold> createHouseholds(double baseWeight) {
		int hhid = 1;
		WeightedHousehold household1 = newHousehold(newId(hhid++), baseWeight, 1, 1);
		WeightedHousehold household2 = newHousehold(newId(hhid++), baseWeight, 1, 1);
		return asList(household1, household2);
	}

	private HouseholdOfPanelDataId newId(int id) {
		short year = 2000;
		return new HouseholdOfPanelDataId(year, id);
	}

	private WeightedHousehold newHousehold(
			HouseholdOfPanelDataId id, double baseWeight, int householdType, int personType) {
		Map<String, Integer> attributes = new HashMap<>();
		attributes.put("some attribute", householdType);
		attributes.put("another attribute", personType);
		return new WeightedHousehold(id, baseWeight, attributes);
	}

	@Test
	public void neverReachConvergence() {
		hugeDecreasingGoodnessAfterFirst();
		int maxIterations = 3;
		double maxGoodness = 0.0d;
		Ipu ipu = new Ipu(iteration, maxIterations, maxGoodness, logger);

		List<WeightedHousehold> updatedHouseholds = ipu.adjustWeightsOf(households);

		assertThat(updatedHouseholds, is(equalTo(afterSecondIteration)));
		verify(logger).println(anyString());
	}

	@Test
	public void cancelOnConvergence() {
		hugeDecreasingGoodnessAfterFirst();
		int maxIterations = 3;
		double maxGoodness = 0.5d;
		Ipu ipu = new Ipu(iteration, maxIterations, maxGoodness, logger);

		List<WeightedHousehold> updatedHouseholds = ipu.adjustWeightsOf(households);

		assertThat(updatedHouseholds, is(equalTo(afterFirstIteration)));
	}

	private void hugeDecreasingGoodnessAfterFirst() {
		when(iteration.calculateGoodnessOfFitFor(afterFirstIteration)).thenReturn(0.5d);
		when(iteration.calculateGoodnessOfFitFor(afterSecondIteration)).thenReturn(0.25d);
		when(iteration.calculateGoodnessOfFitFor(afterThirdIteration)).thenReturn(0.4d);
	}
	
	@Test
	public void convergesOnlyOnImprovement() {
		when(iteration.calculateGoodnessOfFitFor(afterFirstIteration)).thenReturn(0.6d);
		when(iteration.calculateGoodnessOfFitFor(afterSecondIteration)).thenReturn(0.7d);
		when(iteration.calculateGoodnessOfFitFor(afterThirdIteration)).thenReturn(0.4d);
		
		int maxIterations = 3;
		double maxGoodness = 0.2d;
		Ipu ipu = new Ipu(iteration, maxIterations, maxGoodness, logger);

		List<WeightedHousehold> updatedHouseholds = ipu.adjustWeightsOf(households);

		assertThat(updatedHouseholds, is(equalTo(afterThirdIteration)));
	}

}
