package edu.kit.ifv.mobitopp.populationsynthesis;

import java.util.function.Predicate;

import edu.kit.ifv.mobitopp.data.DemandRegion;
import edu.kit.ifv.mobitopp.populationsynthesis.region.ZoneBasedRegionPredicate;
import edu.kit.ifv.mobitopp.simulation.ImpedanceIfc;

public class OnlyAllZonesCalculator implements DemandDataForDemandRegionCalculator {

  private final DemandDataForDemandRegionCalculator other;
  private final Predicate<DemandRegion> filter;

  public OnlyAllZonesCalculator(
      final DemandDataForDemandRegionCalculator other, final Predicate<DemandRegion> filter) {
    this.other = other;
    this.filter = filter;
  }

  public OnlyAllZonesCalculator(DemandDataForDemandRegionCalculator other) {
    this(other, new ZoneBasedRegionPredicate());
  }

  @Override
  public void calculateDemandData(DemandRegion region, ImpedanceIfc impedance) {
    if (filter.test(region)) {
      other.calculateDemandData(region, impedance);
    }
  }

}
