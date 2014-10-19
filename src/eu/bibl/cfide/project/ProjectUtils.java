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
	
	public static final WorkspaceProject newProject(String jarLocation) {
		return new WorkspaceProject(jarLocation);
	}
	
	public static final WorkspaceProject fromFile(File projFile) {
		try (BufferedReader reader = new BufferedReader(new FileReader(projFile))) {
			String line;
			String total = "";
			while ((line = reader.readLine()) != null) {
				total += line;
			}
			return GSON_INSTANCE.fromJson(total, WorkspaceProject.class);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static final boolean save(WorkspaceProject proj, File projFile) {
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