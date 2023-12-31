package edu.kit.ifv.mobitopp.populationsynthesis;

import static edu.kit.ifv.mobitopp.util.collections.StreamUtils.warn;

import java.io.File;
import java.util.Collections;
import java.util.Map;
import java.util.SortedMap;
import java.util.stream.Stream;

import edu.kit.ifv.mobitopp.data.local.DemandRegionMapping;
import edu.kit.ifv.mobitopp.util.dataimport.CsvFile;
import edu.kit.ifv.mobitopp.util.dataimport.Row;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class StandardDemandRegionMapping implements DemandRegionMapping {

	private final SortedMap<RegionalLevel, RegionalLevel> levelMapping;
	private final Map<RegionalLevel, File> content;

	@Override
	public SortedMap<RegionalLevel, RegionalLevel> getLowToHigh() {
		return Collections.unmodifiableSortedMap(levelMapping);
	}

	@Override
	public Stream<Row> contentFor(RegionalLevel level) {
		if (content.containsKey(level)) {
			return CsvFile.createFrom(content.get(level)).stream();
		}
		throw warn(new IllegalArgumentException("Could not find a file for level: " + level), log);
	}

}
