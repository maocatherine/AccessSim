package edu.kit.ifv.mobitopp.simulation.person;

import java.util.Optional;

import edu.kit.ifv.mobitopp.publictransport.connectionscan.PublicTransportRoute;
import edu.kit.ifv.mobitopp.simulation.ImpedanceIfc;
import edu.kit.ifv.mobitopp.simulation.Mode;
import edu.kit.ifv.mobitopp.simulation.Trip;
import edu.kit.ifv.mobitopp.simulation.TripIfc;
import edu.kit.ifv.mobitopp.simulation.activityschedule.ActivityIfc;
import edu.kit.ifv.mobitopp.time.RelativeTime;
import edu.kit.ifv.mobitopp.time.Time;

public class DefaultTripFactory implements TripFactory {

  private int tripCount;

  public DefaultTripFactory() {
    super();
    tripCount = 0;
  }

  @Override
  public TripIfc createTrip(
      SimulationPerson person, ImpedanceIfc impedance, Mode mode, ActivityIfc previousActivity,
      ActivityIfc nextActivity) {
    assert mode != null;
    assert previousActivity != null;
    assert nextActivity != null;

    assert previousActivity.isLocationSet();
    assert nextActivity.isLocationSet();

    int sourceZoneOid = previousActivity.zone().getOid();
    int targetZoneOid = nextActivity.zone().getOid();

    Time plannedEnd = previousActivity.calculatePlannedEndDate();

    int matrixDuration = (int) impedance
        .getTravelTime(sourceZoneOid, targetZoneOid, mode, plannedEnd);
    Optional<PublicTransportRoute> route = Optional.empty();
    if (Mode.PUBLICTRANSPORT.equals(mode)) {
      route = findRoute(impedance, mode, previousActivity, nextActivity, plannedEnd);
    }

    int duration = route
        .map(PublicTransportRoute::duration)
        .map(RelativeTime::toMinutes)
        .map(Long::intValue)
        .orElse(matrixDuration);
    duration = Math.max(1, duration);

    if (duration > java.lang.Short.MAX_VALUE) {
      System.out.println("WARNING: duration > java.lang.Short.MAX_VALUE");
      duration = java.lang.Short.MAX_VALUE;
    }

    assert duration > 0;

    TripIfc trip = new Trip(++tripCount, mode, previousActivity, nextActivity, (short) duration);

    if (Mode.PUBLICTRANSPORT.equals(mode)) {
      trip = PublicTransportTrip.of(trip, route);
    }

    return trip;
  }

  protected Optional<PublicTransportRoute> findRoute(
      ImpedanceIfc impedance, Mode modeType, ActivityIfc previousActivity, ActivityIfc nextActivity,
      Time currentTime) {
    return impedance
        .getPublicTransportRoute(previousActivity.location(), nextActivity.location(), modeType,
            currentTime);
  }
}
