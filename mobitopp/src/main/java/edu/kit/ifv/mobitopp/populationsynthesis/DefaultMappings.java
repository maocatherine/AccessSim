package edu.kit.ifv.mobitopp.populationsynthesis;

import edu.kit.ifv.mobitopp.data.local.DynamicTypeMapping;
import edu.kit.ifv.mobitopp.data.local.TypeMapping;
import edu.kit.ifv.mobitopp.data.local.configuration.TravelTimeMatrixType;
import edu.kit.ifv.mobitopp.simulation.Mode;

public abstract class DefaultMappings {

  public static TypeMapping noAutonomousModes() {
    return createNormalModes();
  }

  private static DynamicTypeMapping createNormalModes() {
    DynamicTypeMapping types = new DynamicTypeMapping();
    types.add(Mode.BIKE, TravelTimeMatrixType.bike);
    types.add(Mode.CAR, TravelTimeMatrixType.car);
    types.add(Mode.CARSHARING_FREE, TravelTimeMatrixType.car);
    types.add(Mode.CARSHARING_STATION, TravelTimeMatrixType.car);
    types.add(Mode.PASSENGER, TravelTimeMatrixType.car);
    types.add(Mode.PEDELEC, TravelTimeMatrixType.bike);
    types.add(Mode.PEDESTRIAN, TravelTimeMatrixType.pedestrian);
    types.add(Mode.PUBLICTRANSPORT, TravelTimeMatrixType.publictransport);
    types.add(Mode.TRUCK, TravelTimeMatrixType.truck);
    return types;
  }
  
  public static TypeMapping autonomousModes() {
    DynamicTypeMapping types = createNormalModes();
    types.add(Mode.AUTONOMOUS_TAXI, TravelTimeMatrixType.car);
    types.add(Mode.RIDE_POOLING, TravelTimeMatrixType.car);
    return types;
  }

}