package edu.kit.ifv.mobitopp.util.dataexport;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import edu.kit.ifv.mobitopp.data.FloatMatrix;
import edu.kit.ifv.mobitopp.data.TravelTimeMatrix;
import edu.kit.ifv.mobitopp.data.ZoneId;

public class MatrixToCsvTest {

  private static final int someOid = 2;
  private static final int anotherOid = 5;
  private static final ZoneId someId = new ZoneId("2", someOid);
	private static final ZoneId anotherId = new ZoneId("5", anotherOid);
	private static final List<ZoneId> ids = asList(someId, anotherId);

	@Test
	public void toCsv() {
		FloatMatrix matrix = new TravelTimeMatrix(ids);
		matrix.set(someIndex(), someIndex(), 1.0f);
		matrix.set(someIndex(), anotherIndex(), 2.0f);
		matrix.set(anotherIndex(), someIndex(), 3.0f);
		matrix.set(anotherIndex(), anotherIndex(), 4.0f);
		String csv = new MatrixToCsv(matrix).toCsv();

		String newline = System.lineSeparator();
		String expected = "Von;Nach;Zeit" + newline + someOid + ";" + someOid + ";1.0" + newline
				+ someOid + ";" + anotherOid + ";2.0" + newline + anotherOid + ";" + someOid + ";3.0"
				+ newline + anotherOid + ";" + anotherOid + ";4.0" + newline;
		assertEquals(expected, csv);
	}

	private int someIndex() {
		return ids.indexOf(someId) + 1;
	}

	private int anotherIndex() {
		return ids.indexOf(anotherId) + 1;
	}
}
