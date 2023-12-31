package edu.kit.ifv.mobitopp.data.local;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.Before;
import org.junit.Test;

import edu.kit.ifv.mobitopp.data.CostMatrix;
import edu.kit.ifv.mobitopp.data.TravelTimeMatrix;
import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.data.ZoneId;
import edu.kit.ifv.mobitopp.data.ZoneRepository;
import edu.kit.ifv.mobitopp.publictransport.connectionscan.PublicTransportRoute;
import edu.kit.ifv.mobitopp.publictransport.model.Data;
import edu.kit.ifv.mobitopp.simulation.ActivityType;
import edu.kit.ifv.mobitopp.simulation.Location;
import edu.kit.ifv.mobitopp.simulation.StandardMode;
import edu.kit.ifv.mobitopp.time.Time;

public class ImpedanceLocalDataTest {

  private static final int originOid = 0;
  private static final int destinationOid = 1;
  private static final ZoneId origin = new ZoneId("1", originOid);
	private static final ZoneId destination = new ZoneId("2", destinationOid);
	private static final Time date = Data.someTime();
	private static final Location sourceLocation = null;
	private static final Location targetLocation = null;
	private static final StandardMode mode = StandardMode.CAR;
	private static final float cost = 1.0f;
	private static final float distance = 1.0f;
	private static final float storedParkingCost = 1.0f;
	private static final float storedParkingStress = 1.0f;
	private static final float storedConstant = 1.0f;
	private static final float storedTravelTime = 1.0f;
	
	private ImpedanceLocalData impedance;
	private ActivityType activityType;
	private Matrices matrices;
	private ZoneRepository zoneRepository;
	private Zone zone;

	@Before
	public void initialise() {
		matrices = mock(Matrices.class);
		zoneRepository = mock(ZoneRepository.class);
		zone = mock(Zone.class);
		when(zoneRepository.getZoneById(origin)).thenReturn(zone);
		initialiseMatrices();
		impedance = new ImpedanceLocalData(matrices, zoneRepository, date);
	}

	private void initialiseMatrices() {
		TravelTimeMatrix travelTimeMatrix = travelTimeMatrix();
		when(matrices.travelTimeFor(mode, date)).thenReturn(travelTimeMatrix);
		CostMatrix travelCostMatrix = costMatrix(cost);
		when(matrices.travelCostFor(mode, date)).thenReturn(travelCostMatrix);
		CostMatrix distanceMatrix = costMatrix(distance);
		when(matrices.distanceMatrix(date)).thenReturn(distanceMatrix);
		CostMatrix parkingCostMatrix = parkingMatrix(storedParkingCost);
		when(matrices.parkingCostMatrix(date)).thenReturn(parkingCostMatrix);
		CostMatrix parkingStressMatrix = parkingMatrix(storedParkingStress);
		when(matrices.parkingStressMatrix(date)).thenReturn(parkingStressMatrix);
		CostMatrix constantMatrix = costMatrix(storedConstant);
		when(matrices.constantMatrix(date)).thenReturn(constantMatrix);
	}

	private TravelTimeMatrix travelTimeMatrix() {
		TravelTimeMatrix travelTimeMatrix = new TravelTimeMatrix(asList(origin, destination));
		travelTimeMatrix.set(originOid, destinationOid, storedTravelTime);
		return travelTimeMatrix;
	}

	private CostMatrix costMatrix(float cost) {
		CostMatrix costMatrix = new CostMatrix(asList(origin, destination));
		costMatrix.set(originOid, destinationOid, cost);
		return costMatrix;
	}
	
	private CostMatrix parkingMatrix(float cost) {
		CostMatrix costMatrix = new CostMatrix(asList(origin, destination));
		costMatrix.set(destinationOid, destinationOid, cost);
		return costMatrix;
	}
	
	@Test
	public void getTravelTime() {
		float travelTime = impedance.getTravelTime(origin, destination, mode, date);
		
		assertThat(travelTime).isEqualTo(storedTravelTime);

		verify(matrices).travelTimeFor(mode, date);
	}

	@Test
	public void getPublicTransportRoute() {
		Optional<PublicTransportRoute> publicTransportRoute = impedance.getPublicTransportRoute(
				sourceLocation, targetLocation, mode, date);

		assertThat(publicTransportRoute).isEmpty();
	}
	
	@Test
	public void noCostForBikePassengerAndPedestrian() {
		float bikeCost = impedance.getTravelCost(origin, destination, StandardMode.BIKE, date);
		float passangerCost = impedance.getTravelCost(origin, destination, StandardMode.PASSENGER, date);
		float pedestrianCost = impedance.getTravelCost(origin, destination, StandardMode.PEDESTRIAN, date);
		
		assertThat(bikeCost).isEqualTo(0.0f);
		assertThat(passangerCost).isEqualTo(0.0f);
		assertThat(pedestrianCost).isEqualTo(0.0f);
	}

	@Test
	public void getTravelCost() {
		float travelCost = impedance.getTravelCost(origin, destination, mode, date);
		
		assertThat(travelCost).isEqualTo(cost);

		verify(matrices).travelCostFor(mode, date);
	}

	@Test
	public void getDistance() {
		float travelDistance = impedance.getDistance(origin, destination);
		
		assertThat(travelDistance).isEqualTo(distance);

		verify(matrices).distanceMatrix(date);
	}

	@Test
	public void getParkingCost() {
		float parkingCost = impedance.getParkingCost(destination, date);
		
		assertThat(parkingCost).isEqualTo(storedParkingCost);

		verify(matrices).parkingCostMatrix(date);
	}

	@Test
	public void getParkingStress() {
		float parkingStress = impedance.getParkingStress(destination, date);
		
		assertThat(parkingStress).isEqualTo(storedParkingStress);

		verify(matrices).parkingStressMatrix(date);
	}

	@Test
	public void getConstant() {
		float constant = impedance.getConstant(origin, destination, date);

		assertThat(constant).isEqualTo(storedConstant);
		
		verify(matrices).constantMatrix(date);
	}

	@Test
	public void getOpportunities() {
		impedance.getOpportunities(activityType, origin);

		verify(zoneRepository).getZoneById(origin);
		verify(zone).getAttractivity(activityType);
	}
}
