package org.lpe.common.config.experiment;

import java.lang.reflect.Array;
import java.util.HashMap;

import org.lpe.common.util.LpeStringUtils;
import org.lpe.common.util.LpeSupportedTypes;

/**
 * Represents a configuration of an experiment. Define {@link Key Keys} at any
 * place in order to specify key-value pairs. Afterwards,
 * {@link KeyRegistry#generateEmptyConfigFile(String)} creates a template
 * configuration file.<br>
 * Having specified and registered keys, the
 * {@link ConfigParser#parse(java.util.List)} creates a new
 * {@code ExperimentConfiguration} containing a mapping of keys to settings.<br>
 * <b>Example:</b><br>
 * {@code Key<Integer> myKey = new Key<>("MyKey", Integer.class);}<br>
 * {@code ExperimentConfiguration config = ConfigParser.parse(...);}<br>
 * {@code ...}<br>
 * {@code int val = config.get(myKey);}<br>
 * 
 * @author Henning Schulz
 * 
 */
public class ExperimentConfiguration {

	public static final String ARRAY_DELIM = ";";

	private final HashMap<Key<?>, String> configuration = new HashMap<>();

	public void put(Key<?> key, String setting) {
		if (setting == null) {
			return;
		}

		configuration.put(key, setting);
	}

	@SuppressWarnings("unchecked")
	public <T> T get(Key<T> key) {
		if (configuration.get(key) == null) {
			return null;
		}

		if (key.getType().isArray()) {
			return (T) getArray(configuration.get(key), key.getType().getComponentType());
		} else {
			return (T) getElement(configuration.get(key), key.getType());
		}
	}

	@SuppressWarnings("unchecked")
	private <T> T getElement(String setting, Class<T> type) {
		LpeSupportedTypes lpeType = LpeSupportedTypes.get(type);

		if (lpeType != null) {
			return (T) LpeSupportedTypes.getValueOfType(setting, lpeType);
		}

		if (type.isEnum()) {
			for (T constant : type.getEnumConstants()) {
				if (LpeStringUtils.strEqualCaseInsensitive(constant.toString(), setting)) {
					return constant;
				}
			}
		}

		try {
			return type.newInstance();
		} catch (InstantiationException e) {
			throw new IllegalStateException("Illegal configuration: " + setting);
		} catch (IllegalAccessException e) {
			throw new IllegalStateException("Illegal configuration: " + setting);
		}
	}

	@SuppressWarnings("unchecked")
	private <S> S[] getArray(String setting, Class<S> componentType) {
		String[] values = setting.split(ARRAY_DELIM);
		S[] returnVals = (S[]) Array.newInstance(componentType, values.length);

		for (int i = 0; i < values.length; i++) {
			returnVals[i] = (S) getElement(values[i], componentType);
		}

		return returnVals;
	}

	public void fillUp(ExperimentConfiguration other) {
		for (Key<?> key : other.configuration.keySet()) {
			if (!this.configuration.containsKey(key)) {
				this.configuration.put(key, other.configuration.get(key));
			}
		}
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Settings: \n\n");

		for (Key<?> key : configuration.keySet()) {
			builder.append(key.toString());
			builder.append(": ");
			builder.append(configuration.get(key));
			builder.append("\n");
		}

		return builder.toString();
	}
}
