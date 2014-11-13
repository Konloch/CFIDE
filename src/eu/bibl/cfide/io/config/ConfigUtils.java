package eu.bibl.cfide.io.config;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public final class ConfigUtils {
	
	public static final Gson GSON_INSTANCE = new GsonBuilder().setPrettyPrinting().enableComplexMapKeySerialization().create();
	
	public static final CFIDEConfig newConfig(String jarLocation) {
		return new CFIDEConfig(jarLocation);
	}
	
	public static final CFIDEConfig fromFile(File configFile) {
		if (!configFile.exists())
			return new CFIDEConfig(configFile.getAbsolutePath());
		try (BufferedReader reader = new BufferedReader(new FileReader(configFile))) {
			String line;
			String total = "";
			while ((line = reader.readLine()) != null) {
				total += line;
			}
			CFIDEConfig config = GSON_INSTANCE.fromJson(total, CFIDEConfig.class);
			config.file = configFile;
			return config;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static final boolean save(CFIDEConfig config, File configFile, boolean fixName) {
		if (fixName) {
			String name = configFile.getName();
			if (!name.endsWith(".cfide")) {
				if (name.contains(".")) {
					String nameOfFile = name.substring(0, name.lastIndexOf("."));
					name = nameOfFile + ".cfide";
				} else {
					name += ".cfide";
				}
				configFile = new File(configFile.getParentFile(), name);
			}
		}
		
		config.file = configFile;
		
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(configFile))) {
			String gsonString = GSON_INSTANCE.toJson(config);
			writer.write(gsonString);
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
}