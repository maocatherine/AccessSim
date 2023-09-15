package edu.kit.ifv.mobitopp.populationsynthesis.householdlocation;

import java.awt.geom.Point2D;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.URL;
import java.util.*;

import edu.kit.ifv.mobitopp.network.SimpleEdge;
import edu.kit.ifv.mobitopp.network.Zone;
import edu.kit.ifv.mobitopp.network.ZoneArea;
import edu.kit.ifv.mobitopp.populationsynthesis.EconomicalStatusCalculators;
import edu.kit.ifv.mobitopp.populationsynthesis.EconomicalStatusDistributionParser;
import edu.kit.ifv.mobitopp.populationsynthesis.SynthesisContext;
import edu.kit.ifv.mobitopp.populationsynthesis.opportunities.NetworkBasedRoadLocator;
import edu.kit.ifv.mobitopp.simulation.Location;
import edu.kit.ifv.mobitopp.util.dataimport.CsvFile;
import static edu.kit.ifv.mobitopp.util.collections.StreamUtils.warn;
import static java.util.stream.Collectors.toList;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UrbanSimHouseholdLocationSelector extends RandomHouseholdLocationSelector implements HouseholdLocationSelector{

    private static final String buildingFile = "residential-buildings-in-Clayton.csv";
    private static List<Building> buildings;
    private final Random random;

    public UrbanSimHouseholdLocationSelector(SynthesisContext context) {
        super(context);
        loadBuilding(context);
        this.random = new Random(context.seed());
    }

    private void loadBuilding(SynthesisContext context) {

        BuildingParser parser = new BuildingParser(context.zoneRepository().zoneRepository(), new NetworkBasedRoadLocator(context.roadNetwork()));

        try (InputStream input = this.getClass().getClassLoader().getResourceAsStream(buildingFile)) {
            buildings = CsvFile
                .createFrom(input)
                .stream()
                .map(parser::parse)
                .collect(toList());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Location getCoord(Zone zone){
        List<Building> buildingInZone = new ArrayList<>();
        for (Building building : buildings) {
            if (building.zoneId.getExternalId().equals((zone.id().toString()))){
                buildingInZone.add(building);
            }
        }
        int size = buildingInZone.size();
        Building selectedBuilding = buildingInZone.get(random.nextInt(size));
        buildings.remove(selectedBuilding);
        return selectedBuilding.getLocation();
    }

    @Override
    public Location selectLocation(Zone zone) {
        return getCoord(zone);
    }

}
