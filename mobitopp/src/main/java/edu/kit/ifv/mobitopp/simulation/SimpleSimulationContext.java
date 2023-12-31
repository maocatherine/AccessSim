package edu.kit.ifv.mobitopp.simulation;

import java.io.PrintStream;

import edu.kit.ifv.mobitopp.communication.RestServerResourceRegistry;
import edu.kit.ifv.mobitopp.data.DataRepositoryForSimulation;
import edu.kit.ifv.mobitopp.data.PersonLoader;
import edu.kit.ifv.mobitopp.data.ZoneRepository;
import edu.kit.ifv.mobitopp.data.local.configuration.DynamicParameters;
import edu.kit.ifv.mobitopp.data.local.configuration.SimulationParser;
import edu.kit.ifv.mobitopp.result.ResultWriter;

public class SimpleSimulationContext implements SimulationContext {

	private final WrittenConfiguration configuration;
	private final DataRepositoryForSimulation dataRepository;
	private final SimulationParser format;
	private final ResultWriter resultWriter;
	private final SimulationDays simulationDays;
	private final ElectricChargingWriter electricChargingWriter;
	private final PersonResults personResults;
	private final DynamicParameters destinationChoiceParameters;
	private final DynamicParameters modeChoiceParameters;
	private final DynamicParameters experimentalParameters;

	public SimpleSimulationContext(WrittenConfiguration configuration,
		DynamicParameters experimentalParameters, DataRepositoryForSimulation dataRepository,
		SimulationDays simulationDays, SimulationParser format, ResultWriter resultWriter,
		ElectricChargingWriter electricChargingWriter, PersonResults personResults,
		DynamicParameters destinationChoiceParameters, DynamicParameters modeChoiceParameters) {
		this.configuration = configuration;
		this.experimentalParameters = experimentalParameters;
		this.dataRepository = dataRepository;
		this.simulationDays = simulationDays;
		this.format = format;
		this.resultWriter = resultWriter;
		this.electricChargingWriter = electricChargingWriter;
		this.personResults = personResults;
		this.destinationChoiceParameters = destinationChoiceParameters;
		this.modeChoiceParameters = modeChoiceParameters;
	}

	@Override
	public DataRepositoryForSimulation dataRepository() {
		return dataRepository;
	}

	@Override
	public PersonLoader personLoader() {
		return dataRepository.personLoader();
	}

	@Override
	public ZoneRepository zoneRepository() {
		return dataRepository.zoneRepository();
	}

	@Override
	public ImpedanceIfc impedance() {
		return dataRepository.impedance();
	}

	@Override
	public VehicleBehaviour vehicleBehaviour() {
		return dataRepository.vehicleBehaviour();
	}

	@Override
	public SimulationDays simulationDays() {
		return simulationDays;
	}

	@Override
	public ResultWriter results() {
		return resultWriter;
	}

	@Override
	public PersonResults personResults() {
		return personResults;
	}

	@Override
	public DynamicParameters destinationChoiceParameters() {
		return destinationChoiceParameters;
	}

	@Override
	public DynamicParameters modeChoiceParameters() {
		return modeChoiceParameters;
	}

	@Override
	public WrittenConfiguration configuration() {
		return configuration;
	}

	@Override
	public DynamicParameters experimentalParameters() {
		return experimentalParameters;
	}

	/**
	 * This method should be called before the simulation
	 * for set up:
	 * i.e. starting the {@link RestServerResourceRegistry rest server},
	 * and preparing {@link ElectricChargingWriter electric charging results}.
	 */
	@Override
	public void beforeSimulation() {
		printStartupInformationOn(System.out);
		electricChargingWriter.clear();
	}

	private void printStartupInformationOn(PrintStream out) {
		out.println("Configuration:");
		out.print(format.serialise(configuration));
	}

	/**
	 * This method should be called after the simulation
	 * for cleaning up:
	 * i.e. stopping the {@link RestServerResourceRegistry rest server},
	 * and finishing off the simulation results.
	 */
	@Override
	public void afterSimulation() {
		personResults.notifyFinishSimulation();
		electricChargingWriter.print();
		resultWriter.close();
	}

	@Override
	public long seed() {
		return configuration.getSeed();
	}

	@Override
	public float fractionOfPopulation() {
		return configuration.getFractionOfPopulation();
	}

}
