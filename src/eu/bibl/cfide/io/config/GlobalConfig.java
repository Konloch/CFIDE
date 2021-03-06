package eu.bibl.cfide.io.config;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import eu.bibl.cfide.io.FileConstants;

public final class GlobalConfig extends CFIDEConfig {
	
	public static final CFIDEConfig GLOBAL_CONFIG = load(FileConstants.CFIDE_CONFIG);
	
	public static final String FRAME_MAXIMIZED_KEY = "frame.size.max";
	public static final String FRAME_WIDTH_KEY = "frame.size.width";
	public static final String FRAME_HEIGHT_KEY = "frame.size.height";
	public static final String FRAME_LOCATION_X_KEY = "frame.loc.x";
	public static final String FRAME_LOCATION_Y_KEY = "frame.loc.y";
	
	private GlobalConfig() {
		super();
	}
	
	private static final GlobalConfig load(File configFile) {
		if (!configFile.exists()) {
			GlobalConfig config = new GlobalConfig();
			config.file = configFile;
			return config;
		}
		try (BufferedReader reader = new BufferedReader(new FileReader(configFile))) {
			String line;
			String total = "";
			while ((line = reader.readLine()) != null) {
				total += line;
			}
			GlobalConfig config = ConfigUtils.GSON_INSTANCE.fromJson(total, GlobalConfig.class);
			config.file = configFile;
			return config;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
}