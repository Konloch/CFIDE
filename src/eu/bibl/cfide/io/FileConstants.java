package eu.bibl.cfide.io;

import java.io.File;

public final class FileConstants {
	
	public static final File CFIDE_DIR = new File(System.getProperty("user.home"), "cfide");
	public static final File PLUGINS_DIR = new File(CFIDE_DIR, "plugins");
	public static final File CFIDE_CONFIG = new File(CFIDE_DIR, "CFIDEGlobal.config");
	
	static {
		if (!CFIDE_DIR.exists()) {
			CFIDE_DIR.mkdirs();
		}
		
		if (!PLUGINS_DIR.exists()) {
			PLUGINS_DIR.mkdir();
		}
		
		// if (!CFIDE_CONFIG.exists()) { dont create
		// try {
		// CFIDE_CONFIG.createNewFile();
		// } catch (IOException e) {
		// e.printStackTrace();
		// }
		// }
	}
}
