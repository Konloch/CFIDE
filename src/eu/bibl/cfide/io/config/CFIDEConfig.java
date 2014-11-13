package eu.bibl.cfide.io.config;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CFIDEConfig {
	
	protected static final List<CFIDEConfig> configs = new ArrayList<CFIDEConfig>();
	
	public static final String JAR_LOCATION_KEY = "jar.loc";
	public static final String TREE_LIST_INNER_CLASSES_KEY = "tree.list.innerclasses";
	
	public static final String COMPILER_CLASS_KEY = "compiler.class";
	public static final String COMPILER_PARSER_CLASS_KEY = "compiler.parser.class";
	public static final String COMPILER_BUILDER_CLASS_KEY = "compiler.builder.class";
	
	public static final String DECOMPILER_CLASS_KEY = "decompiler.class";
	public static final String DECOMPILER_FIELD_DECOMPILATION_UNIT_CLASS_KEY = "decompiler.fndu.class";
	public static final String DECOMPILER_METHOD_DECOMPILATION_UNIT_CLASS_KEY = "decompiler.mndu.class";
	public static final String DECOMPILER_METHOD_PRINT_LINE_NUMBERS = "decompiler.mndu.linenumbers";
	
	public static final String PLUGIN_MANAGER_CLASS_KEY = "plugin.manager.class";
	
	protected File file;
	protected Map<String, Object> properties;
	
	protected CFIDEConfig() {// needed because if GSON creates a new instance, it needs to be added to the cache.
		properties = new HashMap<String, Object>();
		configs.add(this);
	}
	
	public CFIDEConfig(String jarLocation) {
		this();
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
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				System.out.println("Saving " + configs.size() + " config(s).");
				for (CFIDEConfig config : configs) {
					if ((config != null) && (config.file != null)) {
						System.out.println("Saving: " + config.file);
						for (String key : config.properties.keySet()) {
							System.out.println(key + " " + config.properties.get(key));
						}
						ConfigUtils.save(config, config.file, !(config instanceof GlobalConfig));
					}
				}
			}
		});
	}
}