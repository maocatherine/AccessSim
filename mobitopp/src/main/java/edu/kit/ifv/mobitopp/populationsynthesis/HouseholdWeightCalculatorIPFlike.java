package edu.kit.ifv.mobitopp.populationsynthesis;

import static edu.kit.ifv.mobitopp.util.collections.StreamUtils.warn;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.TreeSet;

import edu.kit.ifv.mobitopp.data.demand.EmploymentDistribution;
import edu.kit.ifv.mobitopp.data.demand.EmploymentDistributionItem;
import edu.kit.ifv.mobitopp.data.demand.RangeDistributionIfc;
import edu.kit.ifv.mobitopp.data.demand.RangeDistributionItem;
import edu.kit.ifv.mobitopp.simulation.Employment;
import edu.kit.ifv.mobitopp.simulation.Gender;
import edu.kit.ifv.mobitopp.util.panel.HouseholdOfPanelData;
import edu.kit.ifv.mobitopp.util.panel.HouseholdOfPanelDataId;
import edu.kit.ifv.mobitopp.util.panel.PersonOfPanelData;
import lombok.extern.slf4j.Slf4j;

@Slf4j
class HouseholdWeightCalculatorIPFlike 
	implements HouseholdWeightCalculatorIfc
{

	private Map<HouseholdOfPanelDataId,HouseholdOfPanelData> households;
	private Map<HouseholdOfPanelDataId,List<PersonOfPanelData>> persons;

	private Map<Integer,Set<HouseholdOfPanelDataId>> hh_male = new TreeMap<Integer,Set<HouseholdOfPanelDataId>>();
	private Map<Integer,Set<HouseholdOfPanelDataId>> hh_female = new TreeMap<Integer,Set<HouseholdOfPanelDataId>>();
	private Map<Integer,Set<HouseholdOfPanelDataId>> hh_emp = new TreeMap<Integer,Set<HouseholdOfPanelDataId>>();

	private boolean hh_lists_initialized = false;

	private Gender nextChildGender = Gender.MALE;



	public List<HouseholdOfPanelDataId> calculateWeights(
		RangeDistributionIfc hhDistribution,
		EmploymentDistribution empDistribution,
		RangeDistributionIfc maleAgeDistribution,
		RangeDistributionIfc femaleAgeDistribution,
		List<HouseholdOfPanelDataId> householdOfPanelDataIds,
		Map<HouseholdOfPanelDataId,HouseholdOfPanelData> households,
		Map<HouseholdOfPanelDataId,List<PersonOfPanelData>> persons
	) {

		this.households = households;
		this.persons = persons;

		SortedMap<Integer,Double> maleDist = normalizedDistribution(maleAgeDistribution);
		SortedMap<Integer,Double> femaleDist = normalizedDistribution(femaleAgeDistribution);
		SortedMap<Integer,Double> empDist = normalizedDistribution(empDistribution);
		SortedMap<Integer,Double> hhDist = normalizedDistribution(hhDistribution);


		if(!this.hh_lists_initialized) {
			initHHIdLists(maleDist, femaleDist, householdOfPanelDataIds);
			this.hh_lists_initialized = true;
		}

		for (HouseholdOfPanelDataId hhId : householdOfPanelDataIds) {
			hhId.set_weight(1.0);
		}

		for (int i=0; i <10; i++) {
			Map<String,SortedMap<Integer,Double>> quotients;

			quotients	= calculateWeightedDistributions(
						maleDist,
						femaleDist,
						empDist,
						hhDist,
						householdOfPanelDataIds,
						false
					);

			adjustHouseholdWeights(quotients.get("MALE"), this.hh_male);
			normalizeHHWeights(householdOfPanelDataIds);

			quotients	= calculateWeightedDistributions(
						maleDist,
						femaleDist,
						empDist,
						hhDist,
						householdOfPanelDataIds,
						false
					);

			adjustHouseholdWeights(quotients.get("FEMALE"), this.hh_female);
			normalizeHHWeights(householdOfPanelDataIds);

			quotients	= calculateWeightedDistributions(
						maleDist,
						femaleDist,
						empDist,
						hhDist,
						householdOfPanelDataIds,
						false
					);

			adjustHouseholdWeights(quotients.get("EMP"), this.hh_emp);
			normalizeHHWeights(householdOfPanelDataIds);

			quotients	= calculateWeightedDistributions(
						maleDist,
						femaleDist,
						empDist,
						hhDist,
						householdOfPanelDataIds,
						false
					);

			log.debug(String.valueOf(quotients.get("MALE").get(5)));
		}

		return new ArrayList<HouseholdOfPanelDataId>();
	}

	private void adjustHouseholdWeights(
		SortedMap<Integer,Double> pers_dist,
		Map<Integer,Set<HouseholdOfPanelDataId>> hh_ids
	) {

		for (Integer key : pers_dist.keySet()) {
			double quotient = pers_dist.get(key);

			if (hh_ids.get(key) != null) {
				for (HouseholdOfPanelDataId hhId : hh_ids.get(key)) {
					double weight = hhId.get_weight();
					hhId.set_weight(weight*quotient);
				}
			}
		}
	}


	private void normalizeHHWeights(List<HouseholdOfPanelDataId> householdOfPanelDataIds) {

		double total = 0.0;

		for (HouseholdOfPanelDataId hhId : householdOfPanelDataIds) {
			total += hhId.get_weight();
		}

		for (HouseholdOfPanelDataId hhId : householdOfPanelDataIds) {
			double weight = hhId.get_weight() / total * householdOfPanelDataIds.size();
			hhId.set_weight(weight);
		}
	}

	protected Map<String,SortedMap<Integer,Double>> calculateWeightedDistributions(
		SortedMap<Integer,Double> maleDist,
		SortedMap<Integer,Double> femaleDist,
		SortedMap<Integer,Double> empDist,
		SortedMap<Integer,Double> hhDist,
		List<HouseholdOfPanelDataId> householdOfPanelDataIds,
		boolean printStatistics
	) {

		SortedMap<Integer,Double> maleCurr = initDistribution(maleDist);
		SortedMap<Integer,Double> femaleCurr = initDistribution(femaleDist);
		SortedMap<Integer,Double> empCurr = initDistribution(empDist);

		for (HouseholdOfPanelDataId hhId : householdOfPanelDataIds) {

			HouseholdOfPanelData hh = this.households.get(hhId);

			double hh_weight = hhId.get_weight();

			int domcode = hh.domCode();

			double domcode_weight = hhDist.get(domcode);

			double children = hh.numberOfNotReportingChildren();
			incrementDistribution(maleCurr, 5, 0.5*children*hh_weight*domcode_weight);
			incrementDistribution(femaleCurr, 5, 0.5*children*hh_weight*domcode_weight);
			incrementDistribution(empCurr, 8, children*hh_weight*domcode_weight);

			for (PersonOfPanelData pers : this.persons.get(hhId)) {

				int age = pers.age();
				int emp = empDistributionType(pers.getEmploymentTypeAsInt());
				int sex = pers.getGenderTypeAsInt();

				incrementDistribution(empCurr, emp, hh_weight*domcode_weight);

				if (sex == 1) {
					incrementDistribution(maleCurr, age, hh_weight*domcode_weight);
				} else if (sex ==2) {
					incrementDistribution(femaleCurr, age, hh_weight*domcode_weight);
				} else {
					throwIncorrectGenderFor(pers);
				}

			}
		}

		if (printStatistics) {
			log.debug("\nMALE:");
			printDist(maleDist, normalizedDistribution(maleCurr));
			log.debug("\nFEMALE:");
			printDist(femaleDist, normalizedDistribution(femaleCurr));
			log.debug("\nEMP:");
			printDist(empDist, normalizedDistribution(empCurr));
		}

		Map<String,SortedMap<Integer,Double>> result = new TreeMap<String,SortedMap<Integer,Double>>();

		result.put("MALE",  calcQuotient(maleDist, normalizedDistribution(maleCurr)));
		result.put("FEMALE", calcQuotient(femaleDist, normalizedDistribution(femaleCurr)));
		result.put("EMP", calcQuotient(empDist, normalizedDistribution(empCurr)));

		return result;
	}

	private void printDist(
		SortedMap<Integer,Double> soll,
		SortedMap<Integer,Double> ist
	) {

		int cnt = 0;
		double diff1 = 0.0;
		double diff2 = 0.0;

		for (Integer key: soll.keySet()) {

			double ist_val = ist.get(key);
			double soll_val = soll.get(key);
			double quotient = ist_val / soll_val;

			cnt++;
			diff1 += Math.pow(Math.abs(1.0-quotient),2.0);
			diff2 += Math.pow(Math.abs(soll_val-ist_val),2.0);

			log.debug(key + ": " 
							+ ist_val + " / " + soll_val
							+ ":\t" + quotient
			);
		}
		log.debug("MSE: " + diff1/cnt);
		log.debug("MSE: " + diff2/cnt);
	}


	private SortedMap<Integer,Double> normalizedDistribution(RangeDistributionIfc distribution) {

		SortedMap<Integer,Double> data = new TreeMap<Integer,Double>();

		double total = distribution.getTotalAmount();

		for (RangeDistributionItem item:
					(Collection<RangeDistributionItem>) distribution.getItems()) {

			double new_val =  Math.max(item.amount()/total,0.001d);

			data.put(item.upperBound(),new_val);
		}

		return data;
	}

	private SortedMap<Integer,Double> normalizedDistribution(EmploymentDistribution distribution) {

		SortedMap<Integer,Double> data = new TreeMap<Integer,Double>();

		double total = distribution.getTotalAmount();

		for (EmploymentDistributionItem item:
					(Collection<EmploymentDistributionItem>) distribution.getItems()) {

			double new_val =  Math.max(item.amount()/total,0.001d);

			data.put(item.getTypeAsInt(),new_val);
		}

		return data;
	}

	private SortedMap<Integer,Double> normalizedDistribution(Map<Integer,Double> distribution) {

		SortedMap<Integer,Double> data = new TreeMap<Integer,Double>();

		double total = 0.0;

		for (Integer key : distribution.keySet()) {
			total += distribution.get(key);
		}


		for (Integer key : distribution.keySet()) {

			double val = distribution.get(key);

			if (total != 0.0) {
				data.put(key, val/total);
			} else {
				data.put(key, 0.0);
			}
			
		}

		return data;
	}

	private SortedMap<Integer,Double> initDistribution(Map<Integer,Double> dist) {

		SortedMap<Integer,Double> data = new TreeMap<Integer,Double>();

		for (Integer key : dist.keySet()) {

			data.put(key,0.0);
		}

		return data;
	}

	private void incrementDistribution(SortedMap<Integer,Double> distribution, Integer key, double value) {

		SortedMap<Integer,Double> tail = distribution.tailMap(key);

		Integer dist_key;

		if (!tail.isEmpty()) {
			dist_key = tail.firstKey();
		} else {
			dist_key = distribution.lastKey();
		}

		Double oldValue = distribution.get(dist_key);
		
		distribution.put(dist_key, oldValue + value);
	}

	private int empDistributionType(int val) {

		switch(val) {
			case 1:
			case 2:
			case 40:
			case 41:
			case 42:
			case 5:
			case 7:
			case 8:
				return val;
			case 3:
			case 6:
				return 9;
			default:
				return 0;
		}

	}

	private SortedMap<Integer,Double> calcQuotient(Map<Integer,Double> dist1, Map<Integer,Double> dist2) {

		SortedMap<Integer,Double> quotient = new TreeMap<Integer,Double>();

		for (Integer key : dist1.keySet()) {
			double val1 = dist1.get(key);
			double val2 = dist2.get(key);
			quotient.put(key, val1/val2);
		}

		return quotient;
	}

	private void initHHIdLists(
		SortedMap<Integer,Double> maleAgeDistribution,
		SortedMap<Integer,Double> femaleAgeDistribution,
		List<HouseholdOfPanelDataId> householdOfPanelDataIds
	) {

		for (HouseholdOfPanelDataId hhId : householdOfPanelDataIds) {

			for (PersonOfPanelData pers : this.persons.get(hhId)) {

				int age = pers.age();
				int emp = empDistributionType(pers.getEmploymentTypeAsInt());
				int sex = pers.getGenderTypeAsInt();

				if (!this.hh_emp.containsKey(emp)) {
					this.hh_emp.put(emp, new TreeSet<HouseholdOfPanelDataId>());
				}

				this.hh_emp.get(emp).add(hhId);

				if (sex == 1) {
					addToHHList(this.hh_male, age, hhId, maleAgeDistribution);
				} else if (sex ==2) {
					addToHHList(this.hh_female, age, hhId, femaleAgeDistribution);
				} else {
					throwIncorrectGenderFor(pers);
				}
			}

			int emp = Employment.INFANT.getTypeAsInt();

			if (!this.hh_emp.containsKey(emp)) {
				this.hh_emp.put(emp, new TreeSet<HouseholdOfPanelDataId>());
			}

			HouseholdOfPanelData hh = this.households.get(hhId);

			for (int i=0; i<= hh.numberOfNotReportingChildren(); i++) {

				this.hh_emp.get(emp).add(hhId);

				if (this.nextChildGender == Gender.MALE) {
					addToHHList(this.hh_male, 5, hhId, maleAgeDistribution);
					this.nextChildGender = Gender.FEMALE;
				} else {
					addToHHList(this.hh_female, 5, hhId, femaleAgeDistribution);
					this.nextChildGender = Gender.MALE;
				}
			}


		}
	}
	
	private void throwIncorrectGenderFor(PersonOfPanelData pers) {
		throw warn(new IllegalArgumentException("Incorrect gender for person: " + pers), log);
	}

	private void addToHHList(
		Map<Integer,Set<HouseholdOfPanelDataId>> hh_list,
		Integer key,
		HouseholdOfPanelDataId hhId,
		SortedMap<Integer,Double> distribution
	) {

		SortedMap<Integer,Double> tail = distribution.tailMap(key);

		Integer dist_key;

		if (!tail.isEmpty()) {
			dist_key = tail.firstKey();
		} else {
			dist_key = distribution.lastKey();
		}

		if (!hh_list.containsKey(dist_key)) {
			hh_list.put(dist_key, new TreeSet<HouseholdOfPanelDataId>());
		}
		hh_list.get(dist_key).add(hhId);

	}


}
