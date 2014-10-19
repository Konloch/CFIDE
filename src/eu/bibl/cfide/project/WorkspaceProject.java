package eu.bibl.cfide.project;

import java.util.HashMap;
import java.util.Map;

public class WorkspaceProject {
	
	protected String jarLocation;
	protected Map<String, Object> properties;
	
	public WorkspaceProject(String jarLocation) {
		this.jarLocation = jarLocation;
		properties = new HashMap<String, Object>();
	}
	
	public String getJarLocation() {
		return jarLocation;
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