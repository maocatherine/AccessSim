package edu.kit.ifv.mobitopp.populationsynthesis.ipu;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

import edu.kit.ifv.mobitopp.data.demand.Demography;

public class StandardAttributeTest {

	@Test
	void nameOfAttributesContainsContext() throws Exception {
		Demography demography = ExampleDemandZones.create().someZone().nominalDemography();
		String contextName = "my-context-1";
		AttributeContext context = () -> contextName;
		Stream<Attribute> attributes = Stream
				.of(StandardAttribute.householdSize, StandardAttribute.femaleAge)
				.flatMap(type -> type.createAttributes(demography, context));

		assertThat(attributes).allMatch(attribute -> attribute.name().startsWith(contextName));
	}
}
