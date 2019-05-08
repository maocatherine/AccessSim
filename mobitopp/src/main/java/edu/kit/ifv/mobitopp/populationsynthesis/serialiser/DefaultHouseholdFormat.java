package edu.kit.ifv.mobitopp.populationsynthesis.serialiser;

import static java.lang.String.valueOf;
import static java.util.Arrays.asList;

import java.util.List;
import java.util.Optional;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.data.ZoneRepository;
import edu.kit.ifv.mobitopp.simulation.Household;
import edu.kit.ifv.mobitopp.simulation.HouseholdAttributes;
import edu.kit.ifv.mobitopp.simulation.HouseholdForDemand;
import edu.kit.ifv.mobitopp.simulation.Location;
import edu.kit.ifv.mobitopp.simulation.LocationParser;
import edu.kit.ifv.mobitopp.data.person.HouseholdId;


public class DefaultHouseholdFormat implements SerialiserFormat<Household> {

	private static final int oidIndex = 0;
	private static final int yearIndex = 1;
	private static final int numberIndex = 2;
	private static final int nominalSizeIndex = 3;
	private static final int domCodeIndex = 4;
	private static final int zoneIndex = 5;
	private static final int locationIndex = 6;
	private static final int childrenIndex = 7;
	private static final int carsIndex = 8;
  private static final int incomeIndex = 9;
  private static final int incomeClassIndex = 10;
	private static final int chargePrivatelyIndex = 11;
	
	private final ZoneRepository zoneRepository;
	private final LocationParser locationParser;

	public DefaultHouseholdFormat(ZoneRepository zoneRepository) {
		super();
		this.zoneRepository = zoneRepository;
		locationParser = new LocationParser();
	}

  @Override
  public List<String> header() {
    return asList("householdId", "year", "householdNumber", "nominalSize", "domCode", "homeZone",
        "homeLocation", "numberOfNotSimulatedChildren", "totalNumberOfCars", "income",
        "incomeClass", "canChargePrivately");
	}

	@Override
	public List<String> prepare(Household household) {
		HouseholdAttributes attributes = household.attributes();
		int householdOid = attributes.oid;
		HouseholdId id = attributes.id;
		int nominalSize = attributes.nominalSize;
		int domCode = attributes.domCode;
		Zone homeZone = attributes.homeZone;
		String homeLocation = locationParser.serialise(attributes.homeLocation);
		int numberOfNotSimulatedChildren = attributes.numberOfNotSimulatedChildren;
		int totalNumberOfCars = attributes.totalNumberOfCars;
		int income = attributes.monthlyIncomeEur;
		int incomeClass = attributes.incomeClass;
		boolean canChargePrivately = attributes.canChargePrivately;
		return asList( 
				valueOf(householdOid), 
				valueOf(id.getYear()),
				valueOf(id.getHouseholdNumber()), 
				valueOf(nominalSize), 
				valueOf(domCode),
				valueOf(homeZone.getId().getMatrixColumn()), 
				homeLocation, 
				valueOf(numberOfNotSimulatedChildren),
				valueOf(totalNumberOfCars), 
        valueOf(income), 
        valueOf(incomeClass),
				valueOf(canChargePrivately)
			);
	}

	@Override
	public Optional<Household> parse(List<String> data) {
		return zoneOf(data).map(zone -> createHousehold(data, zone));
	}

	private HouseholdForDemand createHousehold(List<String> data, Zone zone) {
		HouseholdId id = idOf(data);
		int nominalSize = nominalSizeOf(data);
		int domCode = domCodeOf(data);
		Location location = locationOf(data);
		int numberOfNotSimulatedChildren = childrenOf(data);
		int totalNumberOfCars = carsOf(data);
		int income = incomeOf(data);
		int incomeClass = incomeClassOf(data);
		boolean canChargePrivately = chargePrivatelyOf(data);
    HouseholdForDemand household = new HouseholdForDemand(id, nominalSize, domCode, zone,
				location, numberOfNotSimulatedChildren, totalNumberOfCars, income, incomeClass, canChargePrivately);
		assign(household, zone);
		return household;
	}

	private static void assign(HouseholdForDemand household, Zone zone) {
		zone.getDemandData().getPopulationData().addHousehold(household);
	}

	private static int oidOf(List<String> data) {
		return Integer.parseInt(data.get(oidIndex));
	}

	private static HouseholdId idOf(List<String> data) {
	  int oid = oidOf(data);
		short year = Short.parseShort(data.get(yearIndex));
		int householdNumber = Integer.parseInt(data.get(numberIndex));
		return new HouseholdId(oid, year, householdNumber);
	}

	private static int nominalSizeOf(List<String> data) {
		return Integer.parseInt(data.get(nominalSizeIndex));
	}

	private static int domCodeOf(List<String> data) {
		return Integer.parseInt(data.get(domCodeIndex));
	}

	private Optional<Zone> zoneOf(List<String> data) {
		int oid = Integer.parseInt(data.get(zoneIndex));
		if (zoneRepository.hasZone(oid)) {
			return Optional.of(zoneRepository.getZoneByOid(oid));
		}
		return Optional.empty();
	}

	private Location locationOf(List<String> data) {
		return locationParser.parse(data.get(locationIndex));
	}

	private int childrenOf(List<String> data) {
		return Integer.parseInt(data.get(childrenIndex));
	}

	private int carsOf(List<String> data) {
		return Integer.parseInt(data.get(carsIndex));
	}

  private int incomeOf(List<String> data) {
    return Integer.parseInt(data.get(incomeIndex));
  }
  
  private int incomeClassOf(List<String> data) {
    return Integer.parseInt(data.get(incomeClassIndex));
  }

	private boolean chargePrivatelyOf(List<String> data) {
		return Boolean.parseBoolean(data.get(chargePrivatelyIndex));
	}

}
