package edu.kit.ifv.mobitopp.publictransport.serializer;

import static edu.kit.ifv.mobitopp.util.collections.StreamUtils.warn;

import java.io.BufferedWriter;
import java.io.IOException;

import edu.kit.ifv.mobitopp.publictransport.model.Station;
import lombok.extern.slf4j.Slf4j;

@Slf4j
class DefaultStationSerializer extends BaseSerializer implements StationSerializer {

	private final BufferedWriter stationWriter;
	private final StationFormat stationFormat;

	DefaultStationSerializer(BufferedWriter stationWriter) {
		super();
		this.stationWriter = stationWriter;
		stationFormat = new CsvStationFormat();
	}

	public void serialize(Station station) {
		try {
			write(station);
		} catch (IOException e) {
			throw warn(new RuntimeException(e), log);
		}
	}

	private void write(Station station) throws IOException {
		String serialized = stationFormat().serialize(station);
		stationWriter.write(serialized);
		stationWriter.newLine();
	}

	StationFormat stationFormat() {
		return stationFormat;
	}

	static DefaultStationSerializer at(TimetableFiles timetableFiles) throws IOException {
		return new DefaultStationSerializer(timetableFiles.stationWriter());
	}

	@Override
	public void close() throws IOException {
		stationWriter.close();
	}

	@Override
	public void writeHeader() throws IOException {
		stationWriter.write("id;nodes");
		stationWriter.newLine();
	}

}
