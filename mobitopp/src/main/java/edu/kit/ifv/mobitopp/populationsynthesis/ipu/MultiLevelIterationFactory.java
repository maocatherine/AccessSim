package edu.kit.ifv.mobitopp.populationsynthesis.ipu;

import static java.util.stream.Collectors.toList;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import edu.kit.ifv.mobitopp.data.DemandRegion;
import edu.kit.ifv.mobitopp.populationsynthesis.SynthesisContext;

public class MultiLevelIterationFactory extends BaseIterationFactory implements IterationFactory {

	private final BaseIterationFactory singleLevelFactory;
	
	public MultiLevelIterationFactory(SynthesisContext context) {
		super(context);
		singleLevelFactory = new SingleLevelIterationFactory(context);
	}

	@Override
	protected Stream<Attribute> attributesFor(final DemandRegion region) {
		List<Attribute> toAttributes = new LinkedList<>();
		addAttributesOf(region, toAttributes);
		return toAttributes.stream();
	}

	private void addAttributesOf(DemandRegion region, List<Attribute> toAttributes) {
		toAttributes.addAll(singleLevelFactory.attributesFor(region).collect(toList()));
		for (DemandRegion part : region.parts()) {
			addAttributesOf(part, toAttributes);
		}
	}

	@Override
	public AttributeResolver createAttributeResolverFor(DemandRegion region) {
		Map<RegionalContext, List<Attribute>> attributes = attributesPerZone(region);
		return new MultiLevelAttributeResolver(attributes);
	}

	Map<RegionalContext, List<Attribute>> attributesPerZone(DemandRegion region) {
		Map<RegionalContext, List<Attribute>> toAttributes = new HashMap<>();
		addAttributesOf(region, List.of(), toAttributes);
		return toAttributes;
	}

	private void addAttributesOf(
			DemandRegion region, List<Attribute> upperRegionAttributes,
			Map<RegionalContext, List<Attribute>> toAttributes) {
		List<Attribute> andRegionAttributes = new LinkedList<>(upperRegionAttributes);
		andRegionAttributes.addAll(singleLevelFactory.attributesFor(region).collect(toList()));
		for (DemandRegion part : region.parts()) {
			addAttributesOf(part, andRegionAttributes, toAttributes);
		}
		toAttributes.put(region.getRegionalContext(), andRegionAttributes);
	}

}
