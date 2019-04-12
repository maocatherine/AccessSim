package edu.kit.ifv.mobitopp.visum.routes;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.hasSize;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.kit.ifv.mobitopp.time.RelativeTime;

public class RouteReaderTest {

  private RouteReader reader;
  private TestRoutes routes;
  private Map<NodeNode, RelativeTime> connectors;

  @BeforeEach
  public void initialise() {
    connectors = new HashMap<>();
    routes = new TestRoutes();
    reader = new RouteReader(connectors);
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

  @Test
  void buildsRouteWithMissingIntermediate() throws Exception {
    routes.addSomeRoute();
    routes.addRouteWithoutIntermediate();
    routes.addOtherRoute();

    Map<OdPair, ZoneRoute> transformed = reader.transform(rows());

    assertHasSomeRoute(transformed);
    assertHasIntermediateRoute(transformed);
    assertHasOtherRoute(transformed);
  }

  @Test
  void usesConnectorTime() throws Exception {
    connectors.put(routes.connectorNodeToNode(), routes.connectorTime());
    routes.addSomeRoute();

    Map<OdPair, ZoneRoute> transformed = reader.transform(rows());

    assertThat(transformed, hasEntry(routes.someOdPair(), routes.someRouteWithConnector()));
  }

  private void assertHasIntermediateRoute(Map<OdPair, ZoneRoute> transformed) {
    assertThat(transformed,
        hasEntry(routes.missingIntermediateOdPair(), routes.missingIntermediateRoute()));
  }

  private Stream<Row> rows() {
    return routes.createRows();
  }

}
