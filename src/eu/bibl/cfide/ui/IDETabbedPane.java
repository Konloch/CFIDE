package eu.bibl.cfide.ui;

import java.io.File;
import java.lang.reflect.Constructor;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import eu.bibl.banalysis.asm.ClassNode;
import eu.bibl.cfide.engine.compiler.BasicSourceCompiler;
import eu.bibl.cfide.engine.compiler.CFIDECompiler;
import eu.bibl.cfide.project.CFIDEProject;
import eu.bibl.cfide.project.ProjectUtils;

public class IDETabbedPane extends JTabbedPane {
	
	private static final long serialVersionUID = -8407666288357935339L;
	
	public IDETabbedPane() {
		setFocusable(false);
		addTab("Welcome", new JPanel());
	}
	
	public void openJar(String location) {
		File loc = new File(location);
		if (!loc.exists()) {
			JOptionPane.showMessageDialog(null, "File doesn't exist.", "Invalid input file.", JOptionPane.ERROR_MESSAGE);
			return;
		}
		CFIDEProject proj = ProjectUtils.newProject(location);
		String tabName = loc.getName().substring(0, loc.getName().length() - 4);// remove .jar from the end of the name
		ProjectPanel panel = new ProjectPanel(this, tabName, proj, new CFIDECompiler(proj));
		addTab(tabName, panel);
		panel.setupFinal();
		setSelectedComponent(panel);
	}
	
	@SuppressWarnings("unchecked")
	public void openProj(String location) {
		File loc = new File(location);
		if (!loc.exists()) {
			JOptionPane.showMessageDialog(null, "File doesn't exist.", "Invalid input file.", JOptionPane.ERROR_MESSAGE);
			return;
		}
		CFIDEProject proj = ProjectUtils.fromFile(loc);
		String tabName = loc.getName().substring(0, loc.getName().length() - 6);// remove .cfide from the end of the name
		
		BasicSourceCompiler<ClassNode[]> compilerImpl = null;
		String className = null;
		try {
			className = proj.getProperty(CFIDEProject.COMPILER_CLASS, CFIDECompiler.class.getCanonicalName());
			Class<?> c = Class.forName(className);
			for (Constructor<?> constructor : c.getDeclaredConstructors()) {
				if (constructor.getName().endsWith("CFIDEProject)")) { // because the compiler constructor needs to take a project instance to init the builder and parse
					compilerImpl = (BasicSourceCompiler<ClassNode[]>) constructor.newInstance(proj);
				}
			}
		} catch (Exception e) {
			System.out.println("Error loading custom compiler: " + className);
			e.printStackTrace();
			proj.putProperty(CFIDEProject.COMPILER_CLASS, CFIDECompiler.class.getCanonicalName());
		}
		
		ProjectPanel panel = new ProjectPanel(this, tabName, proj, compilerImpl);
		addTab(tabName, panel);
		panel.setupFinal();
		setSelectedComponent(panel);
	}
}