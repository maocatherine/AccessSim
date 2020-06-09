package edu.kit.ifv.mobitopp.populationsynthesis.region;

import static java.util.stream.Collectors.toList;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Stream;

import edu.kit.ifv.mobitopp.data.DemandRegion;
import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.data.ZoneId;

public class StandardDemandRegionRelationsRepository implements DemandRegionRelationsRepository {

	private final Map<DemandRegion, Map<DemandRegion, Integer>> commutingRelations;

	public StandardDemandRegionRelationsRepository(
			Map<DemandRegion, Map<DemandRegion, Integer>> regionToRegion) {
				this.commutingRelations = regionToRegion;
	}

	@Override
	public Collection<DemandRegion> getRegions() {
		return Collections.unmodifiableCollection(commutingRelations.keySet());
	}

	@Override
	public void notifyAssignedRelation(Zone homeZone, Zone destination) {
		final DemandRegion homeCommunity = get(homeZone.getId());
		final DemandRegion destinationCommunity = get(destination.getId());
		updateRelation(homeCommunity, destinationCommunity);
	}

	private void updateRelation(DemandRegion origin, DemandRegion destination) {
		commutingRelations.get(origin).computeIfPresent(destination, (k, v) -> v - 1);
		cleanUpRelation(origin, destination);
	}

	private void cleanUpRelation(DemandRegion origin, DemandRegion destination) {
		if (!commutingRelations.containsKey(origin)) {
			return;
		}
		final Map<DemandRegion, Integer> relations = commutingRelations.get(origin);
		if (relations.containsKey(destination) && 0 == relations.get(destination)) {
			relations.remove(destination);
		}
	}

	@Override
	public Stream<DemandRegion> getCommutingRegionsFrom(ZoneId zoneId) {
		final DemandRegion region = get(zoneId);
		return commutingRelations.get(region).keySet().stream();
	}
	
	DemandRegion get(ZoneId id) {
		return commutingRelations
				.keySet()
				.stream()
				.filter(c -> c.contains(id))
				.findFirst()
				.orElseThrow(() -> new IllegalArgumentException("No community found for zone: " + id));
	}

	@Override
	public void scale(DemandRegion origin, int numberOfCommuters) {
		final Map<DemandRegion, Integer> destinations = commutingRelations.get(origin);
		final int sum = destinations.values().stream().mapToInt(Integer::intValue).sum();
		final double factor = (double) numberOfCommuters / sum;
		double remainder = 0.0d;
		Entry<DemandRegion, Integer> last = null;
		for (Entry<DemandRegion, Integer> entry : destinations.entrySet()) {
			last = entry;
			final double scaled = entry.getValue() * factor;
			final double withRemainder = scaled + remainder;
			final double floored = Math.floor(withRemainder);
			remainder = withRemainder - floored;
			final int newValue = (int) floored;
			destinations.put(entry.getKey(), newValue);
		}
		if (0.0d != remainder && null != last) {
			destinations.put(last.getKey(), last.getValue() + 1);
		}
		destinations
				.entrySet()
				.stream()
				.filter(e -> 0 == e.getValue())
				.map(Entry::getKey)
				.collect(toList())
				.forEach(d -> cleanUpRelation(origin, d));
	}

}
