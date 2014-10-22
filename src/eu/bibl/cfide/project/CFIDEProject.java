package eu.bibl.cfide.project;

import java.util.HashMap;
import java.util.Map;

public class CFIDEProject {
	
	public static final String JAR_LOCATION_KEY = "jar.loc";
	
	protected Map<String, Object> properties;
	
	public CFIDEProject(String jarLocation) {
		properties = new HashMap<String, Object>();
		properties.put(JAR_LOCATION_KEY, jarLocation);
	}
	
	public <T> T getProperty(String key) {
		return getProperty(key, null);
	}
	
	@SuppressWarnings("unchecked")
	public <T> T getProperty(String key, T defaultValue) {
		try {
			return (T) properties.get(key);
		} catch (ClassCastException e) {
			return defaultValue;
		}
	}
	
	public void putProperty(String key, Object o) {
		properties.put(key, o);
	}
	
	public boolean exists(String key) {
		return properties.containsKey(key);
	}
}