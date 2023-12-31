package edu.kit.ifv.mobitopp.visum.reader;

import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.Stream;

import edu.kit.ifv.mobitopp.util.dataimport.Row;
import edu.kit.ifv.mobitopp.visum.NetfileLanguage;
import edu.kit.ifv.mobitopp.visum.VisumPoint;

public class VisumIntermediatePointReader extends VisumBaseReader {

  private final Map<Integer,SortedMap<Integer,VisumPoint>> result;

  public VisumIntermediatePointReader(NetfileLanguage language) {
    super(language);
    result = new HashMap<>();
  }

  public Map<Integer, SortedMap<Integer, VisumPoint>> readPoints(Stream<Row> content) {
    result.clear();
    content.forEach(this::read);
    return result;
  }

  private void read(Row row) {
    int edgeId = edgeIdOf(row);
    int index =  indexOf(row);
    VisumPoint point = createPoint(row);
    if (!result.containsKey(edgeId)) {
      result.put(edgeId, new TreeMap<>());
    }
    result.get(edgeId).put(index,point);
  }

  private VisumPoint createPoint(Row row) {
    float x = xCoordOf(row);
    float y = yCoordOf(row);
    return new VisumPoint(x,y);
  }

}
