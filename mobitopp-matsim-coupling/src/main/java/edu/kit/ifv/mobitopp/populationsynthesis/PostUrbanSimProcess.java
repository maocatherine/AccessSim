package edu.kit.ifv.mobitopp.populationsynthesis;

import edu.kit.ifv.mobitopp.actitopp.*;
import edu.kit.ifv.mobitopp.actitopp.demo.CSVPersonInputReader;
import edu.kit.ifv.mobitopp.data.*;
import edu.kit.ifv.mobitopp.data.local.Convert;
import edu.kit.ifv.mobitopp.data.local.configuration.ParserBuilder;
import edu.kit.ifv.mobitopp.data.local.configuration.SimulationParser;
import edu.kit.ifv.mobitopp.data.person.HouseholdId;
import edu.kit.ifv.mobitopp.data.tourbasedactivitypattern.ExtendedPatternActivity;
import edu.kit.ifv.mobitopp.data.tourbasedactivitypattern.TourBasedActivityPattern;
import edu.kit.ifv.mobitopp.data.tourbasedactivitypattern.TourBasedActivityPatternCreator;
import edu.kit.ifv.mobitopp.data.tourbasedactivitypattern.TourPattern;
import edu.kit.ifv.mobitopp.matsim.MatsimContext;
import edu.kit.ifv.mobitopp.matsim.MatsimContextBuilder;
import edu.kit.ifv.mobitopp.network.SimpleEdge;
import edu.kit.ifv.mobitopp.network.SimpleRoadNetwork;
import edu.kit.ifv.mobitopp.network.ZoneArea;
import edu.kit.ifv.mobitopp.populationsynthesis.carownership.CarSegmentModel;
import edu.kit.ifv.mobitopp.populationsynthesis.carownership.DefaultCarOwnershipModel;
import edu.kit.ifv.mobitopp.populationsynthesis.carownership.LogitBasedCarSegmentModel;
import edu.kit.ifv.mobitopp.populationsynthesis.serialiser.*;
import edu.kit.ifv.mobitopp.simulation.*;
import edu.kit.ifv.mobitopp.simulation.ActivityType;
import edu.kit.ifv.mobitopp.simulation.ContextBuilder;
import edu.kit.ifv.mobitopp.simulation.WrittenConfiguration;
import edu.kit.ifv.mobitopp.simulation.car.PrivateCar;
import edu.kit.ifv.mobitopp.simulation.person.PersonForDemand;
import edu.kit.ifv.mobitopp.time.DayOfWeek;
import edu.kit.ifv.mobitopp.util.dataimport.CsvFile;
import edu.kit.ifv.mobitopp.visum.VisumNetwork;
import org.javatuples.Pair;

import au.com.bytecode.opencsv.CSVWriter;

import java.awt.geom.Point2D;
import java.io.*;
import java.net.URL;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Stream;

import static edu.kit.ifv.mobitopp.populationsynthesis.serialiser.DemandDataInput.household;
import static java.util.stream.Collectors.toList;

public class PostUrbanSimProcess {

    private static MatsimContext context;

    public PostUrbanSimProcess(MatsimContext context) {
        super();
        this.context = context;
    }

    public static void main(String... args) throws IOException {
        if (1 > args.length) {
            System.out.println("Usage: ... <configuration file>");
            System.exit(-1);
        }

        File ConfigurationFile = new File(args[0]);
        new PostUrbanSimProcess(new MatsimContextBuilder().buildFromShort(ConfigurationFile));
//        updateHouseholds();
        updatePersonAndActivity();
    }

    private static void updateHouseholds() throws IOException {
        List<Pair<Integer, Integer>> householdLCM = new ArrayList<>();
        PersonLoader loader = context.personLoader();
        ZoneRepository zoneRepository = context.dataRepository().zoneRepository();
        File newHouseholds = context.experimentalParameters().valueAsFile("householdAfterLocationChoice");
        VisumNetwork network = context.network();
        Long seed = context.seed();
        try (Scanner scanner = new Scanner(newHouseholds)) {
            while (scanner.hasNextLine()) {
                householdLCM.add(getRecordFromLine(scanner.nextLine()));
            }
        }
        for (Pair<Integer, Integer> element : householdLCM) {
            Household household = loader.getHouseholdByOid(element.getValue0());
            System.out.println("Updating household id: " + household.getOid());
            Zone newHomeZone = zoneRepository.getZoneByOid(element.getValue1());
            household.setHomeZone(newHomeZone);
            household.setHomeLocation(selectLocation(newHomeZone, network, seed));
        }
        writeHouseholdsAndCars();
    }

    private static Pair<Integer, Integer> getRecordFromLine(String line) {
        List<String> values = new ArrayList<String>();
        try (Scanner rowScanner = new Scanner(line)) {
            while (rowScanner.hasNext()) {
                values.add(rowScanner.next());
            }
        }
        String[] temp = values.get(0).split(",");
        Pair<Integer, Integer> pair = Pair.with(Integer.parseInt(temp[0]), Integer.parseInt(temp[1]));
        return pair;
    }

    private static Location selectLocation(Zone zone, VisumNetwork visumNetwork, Long seed) {
        Random random = new Random(seed);
        SimpleRoadNetwork network = new SimpleRoadNetwork(visumNetwork);
        ZoneArea area = network.zone(zone.getId()).totalArea();
        Point2D point = area.randomPoint(random.nextInt());
        SimpleEdge road = network.zone(zone.getId()).nearestEdge(point);
        double pos = road.nearestPositionOnEdge(point);
        return new Location(point, road.id(), pos);
    }

    private static void writeHouseholdsAndCars() throws IOException {
        List<Household> households = (List<Household>) context.dataRepository().personLoader().households().collect(toList());
        String[] header = {"householdId", "year", "householdNumber", "nominalSize", "domCode", "type", "homeZone",
                "homeLocation", "homeX", "homeY", "numberOfMinors", "numberOfNotSimulatedChildren",
                "totalNumberOfCars", "income", "incomeClass", "economicalStatus", "canChargePrivately"};
        List<String[]> list = new ArrayList<>();
        list.add(header);
        for (Household h : households) {
            HouseholdAttributes attributes = h.attributes();
            String[] record = {String.valueOf(attributes.id.getOid()), String.valueOf(attributes.id.getYear()), String.valueOf(attributes.id.getHouseholdNumber()),
                    String.valueOf(attributes.nominalSize), String.valueOf(attributes.domCode), String.valueOf(attributes.type),
                    String.valueOf(attributes.homeZone.getId().getMatrixColumn()), attributes.homeLocation.toString(), String.valueOf(attributes.homeLocation.coordinatesP().getX()),
                    String.valueOf(attributes.homeLocation.coordinatesP().getY()), String.valueOf(attributes.numberOfMinors),
                    String.valueOf(attributes.numberOfNotSimulatedChildren), String.valueOf(attributes.totalNumberOfCars),
                    String.valueOf(attributes.monthlyIncomeEur), String.valueOf(attributes.incomeClass), String.valueOf(attributes.economicalStatus.getCode()),
                    String.valueOf(attributes.canChargePrivately)};
            list.add(record);
        }
        try (CSVWriter writer = new CSVWriter(new FileWriter("output/long_term/demand-data/household.csv"), ';')) {
            writer.writeAll(list);
        }
        writeCars(households);
    }

    private static void writeCars(List<Household> households) throws IOException {
        String[] header = {"ownerId", "mainUserId", "personalUserId", "carType", "car attributes", "carId",
                "position", "segment", "capacity", "currentMileage", "currentFuelLevel", "maxRange"};
        List<String[]> list = new ArrayList<>();
        list.add(header);
        for (Household h : households) {
            Collection<PrivateCar> cars = h.whichCars();
            if (!cars.isEmpty()) {
                for (PrivateCar car : cars) {
                    String personalUserId = "-1";
                    if (car.personalUser() != null){
                        personalUserId = String.valueOf(car.personalUser().getOid());
                    }
                    String[] record = {String.valueOf(car.owner().getOid()), String.valueOf(car.mainUser().getOid()), personalUserId,
                            car.getType(), String.valueOf(car.id()), String.valueOf(car.position().zone().getId().getMatrixColumn()),
                            car.position().location().toString(), car.carSegment().name(), String.valueOf(car.capacity()), String.valueOf(car.currentMileage()),
                            String.valueOf(car.currentFuelLevel()), String.valueOf(car.maxRange())};
                    list.add(record);
                }
                try (CSVWriter writer = new CSVWriter(new FileWriter("output/long_term/demand-data/car.csv"), ';')) {
                    writer.writeAll(list);
                }
            }
        }
    }

    private static void updatePersonAndActivity() throws IOException {
        List<Household> households = (List<Household>) context.dataRepository().personLoader().households().collect(toList());
        TourPatternFixer fixer = new TourPatternFixer();
        String[] header = {"personId", "activityType", "observedTripDuration", "startTime", "duration", "tournr", "isMainActivity", "isSupertour"};
        List<String[]> list = new ArrayList<>();
        list.add(header);
        for (Household toHousehold : households) {
            for (Person personInHH : toHousehold.getPersons()) {
                PatternActivityWeek patternActivityWeek = new PatternActivityWeek();
                ActitoppPerson personOfActiTopp = new ActitoppPerson(
                        personInHH.getOid(),
                        toHousehold.attributes().numberOfNotSimulatedChildren,
                        toHousehold.attributes().numberOfMinors,
                        personInHH.age(),
                        personInHH.attributes().employment.getTypeAsInt(),
                        personInHH.attributes().gender.getTypeAsInt(),
                        toHousehold.homeZone().getAreaType().getTypeAsInt(),
                        toHousehold.getTotalNumberOfCars()
                );
                boolean scheduleOK = false;
                while (!scheduleOK) {
                    try {
                        // create week activity schedule
                        personOfActiTopp.generateSchedule(new ModelFileBase(), new RNGHelper(context.seed()));
                        for (HActivity act : personOfActiTopp.getWeekPattern().getAllActivities()) {
                            patternActivityWeek.addPatternActivity(convertActiToppTomobiToppActivityType(act));
                        }
                        scheduleOK = true;
                    } catch (InvalidPatternException e) {
                        System.err.println(e.getReason());
                        System.err.println("person involved: " + personOfActiTopp.getPersIndex());
                    }
                }
                patternActivityWeek = fixer.ensureIsTour(patternActivityWeek);
                TourBasedActivityPattern activitySchedule = TourBasedActivityPatternCreator.fromPatternActivityWeek(patternActivityWeek);
                List<ExtendedPatternActivity> patterns = activitySchedule.asPatternActivities();
                for (ExtendedPatternActivity activity: patterns) {
                    PersonPatternActivity personActivity = new PersonPatternActivity(personInHH.getOid(),activity);
                    String[] record = {String.valueOf(personInHH.getOid()), String.valueOf(personActivity.activityTypeAsInt()),
                            String.valueOf(personActivity.observedTripDuration()), String.valueOf(personActivity.startTime().toMinutes()), String.valueOf(personActivity.duration()),
                            String.valueOf(personActivity.tourNumber()), String.valueOf(personActivity.isMainActivity()), String.valueOf(personActivity.isInSupertour())};
                    list.add(record);
                }
                try (CSVWriter writer = new CSVWriter(new FileWriter("output/long_term/demand-data/activity.csv"), ';')) {
                    writer.writeAll(list);
                }
            }
        }
    }

    public static PatternActivity convertActiToppTomobiToppActivityType(HActivity act) {
        DayOfWeek wd = DayOfWeek.fromDay(act.getDayIndex());
        int estimatedTripTimeBeforeActivity = -1;
        if (act.tripBeforeActivityisScheduled()) {
            estimatedTripTimeBeforeActivity = act.getEstimatedTripTimeBeforeActivity();
        }
        PatternActivity activity = new PatternActivity(ActivityType.getTypeFromInt(act.getActivityType().getCode()), wd, estimatedTripTimeBeforeActivity, act.getStartTime(), act.getDuration());
        return activity;
    }

}
