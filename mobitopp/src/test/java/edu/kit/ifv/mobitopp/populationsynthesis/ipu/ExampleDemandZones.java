package edu.kit.ifv.mobitopp.populationsynthesis.ipu;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import edu.kit.ifv.mobitopp.data.DemandZone;
import edu.kit.ifv.mobitopp.data.ExampleZones;
import edu.kit.ifv.mobitopp.data.demand.Demography;
import edu.kit.ifv.mobitopp.data.demand.EmploymentDistribution;
import edu.kit.ifv.mobitopp.data.demand.RangeDistribution;
import edu.kit.ifv.mobitopp.data.demand.RangeDistributionIfc;
import edu.kit.ifv.mobitopp.data.demand.RangeDistributionItem;

public class ExampleDemandZones {

  private final DemandZone someZone;
  private final DemandZone otherZone;

  public ExampleDemandZones(DemandZone someZone, DemandZone otherZone) {
    super();
    this.someZone = someZone;
    this.otherZone = otherZone;
  }

  public static ExampleDemandZones create() {
    ExampleZones zones = ExampleZones.create();
    DemandZone someZone = new DemandZone(zones.someZone(), someDemography());
    DemandZone otherZone = new DemandZone(zones.otherZone(), otherDemography());
    return new ExampleDemandZones(someZone, otherZone);
  }

  private static Demography someDemography() {
    EmploymentDistribution employment = EmploymentDistribution.createDefault();
    RangeDistributionIfc household = someHouseholdDistribution();
    RangeDistributionIfc femaleAge = someFemaleDistribution();
    RangeDistributionIfc maleAge = someMaleDistribution();
    RangeDistributionIfc income = someIncomeDistribution();
    Map<AttributeType, RangeDistributionIfc> rangeDistributions = new LinkedHashMap<>();
    rangeDistributions.put(StandardAttribute.householdSize, household);
    rangeDistributions.put(StandardAttribute.maleAge, maleAge);
    rangeDistributions.put(StandardAttribute.femaleAge, femaleAge);
    rangeDistributions.put(StandardAttribute.income, income);
    return new Demography(employment, rangeDistributions);
  }

  private static RangeDistributionIfc someMaleDistribution() {
    RangeDistributionIfc distribution = new RangeDistribution();
    distribution.addItem(new RangeDistributionItem(0, 10, 4));
    distribution.addItem(new RangeDistributionItem(11, Integer.MAX_VALUE, 2));
    return distribution;
  }

  private static RangeDistributionIfc someFemaleDistribution() {
    RangeDistributionIfc distribution = new RangeDistribution();
    distribution.addItem(new RangeDistributionItem(0, 5, 2));
    distribution.addItem(new RangeDistributionItem(6, Integer.MAX_VALUE, 1));
    return distribution;
  }

  private static RangeDistributionIfc someHouseholdDistribution() {
    RangeDistributionIfc distribution = new RangeDistribution();
    distribution.addItem(new RangeDistributionItem(1, 2));
    distribution.addItem(new RangeDistributionItem(2, 3));
    return distribution;
  }

  private static Demography otherDemography() {
    EmploymentDistribution employment = EmploymentDistribution.createDefault();
    RangeDistributionIfc household = someHouseholdDistribution();
    RangeDistributionIfc femaleAge = someFemaleDistribution();
    RangeDistributionIfc maleAge = someMaleDistribution();
    RangeDistributionIfc income = someIncomeDistribution();
    Map<AttributeType, RangeDistributionIfc> rangeDistributions = new LinkedHashMap<>();
    rangeDistributions.put(StandardAttribute.householdSize, household);
    rangeDistributions.put(StandardAttribute.maleAge, maleAge);
    rangeDistributions.put(StandardAttribute.femaleAge, femaleAge);
    rangeDistributions.put(StandardAttribute.income, income);
    return new Demography(employment, rangeDistributions);
  }

  private static RangeDistributionIfc someIncomeDistribution() {
    RangeDistributionIfc income = new RangeDistribution();
    income.addItem(new RangeDistributionItem(0, 1000, 1));
    income.addItem(new RangeDistributionItem(1001, 2000, 2));
    income.addItem(new RangeDistributionItem(2001, Integer.MAX_VALUE, 2));
    return income;
  }

  public DemandZone someZone() {
    return someZone;
  }

  public DemandZone otherZone() {
    return otherZone;
  }

  public List<DemandZone> asList() {
    return Arrays.asList(someZone, otherZone);
  }

}
