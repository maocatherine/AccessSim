package edu.kit.ifv.mobitopp.simulation.car;


import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.populationsynthesis.carownership.CarType;
import edu.kit.ifv.mobitopp.result.CsvBuilder;
import edu.kit.ifv.mobitopp.simulation.Car;
import edu.kit.ifv.mobitopp.simulation.Person;
import edu.kit.ifv.mobitopp.time.Time;
import edu.kit.ifv.mobitopp.simulation.IdSequence;


import java.io.Serializable;

import java.util.List;
import java.util.ArrayList;


public class ConventionalCar
	implements Car 
		, Serializable
{

	private static final long serialVersionUID = 1L;
	
	protected final int id;
	protected final Car.Segment carSegment;
	protected final int maxRange;

	protected final float consumptionRate;

	protected final int capacity;

	protected float mileage = 0.0f;
	protected float fuelLevel = 1.0f;

	protected Person driver;
	protected List<Person> passengers = new ArrayList<Person>();
	protected Time startUsage;
	protected Time endUsage;


	protected boolean isRunning = false;
	protected CarPosition position = null;


	public ConventionalCar(
			int id, CarPosition position, Segment segment, int capacity, float initialMileage,
			float fuelLevel, int maxRange) {
		super();
		this.id = id;
		this.carSegment = segment;
		this.capacity = capacity;

		this.maxRange = maxRange;
		this.consumptionRate = 1.0f/this.maxRange;

		this.mileage = initialMileage;
		this.fuelLevel = fuelLevel;

		this.position = position;
	}

	public ConventionalCar(
			IdSequence ids, CarPosition position, Car.Segment carSegment, int capacity,
			float initialMileage, float fuelLevel, int maxRange) {
		this(ids.nextId(), position, carSegment, capacity, initialMileage, fuelLevel, maxRange);
	}

	public ConventionalCar(
		IdSequence ids,
		CarPosition position,
		Car.Segment carSegment
	) {
		this(ids, position, carSegment, 4, 0.0f,1.0f,1000);
	}


	public int id() {
		return id;
	}

	public Car.Segment carSegment() {
		return carSegment;
	}

	public float currentMileage() {
		return mileage;
	}

	public float currentFuelLevel() {
		return fuelLevel;
	}

	public float currentBatteryLevel() {
		return 0.0f;
	}

	public int maxRange() {
		return maxRange;
	}

	public int remainingRange() {

		return (int) Math.floor(fuelLevel*maxRange);
	}

	public Integer effectiveRange() {
		return Integer.MAX_VALUE;
	}



	public void refuel(float aimedFuelLevel) {

		if (aimedFuelLevel < 0.0f || aimedFuelLevel > 1.0f) {

			throw(new IllegalArgumentException("aimedFuelLevel must be between 0.0 and 1.0"));
		}

		fuelLevel = aimedFuelLevel > fuelLevel ? Math.min(1.0f,aimedFuelLevel) : fuelLevel; 
	}

	public void driveDistance(float distanceKm) {
		if (distanceKm <= 0.0f) {
			throw(new IllegalArgumentException("distance must be positive"));
		}

		while (distanceKm > remainingRange() ) {
			distanceKm -= remainingRange();
			driveInternal(remainingRange());
			refuel(1.0f);
			System.out.println("Refueling !!!");
			System.out.println("Remaining: " + remainingRange() + "\n" );
		}

		driveInternal(distanceKm);
	}

	protected void driveInternal(float distanceKm) {

		mileage += distanceKm;
		fuelLevel -= consumptionRate*distanceKm;
	}

	public String getType() {
		return CarType.conventional.getName();
	}

	public String toString() {

		return "Car(id=" + id + ",type=" + getType() + ",mileage=" + mileage + ")";
	}

	public boolean isUsed() {
		return hasDriver();
	}

  @Override
  public boolean hasDriver() {
    return this.driver != null;
  }

	public void use(Person person, Time time) {
		assert !isUsed();

		this.driver = person;
		this.startUsage = time;
	}

	public Person driver() {
		assert isUsed();

		return this.driver;
	}

	public void release(Person person, Time time) {
		assert driver() == person;

		this.driver = null;
		this.endUsage = time;
	}

  public void returnCar(Zone zone) {
    
  }

	public Time startOfLastUsage() {
		return this.startUsage;
	}

	public Time endOfLastUsage() {
		return this.endUsage;
	}

	@Override
	public boolean canCarryPassengers() {
	  return hasDriver() && hasCapacity();
	}

  @Override
  public boolean hasCapacity() {
    return remainingCapacity() > 0;
  }

	public void useAsPassenger(Person person) {
		assert canCarryPassengers();

		this.passengers.add(person);
	}

	public void leave(Person person) {
		assert this.passengers.contains(person);

		this.passengers.remove(person);
	}

	public int capacity() {
		return this.capacity;
	}

	public int remainingCapacity() {
		return this.capacity - (isUsed() ? 1 : 0) - passengers.size();
	}

	public String passengersAsString() {

		String pass = "";

		for (Person passenger : passengers) {
			pass += "," + passenger.getOid();
		}

		return pass.length() == 0 ? "" : pass.substring(1);
	}

  public void start(Time currentTime){
		assert !this.isRunning;
		assert this.position != null;

		this.isRunning = true;
		this.position = null;
	}

  public void stop(Time currentTime, CarPosition position){
		assert this.isRunning;
		assert this.position == null;
		assert position != null;

		this.isRunning = false;
		this.position = position;
	}

  public boolean isStarted(){

		return this.isRunning;
	}

  public boolean isStopped(){

		return !this.isRunning;
	}

	public CarPosition position() {
		assert this.position != null;

		return this.position;
	}


	public String forLogging() {

		StringBuffer buffer = new StringBuffer();
	
		buffer.append(id() + "; ");
		buffer.append(carSegment() + "; ");
		buffer.append(getType().substring(0,1).toUpperCase() + "; ");
		buffer.append(maxRange() + "; ");
		buffer.append(currentMileage() + "; ");
		buffer.append(currentFuelLevel() + "; ");

		return buffer.toString();
	}

	@Override
	public String statusForLogging() {
		String cartype 					= getType().substring(0,1).toUpperCase();
		int carid 							= id();
		Car.Segment carSegment 	= carSegment();

		int driveroid						= driver().getOid();
		String passengers 			= "(" + passengersAsString() + ")";

		String electricCarMode 	= "-";

		double currentMileage 	= Math.floor(10.0f*currentMileage())/10.0f;
		double fuelLevel 				= Math.floor(100.0f*currentFuelLevel())/100.0f;
		double batteryLevel 		= Math.floor(100.0f*currentBatteryLevel())/100.0f;
		int remainingRange 			= remainingRange();

		CsvBuilder builder = new CsvBuilder();
		builder.append(cartype);
		builder.append(carid);
		builder.append(carSegment);
		builder.append(currentMileage);
		builder.append(fuelLevel);
		builder.append(batteryLevel);
		builder.append(electricCarMode);
		builder.append(remainingRange);
		builder.append(driveroid);
		builder.append(passengers);
		return builder.toString();
	}

	public boolean isElectric() {
		return false;
	}

}
