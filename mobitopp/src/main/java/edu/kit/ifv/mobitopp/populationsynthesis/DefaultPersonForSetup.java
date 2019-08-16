package edu.kit.ifv.mobitopp.populationsynthesis;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import edu.kit.ifv.mobitopp.data.PatternActivityWeek;
import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.data.person.PersonId;
import edu.kit.ifv.mobitopp.data.tourbasedactivitypattern.TourBasedActivityPattern;
import edu.kit.ifv.mobitopp.simulation.ActivityType;
import edu.kit.ifv.mobitopp.simulation.Employment;
import edu.kit.ifv.mobitopp.simulation.FixedDestination;
import edu.kit.ifv.mobitopp.simulation.Gender;
import edu.kit.ifv.mobitopp.simulation.Graduation;
import edu.kit.ifv.mobitopp.simulation.Household;
import edu.kit.ifv.mobitopp.simulation.Person;
import edu.kit.ifv.mobitopp.simulation.modeChoice.ModeChoicePreferences;
import edu.kit.ifv.mobitopp.simulation.person.PersonForDemand;

public class DefaultPersonForSetup implements PersonBuilder {

  private final PersonId id;
  private final HouseholdForSetup household;
  private final short age;
  private final Employment employment;
  private final Gender gender;
  private final Graduation graduation;
  private final int income;
  private final FixedDestinations fixedDestinations;
  private final ModeChoicePreferences modeChoicePrefsSurvey;

  private boolean hasBike;
  private boolean hasAccessToCar;
  private boolean hasPersonalCar;
  private boolean hasCommuterTicket;
  private boolean hasDrivingLicense;
  private ModeChoicePreferences modeChoicePreferences;
  private TourBasedActivityPattern activityPattern;

  public DefaultPersonForSetup(PersonId id, HouseholdForSetup household, int age, Employment employment,
      Gender gender, Graduation graduation, int income, ModeChoicePreferences modeChoicePrefsSurvey) {
    super();
    this.id = id;
    this.household = household;
    this.age = (short) age;
    this.employment = employment;
    this.gender = gender;
    this.graduation = graduation;
    this.income = income;
    this.modeChoicePrefsSurvey = modeChoicePrefsSurvey;
    fixedDestinations = new FixedDestinations();
    this.hasBike = false;
    this.hasAccessToCar = false;
    this.hasPersonalCar = false;
    this.hasCommuterTicket = false;
    this.hasDrivingLicense = false;
  }
  
  @Override
  public HouseholdForSetup household() {
    return household;
  }

  @Override
  public boolean hasPersonalCar() {
    return hasPersonalCar;
  }

  @Override
  public DefaultPersonForSetup setHasPersonalCar(boolean hasPersonalCar) {
    this.hasPersonalCar = hasPersonalCar;
    return this;
  }
  
  @Override
  public boolean hasAccessToCar() {
    return hasAccessToCar;
  }

  @Override
  public DefaultPersonForSetup setHasAccessToCar(boolean hasAccessToCar) {
    this.hasAccessToCar = hasAccessToCar;
    return this;
  }
  
  @Override
  public boolean hasBike() {
    return hasBike;
  }

  @Override
  public DefaultPersonForSetup setHasBike(boolean hasBike) {
    this.hasBike = hasBike;
    return this;
  }
  
  @Override
  public boolean hasCommuterTicket() {
    return hasCommuterTicket;
  }
  
  @Override
  public DefaultPersonForSetup setHasCommuterTicket(boolean hasCommuterTicket) {
    this.hasCommuterTicket = hasCommuterTicket;
    return this;
  }

  @Override
  public boolean hasDrivingLicense() {
    return hasDrivingLicense;
  }
  
  @Override
  public DefaultPersonForSetup setHasDrivingLicense(boolean hasDrivingLicense) {
    this.hasDrivingLicense = hasDrivingLicense;
    return this;
  }

  @Override
  public PersonId getId() {
    return id;
  }

  @Override
  public Gender gender() {
    return gender;
  }

  @Override
  public Employment employment() {
    return employment;
  }
  
  @Override
  public Graduation graduation() {
    return graduation;
  }

  @Override
  public int age() {
    return age;
  }

  @Override
  public int getIncome() {
    return income;
  }

  @Override
  public Zone homeZone() {
    return household.homeZone();
  }

  @Override
  public boolean isFemale() {
    return Gender.FEMALE.equals(gender);
  }

  @Override
  public boolean isMale() {
    return Gender.MALE.equals(gender);
  }

  @Override
  public PatternActivityWeek getPatternActivityWeek() {
    return new PatternActivityWeek(activityPattern.asPatternActivities());
  }

  @Override
  public DefaultPersonForSetup setPatternActivityWeek(TourBasedActivityPattern activityPattern) {
    this.activityPattern = activityPattern;
    return this;
  }

  @Override
  public TourBasedActivityPattern getActivityPattern() {
    return activityPattern;
  }

  @Override
  public DefaultPersonForSetup setFixedDestination(FixedDestination fixedDestination) {
    fixedDestinations.add(fixedDestination);
    return this;
  }
  
  @Override
  public Optional<FixedDestination> getFixedDestination(ActivityType activityType) {
  	return fixedDestinations.getDestination(activityType);
  }

  @Override
  public boolean hasFixedZoneFor(ActivityType activityType) {
    return fixedDestinations.hasDestination(activityType);
  }

  @Override
  public Optional<Zone> fixedZoneFor(ActivityType activityType) {
    return fixedDestinations.getDestination(activityType).map(FixedDestination::zone);
  }

  @Override
  public boolean hasFixedActivityZone() {
    return this.fixedDestinations.hasFixedDestination();
  }

  @Override
  public Zone fixedActivityZone() {
    return fixedDestinations
        .getFixedDestination()
        .map(FixedDestination::zone)
        .orElseGet(() -> household().homeZone());
  }
  
  @Override
	public Stream<FixedDestination> fixedDestinations() {
  	return fixedDestinations.stream();
  }
  
  @Override
  public ModeChoicePreferences getModeChoicePrefsSurvey() {
	  return modeChoicePrefsSurvey;
  }
  
  @Override
  public ModeChoicePreferences getModeChoicePreferences() {
	  return modeChoicePreferences;
  }
  
  @Override
  public DefaultPersonForSetup setModeChoicePreferences(ModeChoicePreferences modeChoicePreferences) {
    this.modeChoicePreferences = modeChoicePreferences;
    return this;
  }
  
  /**
   * This class does not support carsharing membership. The method does nothing.
   */
  @Override
  public DefaultPersonForSetup setCarsharingMembership(Map<String, Boolean> membership) {
    return this;
  }

  @Override
  public Person toPerson(Household household) {
    return new PersonForDemand(id, household, age, employment, gender, graduation, income, hasBike,
        hasAccessToCar, hasPersonalCar, hasCommuterTicket, hasDrivingLicense, activityPattern,
        fixedDestinations, modeChoicePrefsSurvey, modeChoicePreferences);
  }
  
  @Override
  public String toString() {
    return id.toString();
  }
}
