package org.lpe.common.config.experiment;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class KeyRegistry {
	
	private final Map<String, Key<?>> string2keys = new HashMap<>();
	
	private static KeyRegistry instance;
	
	public static KeyRegistry getInstance() {
		if (instance == null) {
			instance = new KeyRegistry();
		}
		
		return instance;
	}

	private KeyRegistry() {
	}
	
	public void register(String name, Key<?> key) {
		string2keys.put(name, key);
	}
	
	public Key<?> get(String keyString) {
		return string2keys.get(keyString);
	}
	
	public void generateEmptyConfigFile(String fileName) throws FileNotFoundException, UnsupportedEncodingException {
		if (fileName == null) {
			throw new IllegalArgumentException("File name must not be null!");
		}
		
		PrintWriter writer = new PrintWriter(fileName, "UTF-8");
		
		for (Entry<String, Key<?>> entry : string2keys.entrySet()) {
			Key<?> key = entry.getValue();
			writer.println(key.toString() + " = <" + key.getType().getName() + ">");
		}
		
		writer.close();
	}
	
}
