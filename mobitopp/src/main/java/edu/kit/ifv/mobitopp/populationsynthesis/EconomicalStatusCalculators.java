package edu.kit.ifv.mobitopp.populationsynthesis;

import static edu.kit.ifv.mobitopp.util.collections.StreamUtils.warn;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.Map.Entry;
import java.util.TreeMap;

import edu.kit.ifv.mobitopp.data.demand.RangeDistributionIfc;
import edu.kit.ifv.mobitopp.data.demand.RangeDistributionItem;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EconomicalStatusCalculators {

	public static class DefaultEconomicalStatusCalculator implements EconomicalStatusCalculator {

		private static final double firstPerson = 1.0d;
		private static final double additionalPersons = 0.5d;
		private static final double children = 0.3d;
		private final TreeMap<Double, RangeDistributionIfc> distributions;

		public DefaultEconomicalStatusCalculator(
				TreeMap<Double, RangeDistributionIfc> distributions) {
			this.distributions = distributions;
		}

		@Override
		public EconomicalStatus calculateFor(int nominalSize, int numberOfMinors, int income) {
			double weightedSize = calculateOecdSize(nominalSize, numberOfMinors);
			Entry<Double, RangeDistributionIfc> floorEntry = distributions.floorEntry(weightedSize);
			RangeDistributionIfc value = floorEntry.getValue();
			RangeDistributionItem item = value.getItem(income);
			return EconomicalStatus.of(item.amount());
		}

		double calculateOecdSize(int nominalSize, int numberOfMinors) {
			int adults = nominalSize - numberOfMinors;
			double additionalAdults = Math.max(0.0d, adults - 1);
			return firstPerson + additionalPersons * additionalAdults + children * numberOfMinors;
		}

	}

	private static final String oecd2017File = "economical-status-oecd2017.csv";

	public static EconomicalStatusCalculator oecd2017() {
		try {
			TreeMap<Double, RangeDistributionIfc> distributions = loadMid();
			return new DefaultEconomicalStatusCalculator(distributions);
		} catch (IOException cause) {
			throw warn(new UncheckedIOException(cause), log);
		}
	}

	static TreeMap<Double, RangeDistributionIfc> loadMid() throws IOException {
		try (InputStream input = EconomicalStatusCalculators.class
				.getResourceAsStream(oecd2017File)) {
			return new EconomicalStatusDistributionParser().parse(input);
		}
	}

}
