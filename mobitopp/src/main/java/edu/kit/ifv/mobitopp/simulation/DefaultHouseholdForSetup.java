package edu.kit.ifv.mobitopp.simulation;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.data.person.HouseholdId;
import edu.kit.ifv.mobitopp.populationsynthesis.HouseholdForSetup;
import edu.kit.ifv.mobitopp.populationsynthesis.PersonForSetup;
import edu.kit.ifv.mobitopp.populationsynthesis.PrivateCarForSetup;
import edu.kit.ifv.mobitopp.simulation.car.PrivateCar;

public class DefaultHouseholdForSetup implements HouseholdForSetup {

  private final HouseholdId householdId;
  private final int nominalSize;
  private final int domcode;
  private final Zone homeZone;
  private final Location homeLocation;
  private final int numberOfMinors;
  private final int numberOfNotSimulatedChildren;
  private final int nominalNumberOfCars;
  private final int income;
  private final boolean canChargePrivately;
  private final List<PersonForSetup> persons;
  private final List<PrivateCarForSetup> ownedCars;

  public DefaultHouseholdForSetup(
      HouseholdId householdId, int nominalSize, int domcode, Zone zone, Location location,
      int numberOfMinors, int numberOfNotSimulatedChildren, int nominalNumberOfCars, int income,
      boolean canChargePrivately) {
    super();
    this.householdId = householdId;
    this.nominalSize = nominalSize;
    this.domcode = domcode;
    this.homeZone = zone;
    this.homeLocation = location;
    this.numberOfMinors = numberOfMinors;
    this.numberOfNotSimulatedChildren = numberOfNotSimulatedChildren;
    this.nominalNumberOfCars = nominalNumberOfCars;
    this.income = income;
    this.canChargePrivately = canChargePrivately;
    this.persons = new ArrayList<>();
    this.ownedCars = new ArrayList<>();
  }

  @Override
  public void addPerson(PersonForSetup person) {
    persons.add(person);
  }

  @Override
  public List<PersonForSetup> getPersons() {
    return Collections.unmodifiableList(persons);
  }

  @Override
  public Household toHousehold() {
    HouseholdForDemand household = new HouseholdForDemand(getId(), nominalSize(), domcode,
        homeZone(), homeLocation(), numberOfNotSimulatedChildren(), ownedCars.size(), monthlyIncomeEur(),
        canChargePrivately());
    persons.stream().map(person -> person.toPerson(household)).forEach(household::addPerson);
    List<PrivateCar> cars = ownedCars.stream().map(car -> car.toCar(household)).collect(toList());
    household.ownCars(cars);
    return household;
  }

  @Override
  public HouseholdId getId() {
    return householdId;
  }

  @Override
  public Zone homeZone() {
    return homeZone;
  }

  @Override
  public Location homeLocation() {
    return homeLocation;
  }

  @Override
  public int monthlyIncomeEur() {
    return income;
  }

  @Override
  public boolean canChargePrivately() {
    return canChargePrivately;
  }

  @Override
  public void ownCars(Collection<PrivateCarForSetup> cars) {
    checkNumberOfCars(cars);
    ownedCars.addAll(cars);
  }

  private void checkNumberOfCars(Collection<PrivateCarForSetup> cars) {
    if (this.nominalNumberOfCars != cars.size()) {
      System.out
          .println(String
              .format("Nominal number of cars (%s) deviates from generated number of cars (%s).",
                  nominalNumberOfCars, cars.size()));
    }
  }
  
  @Override
  public int getTotalNumberOfCars() {
    return nominalNumberOfCars;
  }

  @Override
  public int getSize() {
    return getPersons().size() + numberOfNotSimulatedChildren;
  }

  @Override
  public int getNumberOfPersonsInAgeRange(int fromIncluding, int toIncluding) {
    return Math
        .toIntExact(persons
            .stream()
            .mapToInt(PersonForSetup::age)
            .filter(age -> fromIncluding <= age)
            .filter(age -> toIncluding >= age)
            .count());
  }

  @Override
  public int nominalSize() {
    return nominalSize;
  }

  @Override
  public int numberOfMinors() {
    return numberOfMinors;
  }

  @Override
  public int numberOfNotSimulatedChildren() {
    return numberOfNotSimulatedChildren;
  }
}
