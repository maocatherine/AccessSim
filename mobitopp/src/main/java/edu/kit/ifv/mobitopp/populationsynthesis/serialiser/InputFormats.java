package edu.kit.ifv.mobitopp.populationsynthesis.serialiser;

import edu.kit.ifv.mobitopp.data.ZoneRepository;
import edu.kit.ifv.mobitopp.populationsynthesis.DefaultFixedDestinationFormat;
import edu.kit.ifv.mobitopp.populationsynthesis.DefaultPrivateCarFormat;
import edu.kit.ifv.mobitopp.populationsynthesis.carownership.CarType;

class InputFormats {

	private final ZoneRepository zoneRepository;
	private final ZoneRepository zonesToSimulate;

	InputFormats(ZoneRepository zoneRepository, ZoneRepository zonesToSimulate) {
		super();
		this.zoneRepository = zoneRepository;
		this.zonesToSimulate = zonesToSimulate;
	}

	public DefaultHouseholdFormat householdFormat() {
		return new DefaultHouseholdFormat(zonesToSimulate);
	}

	public DefaultPersonFormat personFormat(PersonChanger personChanger) {
		return new DefaultPersonFormat(personChanger);
	}

	public DefaultActivityFormat activityFormat() {
		return new DefaultActivityFormat();
	}

	public DefaultPrivateCarFormat privateCarFormat() {
		DefaultPrivateCarFormat carFormat = new DefaultPrivateCarFormat();
		carFormat.register(CarType.conventional, conventionalCarFormat());
		carFormat.register(CarType.bev, bevCarFormat());
		carFormat.register(CarType.erev, erevCarFormat());
		return carFormat;
	}

	private ConventionalCarFormat conventionalCarFormat() {
		return new ConventionalCarFormat(zoneRepository);
	}

	private BevCarFormat bevCarFormat() {
		return new BevCarFormat(abstractElectricCarFormat());
	}

	private AbstractElectricCarFormat abstractElectricCarFormat() {
		return new AbstractElectricCarFormat(conventionalCarFormat());
	}

	private ErevCarFormat erevCarFormat() {
		return new ErevCarFormat(abstractElectricCarFormat());
	}
	
	public DefaultFixedDestinationFormat fixedDestinationFormat() {
		return new DefaultFixedDestinationFormat(zoneRepository);
	}

	public DefaultOpportunityFormat opportunityFormat() {
		return new DefaultOpportunityFormat(zoneRepository);
	}
}
