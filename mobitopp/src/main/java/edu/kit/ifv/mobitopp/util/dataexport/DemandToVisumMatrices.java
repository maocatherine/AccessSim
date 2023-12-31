package edu.kit.ifv.mobitopp.util.dataexport;

import static edu.kit.ifv.mobitopp.util.collections.StreamUtils.warn;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.kit.ifv.mobitopp.data.IntegerMatrix;
import edu.kit.ifv.mobitopp.data.ZoneId;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DemandToVisumMatrices {

	private static final Pattern pattern = Pattern
			.compile("^(?<personid>.*),(?<source>.*),(?<target>.*),(?<departure>\\d\\d).*");

	private final File logFile;

	public DemandToVisumMatrices(File logFile) {
		super();
		this.logFile = logFile;
	}

	public static void main(String[] args) throws IOException {
		if (2 > args.length) {
			log.error("Usage: java DemandToVisumMatrices <simulation result log file> <output folder>");
			return;
		}

		File logFile = new File(args[0]);
		File outputFolder = new File(args[1]);

		DemandToVisumMatrices matrixToVisum = new DemandToVisumMatrices(logFile);
		matrixToVisum.convertTo(outputFolder);
	}

	private void convertTo(File outputFolder) throws IOException {
		List<String> zoneIds = zones();
		HashMap<String, Integer> zoneToOid = new HashMap<>();
		List<ZoneId> ids = new ArrayList<>();
		for (int index = 0; index < zoneIds.size(); index++) {
			int oid = index + 1;
			String zone = zoneIds.get(index);
			zoneToOid.put(zone, oid);
			ids.add(new ZoneId(zone, oid));
		}
		MatrixPrinter printer = new MatrixPrinter();

		HashMap<Integer, IntegerMatrix> matrices = new HashMap<>();
		for (int hour = 0; hour < 24; hour++) {
			matrices.put(hour, new IntegerMatrix(ids));
		}
		outputFolder.mkdirs();

		Map<OdRelation, Long> load = Files
				.lines(logFile.toPath())
				.filter(this::isRelation)
				.map(this::parse)
				.collect(groupingBy(Function.identity(), counting()));

		for (Entry<OdRelation, Long> entry : load.entrySet()) {
			OdRelation odRelation = entry.getKey();
			int from = zoneToOid.get(odRelation.origin());
			int to = zoneToOid.get(entry.getKey().destination());
			int hour = odRelation.departure();
			matrices.get(hour).set(from, to, entry.getValue().intValue());
		}

		for (Entry<Integer, IntegerMatrix> entry : matrices.entrySet()) {
			Integer hour = entry.getKey();
			String start = format(hour);
			String end = format(hour + 1);
			String filePath = new File(outputFolder, start + "_" + end + ".mtx").getAbsolutePath();
			printer.writeMatrixToFile(entry.getValue(), start, end, filePath);
		}
	}

	private String format(int hour) {
		return String.format("%1$02d", hour);
	}

	private boolean isRelation(String line) {
		return !line.startsWith("p");
	}

	private OdRelation parse(String line) {
		Matcher matcher = pattern.matcher(line);
		if (matcher.matches()) {
			String source = matcher.group("source");
			String target = matcher.group("target");
			int departure = Integer.parseInt(matcher.group("departure"));
			OdRelation odRelation = new OdRelation(source, target, departure);
			return odRelation;
		}
		throw warn(new RuntimeException("Can not parse input: " + line), log);
	}

	private static List<String> zones() {
		return asList("10101", "10301", "10302", "10601", "10701", "10702", "11001", "11002", "11003",
				"11101", "11102", "11103", "11104", "11201", "11202", "11203", "11401", "11501", "11601",
				"11801", "11901", "11902", "11903", "12101", "12102", "12103", "12701", "12801", "14001",
				"14601", "14602", "14603", "14604", "14605", "14606", "14607", "14701", "14801", "14802",
				"14803", "14804", "14805", "14806", "14807", "14808", "14809", "14810", "14811", "14812",
				"14901", "14902", "15001", "15002", "15101", "15301", "15401", "15901", "16001", "16301",
				"16701", "16801", "17001", "17002", "17003", "17101", "17301", "17302", "17303", "17304",
				"17305", "17306", "17307", "17308", "17309", "17401", "17601", "17602", "17603", "17604",
				"17605", "17606", "17701", "17702", "17801", "17802", "17803", "17901", "17902", "17903",
				"17904", "17905", "17906", "17907", "17908", "17909", "18001", "18002", "18101", "18102",
				"18103", "18104", "18105", "21111", "21112", "21113", "21114", "21115", "21116", "21117",
				"21118", "21121", "21122", "21123", "21124", "21125", "21126", "21127", "21128", "21131",
				"21132", "21133", "21134", "21141", "21142", "21143", "21144", "21145", "21151", "21152",
				"21153", "21154", "21161", "21162", "21163", "21164", "21165", "21166", "21171", "21172",
				"21173", "21181", "21182", "21183", "21211", "21212", "21213", "21214", "21215", "21216",
				"21221", "21222", "21231", "21232", "21233", "21234", "21241", "21242", "21243", "21251",
				"21252", "21261", "21262", "21263", "21271", "21272", "21273", "21311", "21312", "21313",
				"21314", "21321", "21322", "21323", "21324", "21325", "21326", "21331", "21332", "21333",
				"21334", "21341", "21342", "21343", "21344", "21345", "21346", "21347", "21351", "21352",
				"21353", "21354", "22111", "22112", "22113", "22121", "22122", "22123", "22131", "22132",
				"22133", "22134", "22141", "22142", "22143", "22151", "22152", "22153", "22161", "22162",
				"22163", "22211", "22212", "22213", "22221", "22222", "22223", "22224", "22225", "22231",
				"22232", "22311", "22312", "22313", "22314", "22315", "22316", "22321", "22322", "22323",
				"22331", "22332", "22333", "22334", "22335", "22341", "22342", "22343", "22344", "22345",
				"22346", "22351", "22352", "22353", "22354", "22361", "22362", "22363", "22364", "22365",
				"22366", "22367", "22368", "22369", "22371", "22372", "22373", "22374", "22375", "22381",
				"22382", "22383", "22384", "22385", "22386", "22411", "22412", "22413", "22421", "22422",
				"22423", "22424", "22425", "22426", "22431", "22432", "22433", "22441", "22442", "22451",
				"22452", "22461", "22462", "22463", "22471", "22472", "22473", "22474", "22511", "22512",
				"22513", "22514", "22521", "22522", "22523", "22524", "22525", "22526", "22531", "22532",
				"22533", "22541", "22542", "22543", "22544", "22545", "22546", "22547", "22551", "22552",
				"22553", "22554", "22555", "23111", "23112", "23113", "23121", "23122", "23123", "23124",
				"23125", "23131", "23132", "23133", "23134", "23135", "23141", "23142", "23143", "23144",
				"23145", "23151", "23152", "23153", "23154", "23155", "23156", "23157", "23161", "23162",
				"23163", "23164", "23165", "23166", "23167", "23171", "23172", "23173", "23181", "23182",
				"23191", "23192", "23193", "23211", "23212", "23213", "23214", "23215", "23221", "23222",
				"23223", "23224", "23225", "23231", "23232", "23233", "23234", "23235", "23236", "23237",
				"23238", "23241", "23242", "23243", "23244", "23245", "23246", "23251", "23252", "23253",
				"23261", "23262", "23263", "23264", "23271", "23272", "23273", "23281", "23282", "23283",
				"23284", "23285", "23311", "23312", "23313", "23321", "23322", "23323", "23324", "23331",
				"23332", "23333", "23334", "23335", "23336", "23341", "23342", "23343", "23344", "23345",
				"23351", "23352", "23353", "23354", "23361", "23362", "23363", "23364", "23365", "23371",
				"23372", "23373", "23374", "23375", "23376", "23411", "23412", "23413", "23414", "23421",
				"23422", "23423", "23424", "23425", "23426", "23427", "23428", "23429", "23431", "23432",
				"23433", "23441", "23442", "23443", "23444", "23451", "23452", "23453", "23454", "23461",
				"23511", "23512", "23513", "23514", "23521", "23522", "23523", "23524", "23525", "23526",
				"23527", "23531", "23532", "23533", "23541", "23542", "23543", "23551", "23552", "23553",
				"23554", "23561", "23562", "23563", "23564", "23565", "23566", "23567", "23568", "23571",
				"23572", "23573", "23574", "23575", "23576", "23577", "24111", "24121", "24131", "24132",
				"24133", "24134", "24135", "24136", "24211", "24212", "24213", "24214", "24215", "24221",
				"24222", "24223", "24224", "24225", "24231", "24232", "24241", "24242", "24243", "24244",
				"24251", "24252", "24253", "24261", "24262", "24263", "24264", "24265", "24271", "24272",
				"24273", "24274", "24281", "24282", "24311", "24312", "24321", "24322", "24411", "24412",
				"24413", "24414", "24415", "24421", "24422", "24501", "24502", "24503", "25111", "25112",
				"25113", "25114", "25115", "25121", "25122", "25123", "25124", "25125", "25126", "25131",
				"25132", "25133", "25134", "25141", "25142", "25151", "25152", "25161", "25162", "25211",
				"25212", "25213", "25214", "25215", "25231", "25232", "25233", "25234", "25235", "25241",
				"25242", "25251", "25252", "25253", "25261", "25262", "25271", "25272", "25273", "25281",
				"25282", "25283", "25311", "25312", "25321", "25322", "25323", "25324", "25331", "25332",
				"25333", "25334", "25341", "25351", "25352", "25361", "25362", "25363", "25364", "25365",
				"25411", "25412", "25421", "25422", "25423", "25424", "25425", "25431", "25432", "25433",
				"25434", "25435", "25441", "25442", "25451", "25452", "30101", "30102", "30103", "30301",
				"30302", "30401", "30402", "30601", "30602", "30603", "30801", "30802", "30803", "30804",
				"30805", "30806", "30807", "30808", "30809", "30810", "30811", "30812", "30813", "31801",
				"31802", "32001", "32002", "32003", "32004", "32005", "32006", "32401", "32402", "33701",
				"33801", "34101", "34102", "34201", "34202", "34203", "34401", "34402", "34403", "34404",
				"34405", "34406", "34407", "34408", "35301", "35501", "35502", "35503", "36101", "36102",
				"36103", "36104", "36701", "36702", "36703", "36704", "36705", "36706", "36707", "36708",
				"36709", "36710", "36711", "36712", "36801", "36901", "36902", "37501", "37502", "37601",
				"37602", "37901", "37902", "37903", "37904", "37905", "37906", "37907", "37908", "37909",
				"38301", "38302", "38303", "38401", "38402", "38403", "38404", "38405", "38501", "38502",
				"38503", "38504", "38505", "38506", "38507", "38508", "38601", "38602", "38701", "38702",
				"38703", "38704", "38901", "38902", "38903", "38904", "38905", "39001", "39002", "39003",
				"39004", "39101", "39102", "39103", "39104", "39105", "39301", "39302", "40101", "40102",
				"40201", "40301", "40302", "40303", "40304", "40305", "40306", "40307", "40308", "40401",
				"41001", "41301", "41501", "41502", "41601", "41602", "41603", "42101", "42102", "42103",
				"42104", "42105", "42106", "42107", "42108", "42201", "42401", "42801", "42802", "42803",
				"42804", "42805", "42806", "42807", "42808", "42809", "42810", "42811", "42901", "43401",
				"43701", "44101", "44102", "44201", "44202", "44401", "44501", "44502", "44503", "44504",
				"44505", "44506", "44507", "44508", "44509", "44510", "44601", "44801", "45001", "45002",
				"45003", "45004", "45005", "45101", "45102", "45103", "45201", "45202", "45301", "45302",
				"45401", "45402", "50401", "50501", "50601", "50701", "50702", "50801", "50802", "51101",
				"51201", "51202", "51401", "51501", "51601", "51801", "51901", "51902", "51903", "51904",
				"51905", "51906", "51907", "51908", "51909", "51910", "51911", "51912", "51913", "51914",
				"51915", "52001", "52002", "52003", "52201", "52701", "52901", "53301", "53302", "53303",
				"53304", "53305", "53306", "53307", "53308", "53501", "53601", "53701", "53702", "54101",
				"54201", "54301", "54601", "54602", "54701", "54801", "54901", "54902", "54903", "54904",
				"54905", "54906", "54907", "54908", "54909", "54910", "55001", "55301", "55401", "55601",
				"55602", "55801", "56301", "56801", "57001", "57002", "57101", "57102", "57201", "57202",
				"57301", "57601", "57602", "57603", "57701", "57702", "57703", "57704", "57705", "57706",
				"57801", "57802", "57803", "57804", "57805", "57901", "57902", "57903", "57904", "57905",
				"58001", "58002", "58003", "58004", "58005", "58101", "58102", "58103", "60101", "60201",
				"60301", "60601", "60602", "60603", "60701", "60702", "60703", "60901", "61001", "61002",
				"61003", "61004", "61101", "61201", "61401", "61402", "61501", "61502", "61503", "61504",
				"61505", "61506", "61601", "61701", "61801", "61802", "61803", "61804", "61805", "61806",
				"61807", "61901", "61902", "61903", "61904", "61905", "62001", "62301", "62401", "62402",
				"62403", "62404", "62405", "62406", "62407", "62408", "62409", "62410", "62411", "62501",
				"62601", "62602", "62603", "62604", "62605", "62606", "62607", "62608", "62609", "62610",
				"62611", "62612", "62613", "62801", "62901", "63001", "63101", "63301", "63501", "63701",
				"63801", "64201", "64202", "64203", "64204", "64301", "64401", "64901", "64902", "64903",
				"64904", "64905", "65101", "65102", "65103", "65104", "65105", "65301", "65501", "65801",
				"66001", "66101", "66102", "120071", "120091", "120171", "120591", "120641", "121071",
				"130001", "210051", "210061", "210101", "210131", "210171", "210211", "210261", "210341",
				"210381", "210461", "210471", "210561", "210581", "210591", "210691", "210801", "210981",
				"211021", "211071", "220001", "230111", "230461", "230471", "230661", "230941", "250121",
				"250141", "250231", "250251", "250321", "250431", "250521", "250561", "250591", "250761",
				"250791", "250991", "310151", "310161", "310191", "310251", "310321", "320021", "320071",
				"320191", "320241", "320271", "320381", "320421", "320431", "320441", "320451", "320651",
				"320881", "410021", "410051", "410081", "410201", "410311", "410331", "410391", "410711",
				"410721", "410751", "410791", "410841", "411341", "411381", "411391", "430001", "510141",
				"510271", "510501", "510531", "510591", "510601", "510611", "510731", "510781", "510801",
				"510851", "510881", "510911", "510921", "520091", "520111", "520151", "520181", "520221",
				"520231", "520251", "520361", "520411", "520481", "520491", "520501", "530021", "530131",
				"530221", "530251", "530311", "530791", "610061", "610071", "610081", "610201", "610331",
				"610461", "610501", "610791", "610801", "610851", "620251", "620281", "620331", "620381",
				"620391", "620401", "620441", "620461", "620611", "620721", "620761", "630191", "630241",
				"630271", "630281", "630401", "640001", "700001", "700002", "800001", "800002", "800003",
				"900001", "900002", "900003", "900004", "900005", "900006", "900007", "900008", "900009",
				"900010", "900011", "900012", "900013", "900014", "900015", "900016", "900017", "900018",
				"900019", "900020", "900021");
	}
}
