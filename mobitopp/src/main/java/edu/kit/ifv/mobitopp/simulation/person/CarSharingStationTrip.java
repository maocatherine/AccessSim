package edu.kit.ifv.mobitopp.simulation.person;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.simulation.Car;
import edu.kit.ifv.mobitopp.simulation.ImpedanceIfc;
import edu.kit.ifv.mobitopp.simulation.Location;
import edu.kit.ifv.mobitopp.simulation.PersonListener;
import edu.kit.ifv.mobitopp.simulation.TripData;
import edu.kit.ifv.mobitopp.simulation.BaseTrip;
import edu.kit.ifv.mobitopp.simulation.Trip;
import edu.kit.ifv.mobitopp.simulation.carsharing.CarSharingCar;
import edu.kit.ifv.mobitopp.time.Time;

public class CarSharingStationTrip extends BaseTrip implements Trip {

  public CarSharingStationTrip(TripData trip, SimulationPerson person) {
    super(trip, person);
  }
  
  @Override
  public void prepareTrip(ImpedanceIfc impedance, Time currentTime) {
    if (person().currentActivity().activityType().isHomeActivity()) {
      allocateCar(currentTime);
    }
  }

  private void allocateCar(Time currentTime) {
    Zone zone = person().currentActivity().zone();
    assert zone.carSharing().isStationBasedCarSharingCarAvailable(person());

    Car car = zone.carSharing().bookStationBasedCar(person());

    person().useCar(car, currentTime);
  }
  
  @Override
  public FinishedTrip finish(Time currentDate, PersonListener listener) {
    FinishedTrip finishedTrip = super.finish(currentDate, listener);
    Zone zone = nextActivity().zone();
    if (nextActivity().activityType().isHomeActivity()) {
      Car car = person().releaseCar(currentDate);
      car.returnCar(zone);
    } else {
      Location location = nextActivity().location();
      Car car = person().parkCar(zone, location, currentDate);
      assert car instanceof CarSharingCar;
    }
    return finishedTrip;
  }

}
