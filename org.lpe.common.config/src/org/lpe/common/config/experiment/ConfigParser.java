package org.lpe.common.config.experiment;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfigParser {

	private static final Logger LOGGER = LoggerFactory.getLogger(ConfigParser.class);

	private static final String TOKEN_COMMENT = "#";
	private static final String TOKEN_DEF = "=";

	public static ExperimentConfiguration parse(List<String> settingsList) {
		ExperimentConfiguration config = new ExperimentConfiguration();

		for (String s : settingsList) {
			String[] splitted = s.trim().split(TOKEN_DEF);

			if (splitted.length < 2) {
				continue;
			}

			String sKey = splitted[0].trim();
			String value = splitted[1].trim();

			if (sKey.startsWith(TOKEN_COMMENT)) {
				continue;
			}

			Key<?> key = KeyRegistry.getInstance().get(sKey);

			if (key == null) {
				LOGGER.warn("Key \"{}\" not known!. Saving it anyway.", sKey);
				key = new Key<String>(sKey, String.class);
			}

			if (key.getType().isArray()) {
				value = s.trim().substring(s.indexOf(TOKEN_DEF) + 1).trim();
			}

			config.put(key, value);
		}

		return config;
	}
}
