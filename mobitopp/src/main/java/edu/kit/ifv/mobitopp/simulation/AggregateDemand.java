package edu.kit.ifv.mobitopp.simulation;

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import edu.kit.ifv.mobitopp.data.IntegerMatrix;
import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.data.ZoneId;
import edu.kit.ifv.mobitopp.routing.Path;
import edu.kit.ifv.mobitopp.simulation.activityschedule.ActivityIfc;
import edu.kit.ifv.mobitopp.simulation.person.FinishedTrip;
import edu.kit.ifv.mobitopp.simulation.person.StartedTrip;
import edu.kit.ifv.mobitopp.simulation.tour.Subtour;
import edu.kit.ifv.mobitopp.simulation.tour.Tour;

public class AggregateDemand implements PersonListener {

  private final Consumer<IntegerMatrix> writer;
  private final IntegerMatrix matrix;

  public AggregateDemand(Consumer<IntegerMatrix> writer, List<ZoneId> ids) {
    super();
    this.writer = writer;
    matrix = new IntegerMatrix(Collections.unmodifiableList(ids));
  }

  @Override
  public void notifyEndTrip(Person person, FinishedTrip trip) {
    ZoneId origin = trip.origin().zone().getId();
    ZoneId destination = trip.destination().zone().getId();
    matrix.increment(origin, destination);
  }
  
  @Override
  public void notifyStartTrip(Person person, StartedTrip trip) {
  }
  
  @Override
  public void notifyFinishSimulation() {
    writer.accept(matrix);
  }

  @Override
  public void notifyFinishCarTrip(Person person, Car car, FinishedTrip trip, ActivityIfc activity) {
  }

  @Override
  public void notifyStartActivity(Person person, ActivityIfc activity) {
  }

  @Override
  public void notifySelectCarRoute(Person person, Car car, TripData trip, Path route) {
  }

  @Override
  public void writeSubtourinfoToFile(Person person, Tour tour, Subtour subtour, Mode tourMode) {
  }

  @Override
  public void writeTourinfoToFile(Person person, Tour tour, Zone tourDestination, Mode tourMode) {
  }

  @Override
  public void notifyStateChanged(StateChange stateChange) {
  }

}
