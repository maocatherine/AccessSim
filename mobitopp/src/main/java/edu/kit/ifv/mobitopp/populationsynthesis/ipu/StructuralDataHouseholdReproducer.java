package edu.kit.ifv.mobitopp.populationsynthesis.ipu;

import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.stream.Stream;

import edu.kit.ifv.mobitopp.data.DemandRegion;
import edu.kit.ifv.mobitopp.data.PanelDataRepository;
import edu.kit.ifv.mobitopp.data.demand.RangeDistributionIfc;
import edu.kit.ifv.mobitopp.data.demand.RangeDistributionItem;

public class StructuralDataHouseholdReproducer implements HouseholdReproducer {

	private final DemandRegion region;
	private final AttributeType householdFilterType;
	private final WeightedHouseholdSelector householdSelector;
	private final List<Attribute> attributes;
  private final PanelDataRepository panelData;
	
	public StructuralDataHouseholdReproducer(
			final DemandRegion region, final AttributeType householdFilterType,
			final WeightedHouseholdSelector householdSelector,
			final List<Attribute> householdAttributes, final PanelDataRepository panelData) {
		super();
		this.region = region;
		this.householdFilterType = householdFilterType;
		this.householdSelector = householdSelector;
		this.attributes = householdAttributes;
    this.panelData = panelData;
	}

	@Override
	public Stream<WeightedHousehold> getHouseholdsToCreate(final List<WeightedHousehold> households) {
		return householdDistribution().items().flatMap(item -> filter(households, item));
	}

	private RangeDistributionIfc householdDistribution() {
		return region.nominalDemography().getDistribution(householdFilterType);
	}

	private Stream<WeightedHousehold> filter(
			final List<WeightedHousehold> households, final RangeDistributionItem item) {
		List<WeightedHousehold> householdsByType = households
				.stream()
				.filter(household -> filterType(household, item))
				.filter(household -> Double.isFinite(household.weight()))
				.collect(toList());
		return householdSelector.selectFrom(householdsByType, item.amount()).stream();
	}

	private boolean filterType(final WeightedHousehold household, final RangeDistributionItem item) {
		return attributes
				.stream()
				.filter(attribute -> attribute.matches(item))
				.anyMatch(attribute -> 0 < attribute.valueFor(household.household(), panelData));
	}

}
