package edu.kit.ifv.mobitopp.data;

import java.io.IOException;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import edu.kit.ifv.mobitopp.data.areatype.AreaTypeRepository;
import edu.kit.ifv.mobitopp.data.local.TypeMapping;
import edu.kit.ifv.mobitopp.network.SimpleRoadNetwork;
import edu.kit.ifv.mobitopp.populationsynthesis.DemographyData;
import edu.kit.ifv.mobitopp.populationsynthesis.HouseholdForSetup;
import edu.kit.ifv.mobitopp.populationsynthesis.serialiser.PersonChanger;
import edu.kit.ifv.mobitopp.result.ResultWriter;
import edu.kit.ifv.mobitopp.simulation.ElectricChargingWriter;
import edu.kit.ifv.mobitopp.simulation.ImpedanceIfc;
import edu.kit.ifv.mobitopp.simulation.PublicTransportData;
import edu.kit.ifv.mobitopp.visum.VisumNetwork;

public interface DataSource {

	DataRepositoryForPopulationSynthesis forPopulationSynthesis(
			VisumNetwork visumNetwork, SimpleRoadNetwork roadNetwork, DemographyData demographyData,
			PanelDataRepository panelDataRepository, int numberOfZones, StartDateSpecification input,
			ResultWriter results, AreaTypeRepository areaTypeRepository, TypeMapping modeToType,
			PersonChanger personChanger, Function<ImpedanceIfc, ImpedanceIfc> wrapImpedance)
			throws IOException;

	DataRepositoryForSimulation forSimulation(
			Supplier<Network> network, int numberOfZones, InputSpecification input,
			PublicTransportData data, ResultWriter results, ElectricChargingWriter electricChargingWriter,
			AreaTypeRepository areaTypeRepository, TypeMapping modeToType,
			Predicate<HouseholdForSetup> householdFilter, PersonChanger personChanger,
			Function<ImpedanceIfc, ImpedanceIfc> wrapImpedance) throws IOException;

	void validate() throws IOException;

}
