package edu.kit.ifv.mobitopp.populationsynthesis;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import edu.kit.ifv.mobitopp.data.PatternActivityWeek;
import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.data.person.HouseholdId;
import edu.kit.ifv.mobitopp.data.person.PersonId;
import edu.kit.ifv.mobitopp.data.tourbasedactivitypattern.ExtendedPatternActivity;
import edu.kit.ifv.mobitopp.data.tourbasedactivitypattern.TourBasedActivityPattern;
import edu.kit.ifv.mobitopp.data.tourbasedactivitypattern.TourBasedActivityPatternCreator;
import edu.kit.ifv.mobitopp.simulation.ActivityType;
import edu.kit.ifv.mobitopp.simulation.Car;
import edu.kit.ifv.mobitopp.simulation.Car.Segment;
import edu.kit.ifv.mobitopp.simulation.DefaultHouseholdForSetup;
import edu.kit.ifv.mobitopp.simulation.Employment;
import edu.kit.ifv.mobitopp.simulation.FixedDestination;
import edu.kit.ifv.mobitopp.simulation.Gender;
import edu.kit.ifv.mobitopp.simulation.Graduation;
import edu.kit.ifv.mobitopp.simulation.Location;
import edu.kit.ifv.mobitopp.simulation.LocationParser;
import edu.kit.ifv.mobitopp.simulation.car.BatteryElectricCar;
import edu.kit.ifv.mobitopp.simulation.car.CarPosition;
import edu.kit.ifv.mobitopp.simulation.car.ConventionalCar;
import edu.kit.ifv.mobitopp.simulation.car.ExtendedRangeElectricCar;
import edu.kit.ifv.mobitopp.simulation.emobility.EmobilityPerson.PublicChargingInfluencesDestinationChoice;
import edu.kit.ifv.mobitopp.simulation.modeChoice.ModeChoicePreferences;
import edu.kit.ifv.mobitopp.time.Time;

public abstract class ExampleSetup {

	public static final int type = 7;
	public static final int observedTripDuration = 2;
	public static final Time startTime = Time.start.plusMinutes(3);
	public static final int duration = 4;
	public static final int tourNumber = 0;
	public static final boolean isMainActivity = false;
	public static final boolean isInSupertour = false;
	
	public static final int personOid = 1;
	public static final int firstPerson = 1;
	public static final int secondPerson = 2;
	public static final int age = 3;
	public static final Employment employment = Employment.FULLTIME;
	public static final Gender gender = Gender.MALE;
  public static final Graduation graduation = Graduation.highSchoolGraduate;
	public static final boolean hasBike = true;
	public static final boolean hasAccessToCar = true;
	public static final boolean hasPersonalCar = true;
	public static final boolean hasCommuterTicket = true;
	public static final boolean hasLicense = true;
	public static final float eMobilityAcceptance = 1.0f;
	public static final PublicChargingInfluencesDestinationChoice chargingInfluencesDestinationChoice = PublicChargingInfluencesDestinationChoice.ALWAYS;

	public static final int firstHousehold = 1;
	public static final int secondHousehold = 2;
	public static final short householdYear = 2011;
	public static final int householdNumber = 1;
	public static final int nominalSize = 1;
  public static final int domcode = 2;
  public static final int householdType = 3;
	public static final Location location = new Location(new Point2D.Double(1.0, 2.0), 1, 0.5);
	public static final String serialisedLocation = new LocationParser().serialise(location);
	public static final Location anotherLocation = new Location(new Point2D.Double(3.0, 4.0), 2, 0.5);
	public static final int numberOfMinors = 0;
	public static final int numberOfNotSimulatedChildren = 0;
	public static final int totalNumberOfCars = 1;
	public static final int noCars = 0;
	public static final int income = 1;
  public static final int incomeClass = 1;
  public static final EconomicalStatus economicalStatus = EconomicalStatus.veryLow;
	public static final boolean canChargePrivately = false;
	
	public static final int carId = 1;
	public static final Segment segment = Segment.SMALL;
	public static final int capacity = 2;
	public static final float initialMileage = 3;
	public static final float fuelLevel = 4;
	public static final float maxRange = 5;
	public static final float batteryLevel = 6;
	public static final float batteryCapacity = 7;
	public static final float minimumChargingLevel = 8;
	public static final float electricRange = 9;
	public static final float conventionalRange = 10;
	public static final float fullPowerRange = 11;
	public static final float conventionalFuelLevel = 0;

	public static Population population(Zone zone) {
		Population population = new Population();
		HouseholdForSetup householdSetup = household(zone, firstHousehold, totalNumberOfCars);
		PersonBuilder personForDemand = personOf(householdSetup, firstPerson, zone);
		householdSetup.addPerson(personForDemand);
		householdSetup.ownCars(cars(householdSetup.getId(), personForDemand, zone));
    population.add(householdSetup);
		HouseholdForSetup eMobilityHouseholdSetup = household(zone, secondHousehold);
    population.add(eMobilityHouseholdSetup);
		eMobilityHouseholdSetup.addPerson(emobilityPersonOf(eMobilityHouseholdSetup, secondPerson, zone));
		return population;
	}

	private static Collection<PrivateCarForSetup> cars(HouseholdId household, PersonBuilder personForDemand, Zone zone) {
		ArrayList<PrivateCarForSetup> cars = new ArrayList<>();
		cars.add(conventionalCar(household, personForDemand, zone));
		return cars;
	}

	public static PrivateCarForSetup conventionalCar(HouseholdId household, PersonBuilder person, Zone zone) {
		Car car = conventionalCar(zone);
		PersonId personId = person.getId();
    return new DefaultPrivateCarForSetup(car, household, personId, personId);
	}
	
	public static PrivateCarForSetup conventionalCar(
			HouseholdForSetup household, PersonBuilder mainUser, PersonBuilder personalUser, Zone zone) {
		Car car = conventionalCar(zone);
		PersonId mainUserId = mainUser.getId();
    PersonId personalUserId = personalUser == null ? null : personalUser.getId();
    return new DefaultPrivateCarForSetup(car, household.getId(), mainUserId, personalUserId);
	}

	public static ConventionalCar conventionalCar(Zone zone) {
		CarPosition position = new CarPosition(zone, location);
		return new ConventionalCar(carId, position , segment, capacity, initialMileage,
				fuelLevel, maxRange);
	}

	public static BatteryElectricCar bevCar(Zone zone) {
		CarPosition position = new CarPosition(zone, location);
		return new BatteryElectricCar(carId, position , segment, capacity, initialMileage, batteryLevel,
				electricRange, batteryCapacity, minimumChargingLevel);
	}
	
	public static ExtendedRangeElectricCar erevCar(Zone zone) {
		CarPosition position = new CarPosition(zone, location);
		return new ExtendedRangeElectricCar(carId, position, segment, capacity, initialMileage,
				batteryLevel, conventionalFuelLevel, electricRange, conventionalRange, fullPowerRange, batteryCapacity,
				minimumChargingLevel);
	}

	public static HouseholdForSetup household(Zone zone, int householdOid) {
	  return household(zone, householdOid, noCars);
	}
	
	public static HouseholdForSetup household(Zone zone, int householdOid, int numberOfCars) {
	  HouseholdId id = new HouseholdId(householdOid, householdYear, householdNumber);
    return new DefaultHouseholdForSetup(id, nominalSize, domcode, householdType, zone, location,
        numberOfMinors, numberOfNotSimulatedChildren, numberOfCars, income, incomeClass,
        economicalStatus, canChargePrivately);
  }

	public static PersonBuilder emobilityPersonOf(HouseholdForSetup household, int personNumber, Zone zone) {
		Map<String, Boolean> carSharingCustomership = carSharingCustomership();
    return new EmobilityPersonBuilder(personOf(household, personNumber, zone, ActivityType.WORK))
        .setEmobilityAcceptance(eMobilityAcceptance)
        .setChargingInfluencesDestinationChoice(chargingInfluencesDestinationChoice)
        .setMobilityProviderMembership(carSharingCustomership);
	}

	public static Map<String, Boolean> carSharingCustomership() {
		Map<String, Boolean> carSharingCustomership = new HashMap<>();
		carSharingCustomership.put("company-one", true);
		carSharingCustomership.put("company-two", false);
		return carSharingCustomership;
	}

	public static PersonBuilder personOf(HouseholdForSetup household, int personNumber, Zone zone) {
		return personOf(household, personNumber, zone, ActivityType.HOME);
	}
	
	public static PersonBuilder personOf(HouseholdForSetup household, int personNumber, Zone zone, ActivityType activityType) {
		PersonId id = new PersonId(personNumber, household.getId(), personNumber);
		TourBasedActivityPattern activitySchedule = activitySchedule();
		PersonBuilder person = new DefaultPersonForSetup(id, household, age, employment, gender, graduation, income, ModeChoicePreferences.NOPREFERENCES)
		    .setHasBike(hasBike).setHasAccessToCar(hasAccessToCar).setHasPersonalCar(hasPersonalCar).setHasCommuterTicket(hasCommuterTicket).setHasDrivingLicense(hasLicense);
		person.setModeChoicePreferences(ModeChoicePreferences.NOPREFERENCES);
		person.addFixedDestination(new FixedDestination(activityType, zone, location));
		person.setPatternActivityWeek(activitySchedule);
		return person;
	}

	public static TourBasedActivityPattern activitySchedule() {
		PatternActivityWeek schedule = new PatternActivityWeek();
		schedule.addPatternActivity(activity());
		return TourBasedActivityPatternCreator.fromPatternActivityWeek(schedule);
	}

	public static ExtendedPatternActivity activity() {
		ActivityType activityType = ActivityType.getTypeFromInt(type);
		return activity(activityType);
	}

	public static TourBasedActivityPattern activitySchedule(ActivityType... activityType) {
		PatternActivityWeek schedule = new PatternActivityWeek();
		Stream.of(activityType).map(ExampleSetup::activity).forEach(schedule::addPatternActivity);
		return TourBasedActivityPatternCreator.fromPatternActivityWeek(schedule);
	}

	private static ExtendedPatternActivity activity(ActivityType activityType) {
		return new ExtendedPatternActivity(0,false,false,activityType, observedTripDuration, startTime, duration);
	}
}
