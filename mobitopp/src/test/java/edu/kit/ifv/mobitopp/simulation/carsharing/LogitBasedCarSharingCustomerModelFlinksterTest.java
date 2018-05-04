package edu.kit.ifv.mobitopp.simulation.carsharing;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.Before;

import java.util.Random;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.io.File;
import java.net.URISyntaxException;
import java.util.ArrayList;

import edu.kit.ifv.mobitopp.simulation.carsharing.LogitBasedCarSharingCustomerModel;
import edu.kit.ifv.mobitopp.simulation.carsharing.CarSharingDataForZone;
import edu.kit.ifv.mobitopp.simulation.carsharing.FreeFloatingCarSharingOrganization;
import edu.kit.ifv.mobitopp.simulation.carsharing.StationBasedCarSharingOrganization;
import edu.kit.ifv.mobitopp.simulation.carsharing.CarSharingStation;
import edu.kit.ifv.mobitopp.simulation.HouseholdForDemand;
import edu.kit.ifv.mobitopp.simulation.person.PersonForDemand;
import edu.kit.ifv.mobitopp.simulation.Employment;
import edu.kit.ifv.mobitopp.simulation.Gender;
import edu.kit.ifv.mobitopp.data.ExampleZones;
import edu.kit.ifv.mobitopp.data.Zone;

public class LogitBasedCarSharingCustomerModelFlinksterTest {

	private final static double EPSILON = 0.01;

	private	Random random;

	private LogitBasedCarSharingCustomerModel flinkster;

	private ExampleZones zones;
	private Map<String, Float> carsharingcarDensities;
	private CarSharingDataForZone carSharing;

	private HouseholdForDemand household_dummy;
	private HouseholdForDemand household;

	private PersonForDemand person_dummy;
	private PersonForDemand male_working;
	private PersonForDemand female_working;

	@Before
	public void setUp() throws URISyntaxException {

		random = new Random(1234);

		flinkster = new LogitBasedCarSharingCustomerModel(random, "Flinkster", parameterFile());

		zones = ExampleZones.create();

		carsharingcarDensities = new HashMap<String, Float>();
		carsharingcarDensities.put("Flinkster",0.0f);

		carSharing = new CarSharingDataForZone(
			zone(),
			new ArrayList<StationBasedCarSharingOrganization>(),
			new HashMap<String,List<CarSharingStation>>(),
			new ArrayList<FreeFloatingCarSharingOrganization>(),
			new HashMap<String, Boolean>(),
			new HashMap<String, Integer>(),
			carsharingcarDensities
		);
	
		zone().setCarSharing(carSharing);

		household_dummy = new HouseholdForDemand(
																			null, // id
																			6,
																			0, // domcode
																			zone(),
																			null, // Location
																			5, // not simulated people,
																			4, // number of cars,
																			3000, // income
																			true
																		);

		household = new HouseholdForDemand(
																			null, // id
																			4,
																			0, // domcode
																			zone(),
																			null, // Location
																			2, // not simulated people,
																			0, // number of cars
																			3000, // income
																			true
																		);

		person_dummy = new PersonForDemand(	
												0, //oid
												null, // id
												household,
												50, // age,
												Employment.NONE,
												Gender.MALE,
												0, // income
												false, true, false, false, 
												null // activitySchedule
										);

		male_working = new PersonForDemand(	
												1, //oid
												null, // id
												household,
												35, // age,
												Employment.FULLTIME,
												Gender.MALE,
												0, // income
												false, true, false, false, 
												null // activitySchedule
										);

		female_working = new PersonForDemand(	
												2, //oid
												null, // id
												household,
												30, // age,
												Employment.FULLTIME,
												Gender.FEMALE,
												0, // income
												false, true, false, false, 
												null // activitySchedule
										);

		household.addPerson(male_working);
		household.addPerson(female_working);

	}

	private String parameterFile() throws URISyntaxException {
		return new File(
				getClass().getResource("LogitBasedCarSharingCustomerModelParameter_Flinkster.txt").toURI())
						.getAbsolutePath();
	}

	private Zone zone() {
		return zones.someZone();
	}

	protected static HouseholdForDemand makeHousehold(
		int nominalSize,
		int number_of_cars
	) {
		return new HouseholdForDemand(
																			null, // id
																			nominalSize, // nominalSize
																			0, // domcode
																			null, // zone,
																			null, // Location
																			nominalSize-1, // not simulated people,
																			number_of_cars, // number of cars
																			3000, // income
																			true
																		);
	}

	protected static PersonForDemand makePerson(
		int age,
		Employment employment,
		Gender sex,
		boolean ticket
	) {
	
		return   new PersonForDemand(	
												0, //oid
												null, // id
												null, //household,
												age, // age,
												employment,
												sex,
												0, // income
												false, true, false, 
												ticket,
												null // activitySchedule
										);
	}

	@Test
	public void testConstructor() {

 		assertEquals(4, household.getSize());
 		assertEquals(0, household.getTotalNumberOfCars());

 		assertEquals(Employment.FULLTIME, male_working.employment());
 		assertEquals(35, male_working.age());
	}

	@Test
	public void testFlinksterDummy() {

		double expected = -6.0862;

 		assertEquals(expected, flinkster.calculateUtility(person_dummy,household_dummy,zone()), EPSILON);

		expected = -6.0862 + 0.55032 + 0.9263;

 		assertEquals(expected, flinkster.calculateUtility(male_working,household_dummy,zone()), EPSILON);

		expected = -6.0862 + -1.8967 + 1.1547 + 0.9263;

 		assertEquals(expected, flinkster.calculateUtility(female_working,household_dummy,zone()), EPSILON);
	}

	@Test
	public void testFlinksterMale() {

		double expected = -6.0862;

		PersonForDemand person = makePerson(50, 
																				Employment.NONE, 
																				Gender.MALE,
																				false
																				);

 		assertEquals(expected, flinkster.calculateUtility(person,household_dummy,zone()), EPSILON);
	}

	@Test
	public void testFlinksterMaleTicket() {

		double expected = -6.0862 + 0.1095;

		PersonForDemand person = makePerson(50, 
																				Employment.NONE, 
																				Gender.MALE,
																				true
																				);

 		assertEquals(expected, flinkster.calculateUtility(person,household_dummy,zone()), EPSILON);
	}

	@Test
	public void testFlinksterMaleFulltime() {

		double expected = -6.0862 + 0.9263 ;

		PersonForDemand person = makePerson(50, 
																				Employment.FULLTIME, 
																				Gender.MALE,
																				false
																				);

 		assertEquals(expected, flinkster.calculateUtility(person,household_dummy,zone()), EPSILON);
	}

	@Test
	public void testFlinksterFemaleFulltime() {

		double expected = -6.0862 + -1.8967 + 0.9263 ;

		PersonForDemand person = makePerson(50, 
																				Employment.FULLTIME, 
																				Gender.FEMALE,
																				false
																				);

 		assertEquals(expected, flinkster.calculateUtility(person,household_dummy,zone()), EPSILON);
	}

	@Test
	public void testFlinksterMaleAge24() {

		double expected = -6.0862 + -0.57665;

		PersonForDemand person = makePerson(24, 
																				Employment.NONE, 
																				Gender.MALE,
																				false
																				);

 		assertEquals(expected, flinkster.calculateUtility(person,household_dummy,zone()), EPSILON);
	}

	@Test
	public void testFlinksterMaleFulltimeAge30() {

		double expected = -6.0862 + 0.9263 + 1.1547;

		PersonForDemand person = makePerson(30, 
																				Employment.FULLTIME, 
																				Gender.MALE,
																				false
																				);

 		assertEquals(expected, flinkster.calculateUtility(person,household_dummy,zone()), EPSILON);
	}

	@Test
	public void testFlinksterFemaleFulltimeAge30() {

		double expected = -6.0862 + -1.8967 + 0.9263 + 1.1547;

		PersonForDemand person = makePerson(30, 
																				Employment.FULLTIME, 
																				Gender.FEMALE,
																				false
																				);

 		assertEquals(expected, flinkster.calculateUtility(person,household_dummy,zone()), EPSILON);
	}

	@Test
	public void testFlinksterMaleHouseholdCars() {

		double expected = -6.0862;

		PersonForDemand person = makePerson(50, 
																				Employment.NONE, 
																				Gender.MALE,
																				false
																				);

		HouseholdForDemand household = makeHousehold(5,5);

 		assertEquals(expected, flinkster.calculateUtility(person,household,zone()), EPSILON);


		expected = -6.0862 + 2.1474;

		household = makeHousehold(5,0);

 		assertEquals(expected, flinkster.calculateUtility(person,household,zone()), EPSILON);


		expected = -6.0862 + -0.27;

		household = makeHousehold(5,1);

 		assertEquals(expected, flinkster.calculateUtility(person,household,zone()), EPSILON);
	}

	@Test
	public void testFlinksterMaleHouseholdPersons() {

		double expected = -6.0862;

		PersonForDemand person = makePerson(50, 
																				Employment.NONE, 
																				Gender.MALE,
																				false
																				);

		HouseholdForDemand household = makeHousehold(5,5);

 		assertEquals(expected, flinkster.calculateUtility(person,household,zone()), EPSILON);


		expected = -6.0862 + -1.5638;

		household = makeHousehold(1,5);

 		assertEquals(1, household.nominalSize());
 		assertEquals(expected, flinkster.calculateUtility(person,household,zone()), EPSILON);


		expected = -6.0862 + -0.40315;

		household = makeHousehold(2,5);

 		assertEquals(2, household.nominalSize());
 		assertEquals(expected, flinkster.calculateUtility(person,household,zone()), EPSILON);


		expected = -6.0862 + 0.1005;

		household = makeHousehold(3,5);

 		assertEquals(3, household.nominalSize());
 		assertEquals(expected, flinkster.calculateUtility(person,household,zone()), EPSILON);


		expected = -6.0862 + 0.4201;

		household = makeHousehold(4,5);

 		assertEquals(4, household.nominalSize());
	}

	@Test
	public void testFlinksterMaleHouseholdSize1() {

		PersonForDemand person = makePerson(50, 
																				Employment.NONE, 
																				Gender.MALE,
																				false
																				);


		double expected = -6.0862 + -1.5638;

		household = makeHousehold(1,5);

		household.addPerson(person);

 		assertEquals(1, household.nominalSize());
 		assertEquals(1, household.getSize());
 		assertEquals(expected, flinkster.calculateUtility(person,household,zone()), EPSILON);
	}

	@Test
	public void testFlinksterMaleHouseholdSize2() {

		PersonForDemand person = makePerson(50, 
																				Employment.NONE, 
																				Gender.MALE,
																				false
																				);

		double expected = -6.0862 + -0.40315;

		household = makeHousehold(2,5);

		household.addPerson(person);

 		assertEquals(2, household.getSize());
 		assertEquals(expected, flinkster.calculateUtility(person,household,zone()), EPSILON);
	}

	@Test
	public void testFlinksterFemaleAge() {

		double expected = -6.0862 + -1.8967 + 0.6756;

		PersonForDemand person = makePerson(90, 
																				Employment.NONE, 
																				Gender.FEMALE,
																				false
																				);

		HouseholdForDemand household = makeHousehold(5,5);

 		assertEquals(expected, flinkster.calculateUtility(person,household,zone()), EPSILON);


		expected = -6.0862 + -1.8967 + -0.57665;

		person = makePerson(18, Employment.NONE, 
														Gender.FEMALE, false);

 		assertEquals(expected, flinkster.calculateUtility(person,household,zone()), EPSILON);

		person = makePerson(24, Employment.NONE, 
														Gender.FEMALE, false);

 		assertEquals(expected, flinkster.calculateUtility(person,household,zone()), EPSILON);


		expected = -6.0862 + -1.8967 + 1.1547;

		person = makePerson(25, Employment.NONE, 
														Gender.FEMALE, false);

 		assertEquals(expected, flinkster.calculateUtility(person,household,zone()), EPSILON);

		person = makePerson(34, Employment.NONE, 
														Gender.FEMALE, false);

 		assertEquals(expected, flinkster.calculateUtility(person,household,zone()), EPSILON);


		expected = -6.0862 + -1.8967 + 0.55032;

		person = makePerson(35, Employment.NONE, 
														Gender.FEMALE, false);

 		assertEquals(expected, flinkster.calculateUtility(person,household,zone()), EPSILON);

		person = makePerson(49, Employment.NONE, 
														Gender.FEMALE, false);

 		assertEquals(expected, flinkster.calculateUtility(person,household,zone()), EPSILON);


		expected = -6.0862 + -1.8967 + 0.0;

		person = makePerson(50, Employment.NONE, 
														Gender.FEMALE, false);

 		assertEquals(expected, flinkster.calculateUtility(person,household,zone()), EPSILON);

		person = makePerson(64, Employment.NONE, 
														Gender.FEMALE, false);

 		assertEquals(expected, flinkster.calculateUtility(person,household,zone()), EPSILON);


		expected = -6.0862 + -1.8967 + 0.6756;

		person = makePerson(65, Employment.NONE, 
														Gender.FEMALE, false);

 		assertEquals(expected, flinkster.calculateUtility(person,household,zone()), EPSILON);

		person = makePerson(80, Employment.NONE, 
														Gender.FEMALE, false);

 		assertEquals(expected, flinkster.calculateUtility(person,household,zone()), EPSILON);
	}

	@Test
	public void testFlinksterMaleHouseholdSize2Ticket() {

		PersonForDemand person = makePerson(50, 
																				Employment.NONE, 
																				Gender.MALE,
																				true
																				);

		double expected = -6.0862 + -0.40315 + 0.1095;

		household = makeHousehold(2,5);

		household.addPerson(person);

 		assertEquals(2, household.nominalSize());
 		assertEquals(2, household.getSize());
 		assertEquals(expected, flinkster.calculateUtility(person,household,zone()), EPSILON);
	}

	@Test
	public void testFlinksterFemaleEmployment() {

		double expected = -6.0862 + -1.8967 + -0.57665;

		PersonForDemand person = makePerson(20, 
																				Employment.NONE, 
																				Gender.FEMALE,
																				false
																				);

		HouseholdForDemand household = makeHousehold(5,5);

 		assertEquals(expected, flinkster.calculateUtility(person,household,zone()), EPSILON);


		expected = -6.0862 + -1.8967 + -0.57665 + 0.9263;

		person = makePerson(20, Employment.FULLTIME, 
														Gender.FEMALE, false);

 		assertEquals(expected, flinkster.calculateUtility(person,household,zone()), EPSILON);


		expected = -6.0862 + -1.8967 + -0.57665 + 1.1843;

		person = makePerson(20, Employment.PARTTIME, 
														Gender.FEMALE, false);

 		assertEquals(expected, flinkster.calculateUtility(person,household,zone()), EPSILON);


		expected = -6.0862 + -1.8967 + -0.57665 + -2.1083;

		person = makePerson(20, Employment.UNEMPLOYED, 
														Gender.FEMALE, false);

 		assertEquals(expected, flinkster.calculateUtility(person,household,zone()), EPSILON);


		expected = -6.0862 + -1.8967 + -0.57665 + 0.0458;

		person = makePerson(20, Employment.STUDENT_SECONDARY, 
														Gender.FEMALE, false);

 		assertEquals(expected, flinkster.calculateUtility(person,household,zone()), EPSILON);


		expected = -6.0862 + -1.8967 + -0.57665 + 0.6085;

		person = makePerson(20, Employment.STUDENT_TERTIARY, 
														Gender.FEMALE, false);

 		assertEquals(expected, flinkster.calculateUtility(person,household,zone()), EPSILON);


		expected = -6.0862 + -1.8967 + -0.57665 + -0.8818;

		person = makePerson(20, Employment.EDUCATION, 
														Gender.FEMALE, false);

 		assertEquals(expected, flinkster.calculateUtility(person,household,zone()), EPSILON);


		expected = -6.0862 + -1.8967 + -0.57665 + -0.9886;

		person = makePerson(20, Employment.HOMEKEEPER, 
														Gender.FEMALE, false);

 		assertEquals(expected, flinkster.calculateUtility(person,household,zone()), EPSILON);


		expected = -6.0862 + -1.8967 + -0.57665 + -1.0008;

		person = makePerson(20, Employment.RETIRED, 
														Gender.FEMALE, false);

 		assertEquals(expected, flinkster.calculateUtility(person,household,zone()), EPSILON);
	}

	@Test
	public void testReal1() {

		PersonForDemand person = makePerson(46, 
																				Employment.FULLTIME,
																				Gender.MALE,
																				false
																				);

		double expected = -6.0862 + 0.9263 + 0.55032 + 0.0 + -0.27;

		household = makeHousehold(5,1);

 		assertEquals(5, household.nominalSize());
 		assertEquals(expected, flinkster.calculateUtility(person,household,zone()), EPSILON);
	}

	@Test
	public void testFlinkster() {
		double expected_male = -6.0862 + 0.55032 + 2.1474 + 0.4201 + 0.9263;

		assertEquals(expected_male, flinkster.calculateUtility(male_working, household, zone()), EPSILON);
	}

}

