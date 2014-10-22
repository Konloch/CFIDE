package eu.bibl.cfide.project;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public final class ProjectUtils {
	
	public static final Gson GSON_INSTANCE = new GsonBuilder().setPrettyPrinting().create();
	
	public static final CFIDEProject newProject(String jarLocation) {
		return new CFIDEProject(jarLocation);
	}
	
	public static final CFIDEProject fromFile(File projFile) {
		try (BufferedReader reader = new BufferedReader(new FileReader(projFile))) {
			String line;
			String total = "";
			while ((line = reader.readLine()) != null) {
				total += line;
			}
			return GSON_INSTANCE.fromJson(total, CFIDEProject.class);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static final boolean save(CFIDEProject proj, File projFile) {
		String name = projFile.getName();
		if (!name.endsWith(".cfide")) {
			if (name.contains(".")) {
				String nameOfFile = name.substring(0, name.lastIndexOf("."));
				name = nameOfFile + ".cfide";
			} else {
				name += ".cfide";
			}
			projFile = new File(projFile.getParentFile(), name);
		}
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(projFile))) {
			String gsonString = GSON_INSTANCE.toJson(proj);
			writer.write(gsonString);
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
}