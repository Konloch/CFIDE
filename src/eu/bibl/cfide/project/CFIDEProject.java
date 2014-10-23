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
	public static final String COMPILER_CLASS = "compiler.class";
	public static final String COMPILER_PARSER_CLASS = "compiler.parser.class";
	public static final String COMPILER_BUILDER_CLASS = "compiler.builder.class";
	
	protected File file;
	protected Map<String, Object> properties;
	
	private CFIDEProject() {// needed because if GSON creates a new instance, it needs to be added to the cache.
		projects.add(this);
	}
	
	public CFIDEProject(String jarLocation) {
		this();
		properties = new HashMap<String, Object>();
		properties.put(JAR_LOCATION_KEY, jarLocation);
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