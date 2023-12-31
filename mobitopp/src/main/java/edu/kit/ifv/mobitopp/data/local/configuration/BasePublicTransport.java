package edu.kit.ifv.mobitopp.data.local.configuration;

import static edu.kit.ifv.mobitopp.util.collections.StreamUtils.warn;

import java.io.IOException;
import java.io.UncheckedIOException;

import edu.kit.ifv.mobitopp.data.InputSpecification;
import edu.kit.ifv.mobitopp.publictransport.serializer.DefaultJourneyFactory;
import edu.kit.ifv.mobitopp.publictransport.serializer.Deserializer;
import edu.kit.ifv.mobitopp.publictransport.serializer.JourneyFactory;
import edu.kit.ifv.mobitopp.publictransport.serializer.Serializer;
import edu.kit.ifv.mobitopp.publictransport.serializer.TimetableDeserializer;
import edu.kit.ifv.mobitopp.publictransport.serializer.TimetableFiles;
import edu.kit.ifv.mobitopp.publictransport.serializer.TimetableSerializer;
import edu.kit.ifv.mobitopp.simulation.publictransport.PublicTransportFromMobitopp;
import edu.kit.ifv.mobitopp.simulation.publictransport.PublicTransportFromVisum;
import edu.kit.ifv.mobitopp.simulation.publictransport.PublicTransportTimetable;
import edu.kit.ifv.mobitopp.simulation.publictransport.SearchFootpath;
import edu.kit.ifv.mobitopp.simulation.publictransport.TimetableVerifier;
import edu.kit.ifv.mobitopp.time.Time;
import edu.kit.ifv.mobitopp.visum.VisumNetwork;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class BasePublicTransport {

	public BasePublicTransport() {
		super();
	}

	protected PublicTransportTimetable loadTimetable(VisumNetwork network,
			InputSpecification input, TimetableVerifier timetableVerifier) throws IOException {
		TimetableFiles timetableFiles = timetableFiles();
		PublicTransportFromVisum converter = converter(input, network, timetableVerifier);
		Time startTime = input.startDate();
		JourneyFactory factory = new DefaultJourneyFactory();
		if (useSerialized() && timetableFiles.exist()) {
			return loadSerialized(input, timetableFiles, converter, startTime, factory, timetableVerifier);
		}
		return loadVisum(timetableFiles, converter, startTime, factory);
	}

	protected boolean useSerialized() {
		return true;
	}

	private PublicTransportTimetable loadVisum(TimetableFiles timetableFiles,
			PublicTransportFromVisum converter, Time startTime, JourneyFactory factory) throws IOException {
		PublicTransportTimetable timetable = converter.convert();
		log.info("Start serializing mobiTopp timetable");
		serialize(timetableFiles, startTime, factory, timetable);
		log.info("Serializing mobiTopp timetable finished");
		return timetable;
	}

	private void serialize(TimetableFiles timetableFiles, Time startTime, JourneyFactory factory,
			PublicTransportTimetable timetable) {
		try (Serializer serializer = TimetableSerializer.at(timetableFiles, startTime, factory)) {
			serializer.writeHeaders();
			timetable.serializeTo(serializer);

		} catch (IOException cause) {
			throw warn(new UncheckedIOException(cause), log);
		}
	}

	private PublicTransportTimetable loadSerialized(InputSpecification input,
			TimetableFiles timetableFiles, PublicTransportFromVisum converter, Time startTime,
			JourneyFactory factory, TimetableVerifier timetableVerifier) {
		try {
			return convertDeserialized(input, timetableFiles, converter, startTime, factory, timetableVerifier);

		} catch (IOException cause) {
			throw warn(new UncheckedIOException(cause), log);
		}
	}

	protected abstract TimetableFiles timetableFiles();

	protected PublicTransportFromVisum converter(InputSpecification input, VisumNetwork network,
		TimetableVerifier timetableVerifier) {
		return new PublicTransportFromVisum(input.simulationDates(), timetableVerifier, network);
	}

	private PublicTransportTimetable convertDeserialized(InputSpecification input,
		TimetableFiles timetableFiles, SearchFootpath converter, Time startTime,
		JourneyFactory journeyFactory, TimetableVerifier timetableVerifier) throws IOException {
		Deserializer deserializer = TimetableDeserializer
			.defaultDeserializer(timetableFiles, startTime, journeyFactory);
		PublicTransportFromMobitopp fromMobiTopp = createMobitoppTimetableReader(input, converter,
			timetableVerifier, deserializer);
		return fromMobiTopp.convert();
	}

	protected PublicTransportFromMobitopp createMobitoppTimetableReader(InputSpecification input,
		SearchFootpath converter, TimetableVerifier timetableVerifier, Deserializer deserializer) {
		return new PublicTransportFromMobitopp(
			input.simulationDates(), timetableVerifier, deserializer, converter);
	}

}