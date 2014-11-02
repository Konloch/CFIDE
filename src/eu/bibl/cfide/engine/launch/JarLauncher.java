package eu.bibl.cfide.engine.launch;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.help.UnsupportedOperationException;

public final class JarLauncher implements Runnable {
	
	private static final JarLauncher jarLauncher = new JarLauncher();
	private ProcessBuilder pb;
	private Process process;
	private volatile boolean busy = false;
	
	public synchronized void start(File jarLocation, String mainClass) throws IOException {
		poll();
		pb = new ProcessBuilder("java", "-noverify", "-cp", jarLocation.getAbsolutePath(), mainClass);
		pb.redirectErrorStream(true);
		process = pb.start();
		new Thread(this).start();
	}
	
	public void poll() {
		if (busy)
			throw new UnsupportedOperationException("Cannot launch 2 jars at once.");
	}
	
	private boolean isRunning() {
		try {
			process.exitValue();
		} catch (IllegalThreadStateException e) {
			return true;
		}
		return false;
	}
	
	@Override
	public void run() {
		busy = true;
		BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
		String line;
		while (isRunning()) {
			try {
				while ((line = reader.readLine()) != null) {
					System.out.println("[MONITOR]: " + line);
				}
			} catch (IOException e) {
				e.printStackTrace();
				break;// while loop
			} finally {
				busy = false;
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		busy = false;
	}
	
	public static JarLauncher getInstance() {
		return jarLauncher;
	}
}