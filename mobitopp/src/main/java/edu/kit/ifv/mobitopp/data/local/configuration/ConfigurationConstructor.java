package edu.kit.ifv.mobitopp.data.local.configuration;

import static edu.kit.ifv.mobitopp.util.collections.StreamUtils.warn;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.yaml.snakeyaml.constructor.AbstractConstruct;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.ScalarNode;
import org.yaml.snakeyaml.nodes.Tag;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ConfigurationConstructor extends Constructor {

	public ConfigurationConstructor(Tag tag, Pattern timeSpanPattern) {
		super();
		this.yamlConstructors.put(tag, new ConstructTimeSpan(timeSpanPattern));
	}

	private class ConstructTimeSpan extends AbstractConstruct {

		private final Pattern timeSpanPatter;

		public ConstructTimeSpan(Pattern timeSpanPatter) {
			super();
			this.timeSpanPatter = timeSpanPatter;
		}

		@Override
		public Object construct(Node node) {
			String value = constructScalar((ScalarNode) node).toString();
			Matcher matcher = timeSpanPatter.matcher(value);
			if (matcher.matches()) {
				int from = asInt(matcher.group(1));
				int to = asInt(matcher.group(2));
				return TimeSpan.between(from, to);
			}
			throw warn(new IllegalArgumentException("Cannot parse time span: " + value), log);
		}

		private int asInt(String value) {
			return Integer.parseInt(value);
		}

	}
}