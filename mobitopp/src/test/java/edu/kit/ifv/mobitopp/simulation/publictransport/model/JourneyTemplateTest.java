package edu.kit.ifv.mobitopp.simulation.publictransport.model;

import static edu.kit.ifv.mobitopp.publictransport.model.Data.anotherStop;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.coordinate;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.otherStop;
import static edu.kit.ifv.mobitopp.publictransport.model.Data.someStop;
import static edu.kit.ifv.mobitopp.publictransport.model.StopBuilder.stop;
import static edu.kit.ifv.mobitopp.simulation.publictransport.TransportSystemHelper.dummySet;
import static edu.kit.ifv.mobitopp.simulation.publictransport.model.VisumBuilder.visumJourney;
import static edu.kit.ifv.mobitopp.simulation.publictransport.model.VisumBuilder.visumLineRoute;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.awt.geom.Point2D;
import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.junit.Before;
import org.junit.Test;

import edu.kit.ifv.mobitopp.publictransport.model.Connection;
import edu.kit.ifv.mobitopp.publictransport.model.Data;
import edu.kit.ifv.mobitopp.publictransport.model.Journey;
import edu.kit.ifv.mobitopp.publictransport.model.ModifiableJourney;
import edu.kit.ifv.mobitopp.publictransport.model.Stop;
import edu.kit.ifv.mobitopp.time.RelativeTime;
import edu.kit.ifv.mobitopp.time.Time;
import edu.kit.ifv.mobitopp.visum.VisumPtLineRoute;
import edu.kit.ifv.mobitopp.visum.VisumPtLineRouteElement;
import edu.kit.ifv.mobitopp.visum.VisumPtStopArea;
import edu.kit.ifv.mobitopp.visum.VisumPtStopPoint;
import edu.kit.ifv.mobitopp.visum.VisumPtTimeProfile;
import edu.kit.ifv.mobitopp.visum.VisumPtTimeProfileElement;
import edu.kit.ifv.mobitopp.visum.VisumPtVehicleJourney;
import nl.jqno.equalsverifier.EqualsVerifier;

public class JourneyTemplateTest {

	@SuppressWarnings("serial")
	private static class DummyVisumStopPoint extends VisumPtStopPoint {

		private static final Point2D somePoint = new Point2D.Float();

		public DummyVisumStopPoint(int id, String code, String name) {
			super(id, stopArea(), code, name, 0, dummySet(), false);
		}

		private static VisumPtStopArea stopArea() {
			return new VisumPtStopArea(0, null, null, null, null, 0, 0, 0);
		}

		@Override
		public Point2D coordinate() {
			return somePoint;
		}
	}

	private static final Time someDate = Data.someTime();
	private static final RelativeTime someRelativeTime = RelativeTime.ofSeconds(0);
	private static final RelativeTime oneMinute = RelativeTime.ofMinutes(1);
	private static final RelativeTime twoMinutes = RelativeTime.ofMinutes(2);
	private static final Time someTime = someDate;
	private static final Time anotherTime = someTime.plus(oneMinute);
	private static final Time otherTime = someTime.plus(twoMinutes);
	private static final Time threeMinutesLater = someTime.plusMinutes(3);
	private static final Time fourMinutesLater = someTime.plusMinutes(4);

	private PublicTransportFactory factory;
	private VisumStopResolver stopResolver;

	@Before
	public void initialise() throws Exception {
		factory = mock(PublicTransportFactory.class);
		stopResolver = mock(VisumStopResolver.class);
	}

	@Test
	public void creationFromVisum() throws Exception {
		resolve(someStop());
		resolve(anotherStop());

		JourneyTemplate timeProfile = JourneyTemplate.from(timeProfile(), stopResolver);

		JourneyTemplate expectedProfile = new JourneyTemplate("name");
		expectedProfile.add(someStop(), 1, someRelativeTime, someRelativeTime);
		expectedProfile.add(anotherStop(), 2, oneMinute, oneMinute);
		assertThat(timeProfile, is(equalTo(expectedProfile)));
	}

	private static VisumPtTimeProfile timeProfile() {
		return new VisumPtTimeProfile("name", null, profileElements());
	}

	private static Map<Integer, VisumPtTimeProfileElement> profileElements() {
		int arrival = (int) oneMinute.seconds();
		HashMap<Integer, VisumPtTimeProfileElement> elements = new HashMap<>();
		elements.put(1, profileElement("1", 1, 0, 0, stopPoint(1, "1")));
		elements.put(2, profileElement("2", 2, arrival, arrival, stopPoint(2, "2")));
		return elements;
	}

	private static VisumPtTimeProfileElement profileElement(
			String name, int index, int arrival, int departure, VisumPtStopPoint stopPoint) {
		return new VisumPtTimeProfileElement(null, name, index, index,
				lineRouteElement(index, stopPoint), false, false, arrival, departure);
	}

	private static VisumPtLineRouteElement lineRouteElement(int index, VisumPtStopPoint stopPoint) {
		return new VisumPtLineRouteElement(null, index, stopPoint != null, null, stopPoint, 0);
	}

	private static VisumPtStopPoint stopPoint(int index, String name) {
		return new DummyVisumStopPoint(index, name, name);
	}

	@Test
	public void creationFromStopPointsAndRoutePoints() throws Exception {
		resolve(someStop());
		resolve(otherStop());
		
		JourneyTemplate timeProfile = JourneyTemplate.from(timeProfileWithRoutePoints(), stopResolver);

		JourneyTemplate expectedProfile = new JourneyTemplate("name");
		expectedProfile.add(someStop(), 1, someRelativeTime, someRelativeTime);
		expectedProfile.add(otherStop(), 3, oneMinute, oneMinute);
		assertThat(timeProfile, is(equalTo(expectedProfile)));
	}

	private static VisumPtTimeProfile timeProfileWithRoutePoints() {
		return new VisumPtTimeProfile("name", null, profileElementsWithRoutePoint());
	}

	private static Map<Integer, VisumPtTimeProfileElement> profileElementsWithRoutePoint() {
		HashMap<Integer, VisumPtTimeProfileElement> elements = new HashMap<>();
		elements.put(1, profileElement("1", 1, 0, 0, stopPoint(1, "1")));
		elements.put(2, profileElement("2", 2, 0, 0, null));
		int arrival = (int) oneMinute.seconds();
		elements.put(3, profileElement("3", 3, arrival, arrival, stopPoint(3, "3")));
		return elements;
	}

	@Test
	public void departsFromGivenStopAtGivenTimeWithOneAvailableStop() throws Exception {
		JourneyTemplate profile = new JourneyTemplate("name");
		int stopIndex = 1;
		profile.add(someStop(), stopIndex, someRelativeTime, oneMinute);

		Time atTime = threeMinutesLater;
		Time departure = profile.departsFrom(stopIndex, atTime);

		assertThat(departure, is(equalTo(fourMinutesLater)));
	}

	@Test
	public void arrivesAtGivenStopAtGivenTimeWithOneAvailableStop() throws Exception {
		JourneyTemplate profile = new JourneyTemplate("name");
		int stopIndex = 1;
		profile.add(someStop(), stopIndex, someRelativeTime, oneMinute);

		Time atTime = threeMinutesLater;
		Time arrival = profile.arrivesAt(stopIndex, atTime);

		assertThat(arrival, is(equalTo(threeMinutesLater)));
	}

	@Test
	public void whenStopPointIsTwiceOnRoute() throws Exception {
		resolve(someStop());
		resolve(anotherStop());
		
		JourneyTemplate timeProfile = JourneyTemplate.from(timeProfileWithStopPointIsTwiceOnRoute(),
				stopResolver);

		JourneyTemplate expectedProfile = new JourneyTemplate("name");
		expectedProfile.add(someStop(), 1, someRelativeTime, someRelativeTime);
		expectedProfile.add(anotherStop(), 2, oneMinute, oneMinute);
		expectedProfile.add(someStop(), 3, twoMinutes, twoMinutes);
		assertThat(timeProfile, is(equalTo(expectedProfile)));
	}

	private void resolve(Stop stop) {
		when(stopResolver.get(stop.externalId())).thenReturn(stop);
	}

	private VisumPtTimeProfile timeProfileWithStopPointIsTwiceOnRoute() {
		int arrivalAtFirstStop = (int) oneMinute.seconds();
		int arrivalAtSecondStop = (int) twoMinutes.seconds();
		HashMap<Integer, VisumPtTimeProfileElement> elements = new HashMap<>();
		elements.put(1, profileElement("1", 1, 0, 0, stopPoint(1, "1")));
		elements.put(2,
				profileElement("2", 2, arrivalAtFirstStop, arrivalAtFirstStop, stopPoint(2, "2")));
		elements.put(3,
				profileElement("1", 3, arrivalAtSecondStop, arrivalAtSecondStop, stopPoint(1, "1")));
		return new VisumPtTimeProfile("name", null, elements);
	}

	@Test
	public void createConnectionsWhenStopPointIsTwiceOnRoute() throws Exception {
		int journeyId = 1;
		VisumPtVehicleJourney visumJourney = new VisumPtVehicleJourney(journeyId, null, 0, lineRoute(),
				timeProfileWithStopPointIsTwiceOnRoute(), 1, 3, Collections.emptyList());
		ModifiableJourney journey = mock(ModifiableJourney.class);
		when(factory.createJourney(eq(journeyId), eq(someTime), anyInt(), any())).thenReturn(journey);
		JourneyTemplate profile = new JourneyTemplate("name");
		Stop stop1 = stop().withId(0).withExternalId(1).withName("1").build();
		Stop stop2 = stop().withId(1).withExternalId(2).withName("2").build();
		profile.add(stop1, 1, someRelativeTime, someRelativeTime);
		profile.add(stop2, 2, oneMinute, oneMinute);
		profile.add(stop1, 3, twoMinutes, twoMinutes);
		Connection fromStop1ToStop2 = mock(Connection.class);
		Connection fromStop2ToStop1 = mock(Connection.class);
		when(factory.connectionFrom(eq(stop1), eq(stop2), eq(someTime), eq(anotherTime), eq(journey), any()))
				.thenReturn(fromStop1ToStop2);
		when(factory.connectionFrom(eq(stop2), eq(stop1), eq(anotherTime), eq(otherTime), eq(journey), any()))
				.thenReturn(fromStop2ToStop1);

		Journey createdJourney = profile.createJourney(visumJourney, factory, someDate);

		assertSame(journey, createdJourney);
		verify(factory).createJourney(eq(journeyId), eq(someTime), anyInt(), any());
		verify(journey).add(fromStop1ToStop2);
		verify(journey).add(fromStop2ToStop1);
		verify(factory).connectionFrom(eq(stop1), eq(stop2), eq(someTime), eq(anotherTime),
				eq(createdJourney), any());
		verify(factory).connectionFrom(eq(stop2), eq(stop1), eq(anotherTime), eq(otherTime),
				eq(createdJourney), any());
	}

	private VisumPtLineRoute lineRoute() {
		VisumPtLineRoute lineRoute = visumLineRoute().build();
		SortedMap<Integer, VisumPtLineRouteElement> elements = new TreeMap<>();
		elements.put(1, lineRouteElement(1, stopPoint(1, "1")));
		elements.put(2, lineRouteElement(2, stopPoint(2, "2")));
		elements.put(3, lineRouteElement(3, stopPoint(1, "1")));
		lineRoute.setElements(elements);
		return lineRoute;
	}

	@Test
	public void removesTimeFromStartToFirstStopWhenJourneyDoesNotStartAtFirstStop() throws Exception {
		int journeyId = 1;
		int journeyDepartureMinutes = 0;
		Time journeyDepartureTime = someDate.plusMinutes(journeyDepartureMinutes);
		Time secondStop = journeyDepartureTime.plus(oneMinute);
		int fromProfileIndex = 2;
		int toProfileIndex = 3;
		int journeyDeparture = (int) Duration.ofMinutes(journeyDepartureMinutes).getSeconds();
		VisumPtVehicleJourney visumJourney = visumJourney()
				.withId(journeyId)
				.departsAt(journeyDeparture)
				.takes(lineRoute())
				.takes(timeProfileWithThreeStops())
				.startsAt(fromProfileIndex)
				.endsAt(toProfileIndex)
				.build();
		ModifiableJourney journey = mock(ModifiableJourney.class);
		Connection connection = mock(Connection.class);
		when(factory.connectionFrom(eq(anotherStop()), eq(otherStop()), eq(journeyDepartureTime),
				eq(secondStop), eq(journey), any())).thenReturn(connection);
		when(factory.createJourney(eq(journeyId), eq(someTime), anyInt(), any())).thenReturn(journey);

		JourneyTemplate profile = new JourneyTemplate("name");
		profile.add(someStop(), 1, someRelativeTime, someRelativeTime);
		profile.add(anotherStop(), fromProfileIndex, oneMinute, oneMinute);
		profile.add(otherStop(), toProfileIndex, twoMinutes, twoMinutes);

		profile.createJourney(visumJourney, factory, someDate);

		verify(journey).add(connection);
		verify(factory).connectionFrom(eq(anotherStop()), eq(otherStop()), eq(journeyDepartureTime),
				eq(secondStop), eq(journey), any());
	}

	private VisumPtTimeProfile timeProfileWithThreeStops() {
		int arrivalAtFirstStop = (int) oneMinute.seconds();
		int arrivalAtSecondStop = (int) twoMinutes.seconds();
		HashMap<Integer, VisumPtTimeProfileElement> elements = new HashMap<>();
		elements.put(1, profileElement("1", 1, 0, 0, stopPoint(1, "1")));
		elements.put(2,
				profileElement("2", 2, arrivalAtFirstStop, arrivalAtFirstStop, stopPoint(2, "2")));
		elements.put(3,
				profileElement("3", 3, arrivalAtSecondStop, arrivalAtSecondStop, stopPoint(3, "3")));
		return new VisumPtTimeProfile("name", null, elements);
	}

	@Test
	public void equalsAndHashCode() throws Exception {
		EqualsVerifier
				.forClass(JourneyTemplate.class)
				.withPrefabValues(Point2D.class, coordinate(0, 0), coordinate(1, 1))
				.withPrefabValues(Stop.class, someStop(), anotherStop())
				.usingGetClass()
				.verify();
	}

}
