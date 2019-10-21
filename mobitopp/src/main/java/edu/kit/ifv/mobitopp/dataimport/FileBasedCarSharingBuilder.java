package edu.kit.ifv.mobitopp.dataimport;

import static java.util.stream.Collectors.groupingBy;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.network.SimpleRoadNetwork;
import edu.kit.ifv.mobitopp.simulation.Car;
import edu.kit.ifv.mobitopp.simulation.IdSequence;
import edu.kit.ifv.mobitopp.simulation.Location;
import edu.kit.ifv.mobitopp.simulation.car.CarPosition;
import edu.kit.ifv.mobitopp.simulation.car.ConventionalCar;
import edu.kit.ifv.mobitopp.simulation.carsharing.CarSharingDataForZone;
import edu.kit.ifv.mobitopp.simulation.carsharing.CarSharingOrganization;
import edu.kit.ifv.mobitopp.simulation.carsharing.CarSharingStation;
import edu.kit.ifv.mobitopp.simulation.carsharing.FreeFloatingCarSharingOrganization;
import edu.kit.ifv.mobitopp.simulation.carsharing.StationBasedCarSharingCar;
import edu.kit.ifv.mobitopp.simulation.carsharing.StationBasedCarSharingOrganization;
import edu.kit.ifv.mobitopp.util.dataimport.CsvFile;
import edu.kit.ifv.mobitopp.visum.VisumPoint2;

public class FileBasedCarSharingBuilder extends BaseCarSharingBuilder {

	private final CsvFile carSharingData;

	public FileBasedCarSharingBuilder(SimpleRoadNetwork roadNetwork, IdSequence carSharingCarIds, CsvFile carSharingData) {
		super(roadNetwork, carSharingCarIds);
		this.carSharingData = carSharingData;
	}

	public CarSharingDataForZone carsharingIn(Zone zone) {
		List<CarSharingStation> all = carSharingStationsFor(zone);
		Map<String, List<CarSharingStation>> carSharingStations = all.stream()
				.collect(groupingBy(s -> s.carSharingCompany.name()));
		List<StationBasedCarSharingOrganization> stationBasedCarSharingCompanies = new ArrayList<>();
		for (Entry<String, List<CarSharingStation>> entry : carSharingStations.entrySet()) {
			if (!entry.getValue().isEmpty()) {
				stationBasedCarSharingCompanies.add(carSharingCompany(entry.getKey()));
			}
		}
		Map<String, Boolean> freeFloatingArea = new LinkedHashMap<>();
		Map<String, Integer> freeFloatingCars = new LinkedHashMap<>();
		List<FreeFloatingCarSharingOrganization> freeFloatingCarSharingCompanies =  new ArrayList<>(
				getFreeFloatingOrganizations().values());
		Map<String, Float> carsharingCarDensities = new LinkedHashMap<>();
		CarSharingDataForZone carSharingData = new CarSharingDataForZone(zone,
			stationBasedCarSharingCompanies, carSharingStations, freeFloatingCarSharingCompanies,
			freeFloatingArea, freeFloatingCars, carsharingCarDensities);
		createStationBasedCarSharingCarsFor(zone, carSharingData);	
		return carSharingData;
	}
	
	private List<CarSharingStation> carSharingStationsFor(Zone zone) {
		String zoneId = zone.getId().getExternalId();
		List<CarSharingStation> result = new ArrayList<>();
		
		for (int index = 0; index < carSharingData.getLength(); index++) {
			if (carSharingData.getValue(index, "zone_ID").equals(zoneId)) {
				Point2D coordinates = new VisumPoint2(
						carSharingData.getFloat(index, "x_coordinate"),
						carSharingData.getFloat(index, "y_coordinate")).asPoint2D();
				String company = carSharingData.getValue(index, "system");
				StationBasedCarSharingOrganization organisation = carSharingCompany(company);
				int id = carSharingData.getInteger(index, "ID");
				String name = carSharingData.getValue(index, "name");
				String parkingSpace = "";// dataFile.getValue(index, "parking_space");
				int numberOfCars = carSharingData.getInteger(index, "num_vehicles");
				Location location = makeLocation(Integer.valueOf(zoneId), coordinates);
				CarSharingStation realStation = new CarSharingStation(organisation, zone, id, name,
						parkingSpace, location, numberOfCars);
				result.add(realStation);
			}
		}
		return result;
	}
	
	private void createStationBasedCarSharingCarsFor(Zone zone, CarSharingDataForZone carSharingData) {
		
		carSharingData.clearCars();
		List<StationBasedCarSharingCar> stationBasedCars = new ArrayList<StationBasedCarSharingCar>();
		
		for (CarSharingOrganization company :	carSharingData.stationBasedCarSharingCompanies()) {
			for (CarSharingStation station : carSharingData.carSharingStations(company.name(), zone)) {
				assert station != null;
				assert station.numberOfCars != null;
				for (int i=0; i<station.numberOfCars; i++) {
					CarPosition position =	new CarPosition(zone, station.location);
					StationBasedCarSharingCar car = new StationBasedCarSharingCar(
																new ConventionalCar(getCarSharingCarIds(), position, Car.Segment.MIDSIZE),
																company,
																station
															);		
					stationBasedCars.add(car);
				}
			}
		}
		for (StationBasedCarSharingCar car : stationBasedCars) {
			StationBasedCarSharingOrganization company = car.station.carSharingCompany;
			company.ownCar(car, car.station.zone);
		}
		
	}
}
