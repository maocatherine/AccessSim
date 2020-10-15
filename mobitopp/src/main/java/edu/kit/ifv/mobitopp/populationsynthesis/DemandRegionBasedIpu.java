package edu.kit.ifv.mobitopp.populationsynthesis;

import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

import edu.kit.ifv.mobitopp.data.DataRepositoryForPopulationSynthesis;
import edu.kit.ifv.mobitopp.data.DemandRegion;
import edu.kit.ifv.mobitopp.data.DemandZone;
import edu.kit.ifv.mobitopp.data.PanelDataRepository;
import edu.kit.ifv.mobitopp.populationsynthesis.ipu.ArrayIpu;
import edu.kit.ifv.mobitopp.populationsynthesis.ipu.ArrayIteration;
import edu.kit.ifv.mobitopp.populationsynthesis.ipu.WeightedHouseholds;
import edu.kit.ifv.mobitopp.populationsynthesis.ipu.ArrrayWeightedHouseholdsCreator;
import edu.kit.ifv.mobitopp.populationsynthesis.ipu.AttributeResolver;
import edu.kit.ifv.mobitopp.populationsynthesis.ipu.AttributeType;
import edu.kit.ifv.mobitopp.populationsynthesis.ipu.DefaultArrayIteration;
import edu.kit.ifv.mobitopp.populationsynthesis.ipu.DemandCreatorFactory;
import edu.kit.ifv.mobitopp.populationsynthesis.ipu.IterationFactory;
import edu.kit.ifv.mobitopp.populationsynthesis.ipu.MultiLevelIterationFactory;
import edu.kit.ifv.mobitopp.populationsynthesis.ipu.WeightDemandCreatorFactory;
import edu.kit.ifv.mobitopp.populationsynthesis.ipu.WeightedHousehold;
import edu.kit.ifv.mobitopp.populationsynthesis.ipu.WeightedHouseholdSelector;
import edu.kit.ifv.mobitopp.result.Logger;
import edu.kit.ifv.mobitopp.result.Results;
import edu.kit.ifv.mobitopp.simulation.ImpedanceIfc;
import edu.kit.ifv.mobitopp.util.panel.HouseholdOfPanelData;

public class DemandRegionBasedIpu implements DemandDataForDemandRegionCalculator {

	private final DataRepositoryForPopulationSynthesis dataRepository;
	private final SynthesisContext context;
	private final CreateAndSaveDemand createAndSaveDemand;

	public DemandRegionBasedIpu(
			final Results results, final WeightedHouseholdSelector householdSelector,
			final HouseholdCreator householdCreator, final PersonCreator personCreator,
			final DataRepositoryForPopulationSynthesis dataRepository, final SynthesisContext context,
			final AttributeType householdFilterType,
			final Function<DemandZone, Predicate<HouseholdOfPanelData>> householdFilter) {
		this.dataRepository = dataRepository;
		this.context = context;
		DemandCategories categories = new DemandCategories();
		DemandCreatorFactory demandCreatorFactory = new WeightDemandCreatorFactory(householdCreator,
				personCreator, panelData(), householdFilterType, householdFilter, householdSelector);
		createAndSaveDemand = new CreateAndSaveDemand(results, categories, demandCreatorFactory);
	}

	@Override
	public void calculateDemandData(final DemandRegion region, final ImpedanceIfc impedance) {
		ArrayIteration iteration = new DefaultArrayIteration();
    ArrayIpu ipu = new ArrayIpu(iteration, maxIterations(), maxGoodness(), loggerFor(region));
	  WeightedHouseholds households = householdsOf(region);
	  WeightedHouseholds scaledHouseholds = ipu.adjustWeightsOf(households);
	  IterationFactory factory = new MultiLevelIterationFactory(context);
	  AttributeResolver attributeResolver = factory.createAttributeResolverFor(region);
		create(scaledHouseholds.toList(), region, attributeResolver);
	}
	
	private Logger loggerFor(DemandRegion forZone) {
		return message -> System.out.println(String.format("%s: %s", forZone.getExternalId(), message));
	}

	private void create(
			final List<WeightedHousehold> households, final DemandRegion region,
			final AttributeResolver attributeResolver) {
		region.zones().forEach(zone -> createAndSave(households, zone, attributeResolver));
	}

	private void createAndSave(
			List<WeightedHousehold> households, DemandZone zone, AttributeResolver attributeResolver) {
		List<WeightedHousehold> selectedHouseholds = households
				.stream()
				.filter(household -> household.context().equals(zone.getRegionalContext()))
				.collect(toList());
		createAndSaveDemand.createAndSave(selectedHouseholds, zone, attributeResolver);
	}

	private PanelDataRepository panelData() {
		return dataRepository.panelDataRepository();
	}

  private WeightedHouseholds householdsOf(final DemandRegion region) {
    return new ArrrayWeightedHouseholdsCreator(context, panelData()).createFor(region);
  }

	private int maxIterations() {
		return context.maxIterations();
	}

	private double maxGoodness() {
		return context.maxGoodnessDelta();
	}

}
