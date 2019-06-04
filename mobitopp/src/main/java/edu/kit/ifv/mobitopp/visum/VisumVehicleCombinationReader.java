package edu.kit.ifv.mobitopp.visum;

import static java.util.stream.Collectors.toMap;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

import edu.kit.ifv.mobitopp.visum.routes.Row;

public class VisumVehicleCombinationReader extends VisumBaseReader {

  private final Map<Integer, List<VisumVehicleCombinationUnit>> units2combinations;

  public VisumVehicleCombinationReader(
      NetfileLanguage language,
      Map<Integer, List<VisumVehicleCombinationUnit>> units2combinations) {
    super(language);
    this.units2combinations = units2combinations;
  }

  public Map<Integer, VisumVehicleCombination> readCombinations(Stream<Row> content) {
    return content.map(this::createCombination).collect(toMap(c -> c.id, Function.identity()));
  }

  VisumVehicleCombination createCombination(Row row) {
    int id = row.valueAsInteger(number());
    String code = row.get(code());
    String name = row.get(name());
    Map<VisumVehicleUnit, Integer> units = units(id);
    return new VisumVehicleCombination(id, code, name, units);
  }

  private Map<VisumVehicleUnit, Integer> units(Integer id) {
    return units2combinations.get(id).stream().collect(toMap(u -> u.unit, u -> u.quantity));
  }

}