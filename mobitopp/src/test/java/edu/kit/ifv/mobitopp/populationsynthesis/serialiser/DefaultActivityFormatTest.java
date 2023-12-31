package edu.kit.ifv.mobitopp.populationsynthesis.serialiser;

import static com.github.npathai.hamcrestopt.OptionalMatchers.hasValue;
import static java.lang.String.valueOf;
import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;

import edu.kit.ifv.mobitopp.data.tourbasedactivitypattern.ExtendedPatternActivity;
import edu.kit.ifv.mobitopp.populationsynthesis.ExampleSetup;

public class DefaultActivityFormatTest {

	private static final int personOid = 1;
	
	private DefaultActivityFormat format;
	private ExtendedPatternActivity activity;
	private PersonPatternActivity personActivity;
	
	@Before
	public void initialise() {
		activity = ExampleSetup.activity();
		personActivity = new PersonPatternActivity(personOid, activity);
		format = new DefaultActivityFormat();
	}

	@Test
	public void serialiseActivityAttributes() throws IOException {
		List<String> prepared = serialise();

		assertThat(prepared, is(equalTo(patternActivity())));
	}

	private List<String> serialise() {
		return format.prepare(personActivity);
	}
	
	@Test
	public void parseActivity() {
		List<String> serialised = serialise();
		
		Optional<PersonPatternActivity> parsed = format.parse(serialised);
		
		assertThat(parsed, hasValue(personActivity));
	}

	private List<String> patternActivity() throws IOException {
		return asList( 
				valueOf(personOid), 
				valueOf(ExampleSetup.type),
				valueOf(ExampleSetup.observedTripDuration), 
				valueOf(ExampleSetup.startTime.toMinutes()),
				valueOf(ExampleSetup.duration),
				valueOf(ExampleSetup.tourNumber),
				valueOf(ExampleSetup.isMainActivity),
				valueOf(ExampleSetup.isInSupertour)
			);
	}
}
