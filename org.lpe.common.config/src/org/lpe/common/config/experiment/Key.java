package org.lpe.common.config.experiment;

public class Key<T> {

	private final Class<T> type;

	private final String name;

	public Key(String name, Class<T> type) {
		this.type = type;
		this.name = name;
		KeyRegistry.getInstance().register(name, this);
	}

	public Class<T> getType() {
		return type;
	}

	@Override
	public String toString() {
		return name;
	}

}
