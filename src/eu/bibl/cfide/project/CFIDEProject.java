package eu.bibl.cfide.project;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CFIDEProject {
	
	protected static final List<CFIDEProject> projects = new ArrayList<CFIDEProject>();
	
	public static final String JAR_LOCATION_KEY = "jar.loc";
	public static final String TREE_LIST_INNER_CLASSES = "tree.list.innerclasses";
	
	protected File file;
	protected Map<String, Object> properties;
	
	public CFIDEProject(String jarLocation) {
		properties = new HashMap<String, Object>();
		properties.put(JAR_LOCATION_KEY, jarLocation);
		projects.add(this);
	}
	
	public <T> T getProperty(String key) {
		return getProperty(key, null);
	}
	
	@SuppressWarnings("unchecked")
	public <T> T getProperty(String key, T defaultValue) {
		try {
			if (properties.containsKey(key)) {
				return (T) properties.get(key);
			} else {
				putProperty(key, defaultValue);
				return defaultValue;
			}
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
	
	static {
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			@Override
			public void run() {
				for (CFIDEProject proj : projects) {
					if ((proj != null) && (proj.file != null))
						ProjectUtils.save(proj, proj.file);
				}
			}
		}));
	}
}