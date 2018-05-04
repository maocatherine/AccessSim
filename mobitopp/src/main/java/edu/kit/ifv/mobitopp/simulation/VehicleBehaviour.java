package edu.kit.ifv.mobitopp.simulation;

import edu.kit.ifv.mobitopp.publictransport.model.Stop;
import edu.kit.ifv.mobitopp.simulation.events.EventQueue;
import edu.kit.ifv.mobitopp.simulation.person.PublicTransportBehaviour;
import edu.kit.ifv.mobitopp.simulation.person.PublicTransportLeg;
import edu.kit.ifv.mobitopp.simulation.person.PublicTransportTrip;
import edu.kit.ifv.mobitopp.simulation.person.SimulationPerson;
import edu.kit.ifv.mobitopp.time.Time;

public interface VehicleBehaviour extends PublicTransportBehaviour {

	public static final VehicleBehaviour empty = new VehicleBehaviour() {

		@Override
		public void letVehiclesArriveAt(Time currentDate, EventQueue queue) {
		}

		@Override
		public void letVehiclesDepartAt(Time currentDate) {
		}

		@Override
		public boolean isVehicleAvailable(PublicTransportLeg leg) {
			return false;
		}
		
		@Override
		public boolean hasPlaceInVehicle(PublicTransportLeg leg) {
			return false;
		}

		@Override
		public void board(SimulationPerson person, Time time, PublicTransportLeg part) {
		}

		@Override
		public void getOff(SimulationPerson person, Time time, PublicTransportLeg part) {
		}

		@Override
		public TripIfc searchNewTrip(
				SimulationPerson person, Time time, PublicTransportTrip trip) {
			return trip;
		}

		@Override
		public void wait(SimulationPerson person, Time time, PublicTransportLeg part) {
		}

		@Override
		public void enterWaitingArea(SimulationPerson simulationPerson, Stop stop) {
		}

		@Override
		public void leaveWaitingArea(SimulationPerson person, Stop stop) {
		}

	};

	void letVehiclesArriveAt(Time currentDate, EventQueue queue);

	void letVehiclesDepartAt(Time currentDate);

}
