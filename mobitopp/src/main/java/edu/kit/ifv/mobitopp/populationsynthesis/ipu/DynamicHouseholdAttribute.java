package edu.kit.ifv.mobitopp.populationsynthesis.ipu;

import java.util.function.Function;

import edu.kit.ifv.mobitopp.data.PanelDataRepository;
import edu.kit.ifv.mobitopp.util.panel.HouseholdOfPanelData;

public class DynamicHouseholdAttribute extends NamedAttribute implements Attribute {

	private final Function<HouseholdOfPanelData, Integer> householdValue;

  public DynamicHouseholdAttribute(
      final RegionalContext context, final AttributeType attributeType, final int lowerBound,
      final int upperBound, final int requestedWeight,
      final Function<HouseholdOfPanelData, Integer> householdValue) {
		super(context, attributeType, lowerBound, upperBound, requestedWeight);
		this.householdValue = householdValue;
	}

	@Override
	public int valueFor(
			final HouseholdOfPanelData household, final PanelDataRepository panelDataRepository) {
		return lowerBound <= householdValue.apply(household)
				&& upperBound >= householdValue.apply(household) ? 1 : 0;
	}
}
