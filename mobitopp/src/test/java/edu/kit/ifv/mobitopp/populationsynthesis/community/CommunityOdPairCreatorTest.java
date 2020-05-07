package edu.kit.ifv.mobitopp.populationsynthesis.community;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import edu.kit.ifv.mobitopp.data.DemandZone;
import edu.kit.ifv.mobitopp.populationsynthesis.ExampleDemandZones;
import edu.kit.ifv.mobitopp.populationsynthesis.PersonBuilder;

@ExtendWith(MockitoExtension.class)
public class CommunityOdPairCreatorTest {

	@Mock
	private PersonBuilder person;
	@Mock
	private CommunityRepository communityRepository;
	private Community otherCommunity;
	private DemandZone otherZone;
	private DemandZone noWorkZone;

	@BeforeEach
	public void beforeEach() {
		otherZone = ExampleDemandZones.create().getOtherZone();
		noWorkZone = ExampleDemandZones.create().getZoneWithoutLocations();
		otherCommunity = new MultipleZones("community", otherZone, noWorkZone);
	}

	@Test
	void createPairsForCommunityZones() throws Exception {
		DemandZone someZone = ExampleDemandZones.create().getSomeZone();
		when(communityRepository.getCommutingCommunitiesFrom(someZone.getId()))
				.thenReturn(Stream.of(otherCommunity));
		when(person.homeZone()).thenReturn(someZone.zone());

		CommunityOdPairCreator selector = CommunityOdPairCreator.forWork(communityRepository);

		assertThat(selector.select(person), containsInAnyOrder(OdPair.from(someZone, otherZone)));

		verify(communityRepository).getCommutingCommunitiesFrom(someZone.getId());
	}
	
	@Test
	void scaleRelations() throws Exception {
		int numberOfCommuters = 1;
		CommunityOdPairCreator creator = CommunityOdPairCreator.forWork(communityRepository);

		creator.scale(otherCommunity, numberOfCommuters);

		verify(communityRepository).scale(otherCommunity, numberOfCommuters);
	}
}
