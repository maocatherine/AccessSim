package edu.kit.ifv.mobitopp.visum.routes;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.hasSize;

import java.util.Map;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class RouteReaderTest {

  private RouteReader reader;
  private TestRoutes routes;

  @BeforeEach
  public void initialise() {
    routes = new TestRoutes();
    reader = new RouteReader();
  }

  @Test
  void buildSomeRoute() throws Exception {
    routes.addSomeRoute();

    Map<OdPair, ZoneRoute> transformed = reader.transform(rows());

    assertHasSomeRoute(transformed);
  }

  private void assertHasSomeRoute(Map<OdPair, ZoneRoute> transformed) {
    assertThat(transformed, hasEntry(routes.someOdPair(), routes.someRoute()));
  }

  @Test
  void buildSameOdPairOnlyOnce() throws Exception {
    routes.addSomeRoute("1", "Z3");
    routes.addSomeRoute("2", "Z4");

    Map<OdPair, ZoneRoute> transformed = reader.transform(rows());

    assertHasSomeRoute(transformed);
  }

  @Test
  void buildOtherRoute() throws Exception {
    routes.addOtherRoute();

    Map<OdPair, ZoneRoute> transformed = reader.transform(rows());

    assertHasOtherRoute(transformed);
  }

  private void assertHasOtherRoute(Map<OdPair, ZoneRoute> transformed) {
    assertThat(transformed, hasEntry(routes.otherOdPair(), routes.otherRoute()));
  }

  @Test
  void buildsSeveralRoutes() throws Exception {
    routes.addSomeRoute();
    routes.addOtherRoute();

    Map<OdPair, ZoneRoute> transformed = reader.transform(rows());

    assertThat(transformed.entrySet(), hasSize(2));
    assertHasSomeRoute(transformed);
    assertHasOtherRoute(transformed);
  }

  private Stream<Row> rows() {
    return routes.createRows();
  }

}
