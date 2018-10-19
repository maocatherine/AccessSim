package edu.kit.ifv.mobitopp.populationsynthesis.ipu;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import edu.kit.ifv.mobitopp.util.panel.HouseholdOfPanelDataId;
import nl.jqno.equalsverifier.EqualsVerifier;

public class IpuIterationTest {

	private static final double margin = 1e-6;
	private List<WeightedHousehold> households;
	private WeightedHousehold household1;
	private WeightedHousehold household2;
	private Iteration ipu;
	private Constraint someConstraint;
	private Constraint anotherConstraint;
	private List<WeightedHousehold> afterSomeConstraint;
	private List<WeightedHousehold> afterAnotherConstraint;

	@Before
	public void initialise() {
		createHouseholds();
		createConstraints();
		ipu = new IpuIteration(asList(someConstraint, anotherConstraint));
	}

	private void createHouseholds() {
		int hhid = 1;
		double baseWeight = 1.0d;
		household1 = newHousehold(newId(hhid++), baseWeight, 1, 1);
		household2 = newHousehold(newId(hhid++), baseWeight, 1, 1);
		households = asList(household1, household2);
		afterSomeConstraint = asList(household1.newWeight(2.0d), household2.newWeight(3.0d));
		afterAnotherConstraint = asList(household1.newWeight(3.0d), household2.newWeight(4.0d));
	}

	private HouseholdOfPanelDataId newId(int id) {
		return new HouseholdOfPanelDataId(2000, id);
	}

	private void createConstraints() {
		someConstraint = mock(Constraint.class);
		anotherConstraint = mock(Constraint.class);
		when(someConstraint.scaleWeightsOf(households)).thenReturn(afterSomeConstraint);
		when(anotherConstraint.scaleWeightsOf(afterSomeConstraint)).thenReturn(afterAnotherConstraint);
	}

	private WeightedHousehold newHousehold(
			HouseholdOfPanelDataId id, double baseWeight, int householdType, int personType) {
		Map<String, Integer> attributes = new HashMap<>();
		attributes.put("some attribute", householdType);
		attributes.put("another attribute", personType);
		return new WeightedHousehold(id, baseWeight, attributes);
	}

	@Test
	public void adjustHouseholdWeightsWithSize() {
		List<WeightedHousehold> adjusted = ipu.adjustWeightsOf(households);

		assertThat(adjusted, hasSize(households.size()));
		assertThat(adjusted, is(equalTo(afterAnotherConstraint)));

		verify(someConstraint).scaleWeightsOf(households);
		verify(anotherConstraint).scaleWeightsOf(afterSomeConstraint);
	}

	@Test
	public void returnsSameHouseholdsWithoutConstraints() {
		IpuIteration ipuIteration = new IpuIteration(Collections.emptyList());
		List<WeightedHousehold> adjusted = ipuIteration.adjustWeightsOf(households);

		assertThat(adjusted, is(equalTo(households)));
	}

	@Test
	public void calculatesGoodnessOfFit() {
		double someGoodness = 0.75d;
		double anotherGoodness = 0.25d;
		when(someConstraint.calculateGoodnessOfFitFor(households)).thenReturn(someGoodness);
		when(anotherConstraint.calculateGoodnessOfFitFor(households)).thenReturn(anotherGoodness);
		double goodnessOfFit = ipu.calculateGoodnessOfFitFor(households);

		assertThat(goodnessOfFit, is(closeTo(0.5d, margin)));

		verify(someConstraint).calculateGoodnessOfFitFor(households);
		verify(anotherConstraint).calculateGoodnessOfFitFor(households);
	}

	@Test(expected = IllegalArgumentException.class)
	public void calculatesNoGoodnessForEmptyConstraints() {
		IpuIteration ipuIteration = new IpuIteration(Collections.emptyList());
		ipuIteration.calculateGoodnessOfFitFor(households);
	}

	@Test
	public void equalsAndHashCode() {
		EqualsVerifier.forClass(IpuIteration.class).usingGetClass().verify();
	}

}