package edu.kit.ifv.mobitopp.populationsynthesis;

import static java.util.stream.Collectors.toMap;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

public enum RegionalLevel implements Comparable<RegionalLevel> {

	zone("zone"), district("district"), community("community"), county("county");

	private static final Map<String, RegionalLevel> values = Stream
			.of(RegionalLevel.values())
			.collect(toMap(RegionalLevel::identifier, Function.identity()));
	private final String identifier;

	private RegionalLevel(String identifier) {
		this.identifier = identifier;
	}

	public String identifier() {
		return identifier;
	}
	
	public static RegionalLevel levelOf(String identifier) {
		if (values.containsKey(identifier)) {
			return values.get(identifier);
		}
		throw new IllegalArgumentException(String
				.format("Regional level of identifier '%s' is unknown. Available regional levels are: %s",
						identifier, values.keySet()));
	}
}
