package edu.kit.ifv.mobitopp.simulation;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import edu.kit.ifv.mobitopp.simulation.person.PersonForDemand;


public class HouseholdTypeTest {
	
	Person personSingle;
	
	Person person1Couple;
	Person person2Couple;
	
	Person person1Multiadult;
	Person person2Multiadult;
	Person person3Multiadult;
	
	Person person1Multigeneration;
	Person person2Multigeneration;
	
	Person adultWithKids0to7;
	Person kid1WithKids0to7;
	Person kid2WithKids0to7;
	
	Person adult1WithKids8to12;
	Person adult2WithKids8to12;
	Person kid1WithKids8to12;
	Person kid2WithKids8to12;
	
	Person adult1WithKids13plus;
	Person adult2WithKids13plus;
	Person kidWithKids13plus;
	Person adultkidWithKids13plus;
	
	Household single;
	Household couple;
	Household multiadult;
	Household multigeneration;
	Household withKids0to7;
	Household withKids8to12;
	Household withKids13plus;
	
	@Before
	public void setUp() {
		
		single = new HouseholdForDemand(null, 1, 0, null, null, 0, 0, 0, false);
		couple = new HouseholdForDemand(null, 2, 0, null, null, 0, 0, 0, false);
		multiadult = new HouseholdForDemand(null, 3, 0, null, null, 0, 0, 0, false);
		multigeneration = new HouseholdForDemand(null, 2, 0, null, null, 0, 0, 0, false);
		withKids0to7 = new HouseholdForDemand(null, 3, 0, null, null, 0, 0, 0, false);
		withKids8to12 = new HouseholdForDemand(null, 4, 0, null, null, 0, 0, 0, false);
		withKids13plus = new HouseholdForDemand(null, 4, 0, null, null, 0, 0, 0, false);
		
		personSingle = newPerson(single, 20);
		single.addPerson(personSingle);
		
		person1Couple = newPerson(couple, 20);
		person2Couple = newPerson(couple, 22);
		couple.addPerson(person1Couple);
		couple.addPerson(person2Couple);
		
		person1Multiadult = newPerson(multiadult, 20);
		person2Multiadult = newPerson(multiadult, 25);
		person3Multiadult = newPerson(multiadult, 30);
		multiadult.addPerson(person1Multiadult);
		multiadult.addPerson(person2Multiadult);
		multiadult.addPerson(person3Multiadult);
		
		person1Multigeneration = newPerson(multigeneration, 20);
		person2Multigeneration = newPerson(multigeneration, 40);
		multigeneration.addPerson(person1Multigeneration);
		multigeneration.addPerson(person2Multigeneration);
		
		adultWithKids0to7 = newPerson(withKids0to7, 30);
		kid1WithKids0to7 = newPerson(withKids0to7, 10);
		kid2WithKids0to7 = newPerson(withKids0to7, 5);
		withKids0to7.addPerson(adultWithKids0to7);
		withKids0to7.addPerson(kid1WithKids0to7);
		withKids0to7.addPerson(kid2WithKids0to7);
	
		adult1WithKids8to12 = newPerson(withKids8to12, 40);
		adult2WithKids8to12 = newPerson(withKids8to12, 35);
		kid1WithKids8to12 = newPerson(withKids8to12, 12);
		kid2WithKids8to12 = newPerson(withKids8to12, 10);
		withKids8to12.addPerson(adult1WithKids8to12);
		withKids8to12.addPerson(adult2WithKids8to12);
		withKids8to12.addPerson(kid1WithKids8to12);
		withKids8to12.addPerson(kid2WithKids8to12);
	
		adult1WithKids13plus = newPerson(withKids13plus, 50);
		adult2WithKids13plus = newPerson(withKids13plus, 45);
		kidWithKids13plus = newPerson(withKids13plus, 13);
		adultkidWithKids13plus = newPerson(withKids13plus, 19);
		withKids13plus.addPerson(adult1WithKids13plus);
		withKids13plus.addPerson(adult2WithKids13plus);
		withKids13plus.addPerson(kidWithKids13plus);
		withKids13plus.addPerson(adultkidWithKids13plus);
	}

  private PersonForDemand newPerson(Household household, int age) {
    Graduation graduation = Graduation.undefined;
    return new PersonForDemand(null, household, age, null, null, graduation, 0, false, false, false, false, false, null, null, null, null);
  }
	
	@Test
	public void testSingle() {
		assertEquals(HouseholdType.SINGLE, HouseholdType.type(single));
	}
	
	@Test
	public void testCouple() {
		assertEquals(HouseholdType.COUPLE, HouseholdType.type(couple));
	}
	
	@Test
	public void testMultiadult() {
		assertEquals(HouseholdType.MULTIADULT, HouseholdType.type(multiadult));
	}
	
	@Test
	public void testMultigeneration() {
		assertEquals(HouseholdType.MULTIADULT, HouseholdType.type(multigeneration));
	}
	
	@Test
	public void testwithKids0to7() {
		assertEquals(HouseholdType.KIDS0TO7, HouseholdType.type(withKids0to7));
	}
	
	@Test
	public void testWithKids8to12() {
		assertEquals(HouseholdType.KIDS8TO12, HouseholdType.type(withKids8to12));
	}
	
	@Test
	public void testWithKids13plus() {
		assertEquals(HouseholdType.KIDS13PLUS, HouseholdType.type(withKids13plus));
	}

}
