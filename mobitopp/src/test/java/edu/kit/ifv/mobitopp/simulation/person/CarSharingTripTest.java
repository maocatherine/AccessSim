package edu.kit.ifv.mobitopp.simulation.person;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.simulation.ActivityType;
import edu.kit.ifv.mobitopp.simulation.ImpedanceIfc;
import edu.kit.ifv.mobitopp.simulation.TripIfc;
import edu.kit.ifv.mobitopp.simulation.carsharing.CarSharingCar;
import edu.kit.ifv.mobitopp.simulation.carsharing.CarSharingDataForZone;
import edu.kit.ifv.mobitopp.time.Time;

public class CarSharingTripTest {

  private TripSetup setup;
  private ImpedanceIfc impedance;
  private SimulationPerson person;
  private Time currentTime;
  private TripIfc trip;
  private Zone zone;
  private CarSharingDataForZone carSharingData;

  @BeforeEach
  public void initialise() {
    setup = TripSetup.create();
    impedance = setup.impedance;
    person = setup.person;
    trip = setup.trip;
    zone = setup.zone;
    currentTime = setup.currentTime;
    carSharingData = mock(CarSharingDataForZone.class);
    zone.setCarSharing(carSharingData);
  }

  @Test
  void startTrip() throws Exception {
    CarSharingCar carSharingCar = mock(CarSharingCar.class);
    setup.configureActivity(ActivityType.HOME);
    when(carSharingData.isStationBasedCarSharingCarAvailable(person)).thenReturn(true);
    when(carSharingData.bookStationBasedCar(person)).thenReturn(carSharingCar);
    CarSharingTrip carSharingTrip = new CarSharingTrip(trip, person);
    
    carSharingTrip.startTrip(impedance, currentTime);
    
    verify(person).useCar(carSharingCar, currentTime);
    verify(carSharingData).bookStationBasedCar(person);
  }
}
