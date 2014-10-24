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
	
	public static final String DECOMPILER_CLASS = "decompiler.class";
	public static final String DECOMPILER_FIELD_DECOMPILATION_UNIT_CLASS = "decompiler.fndu.class";
	public static final String DECOMPILER_METHOD_DECOMPILATION_UNIT_CLASS = "decompiler.mndu.class";
	
	protected File file;
	protected Map<String, Object> properties;
	
	protected CFIDEProject() {// needed because if GSON creates a new instance, it needs to be added to the cache.
		projects.add(this);
		System.out.println("Created project");
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
				System.out.println("putting " + key);
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
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				System.out.print("Saving " + projects.size() + " project(s).");
				for (CFIDEProject proj : projects) {
					if ((proj != null) && (proj.file != null)) {
						ProjectUtils.save(proj, proj.file);
					}
				}
			}
		});
	}
}