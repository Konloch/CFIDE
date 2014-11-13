package eu.bibl.cfide.context;

import java.lang.reflect.Constructor;

import eu.bibl.banalysis.asm.ClassNode;
import eu.bibl.bio.jfile.in.JarDownloader;
import eu.bibl.cfide.engine.compiler.BasicSourceCompiler;
import eu.bibl.cfide.engine.compiler.CFIDECompiler;
import eu.bibl.cfide.engine.decompiler.ClassNodeDecompilationUnit;
import eu.bibl.cfide.engine.decompiler.DecompilationUnit;
import eu.bibl.cfide.engine.plugin.PluginManager;
import eu.bibl.cfide.io.config.CFIDEConfig;
import eu.bibl.cfide.ui.IDEFrame;
import eu.bibl.cfide.ui.IDETabbedPane;
import eu.bibl.cfide.ui.ProjectPanel;
import eu.bibl.cfide.ui.editor.EditorTabbedPane;
import eu.bibl.cfide.ui.tree.ClassViewerTree;

public class CFIDEContext {
	
	public final IDEFrame frame;
	public final IDETabbedPane ideTabbedPane;
	public final JarDownloader jarDownloader;
	public final ProjectPanel projectPanel;
	public final CFIDEConfig config;
	public final String tabName;
	public EditorTabbedPane editorTabbedPane;
	public ClassViewerTree tree;
	public final DecompilationUnit<ClassNode> decompiler;
	public final BasicSourceCompiler<ClassNode[]> compiler;
	public final PluginManager pluginManager;
	
	public CFIDEContext(IDEFrame frame, IDETabbedPane ideTabbedPane, JarDownloader jarDownloader, ProjectPanel projectPanel, CFIDEConfig config, String tabName) {
		this.frame = frame;
		this.ideTabbedPane = ideTabbedPane;
		this.jarDownloader = jarDownloader;
		this.projectPanel = projectPanel;
		this.config = config;
		this.tabName = tabName;
		decompiler = getDecompilerImpl();
		compiler = getCompilerImpl();
		pluginManager = getPluginManagerImpl();
	}
	
	private PluginManager getPluginManagerImpl() {
		PluginManager pluginManager = null;
		String className = null;
		try {
			className = config.getProperty(CFIDEConfig.PLUGIN_MANAGER_CLASS_KEY, PluginManager.class.getCanonicalName());
			Class<?> c = Class.forName(className);
			Constructor<?> c1 = c.getConstructor(CFIDEContext.class);
			pluginManager = (PluginManager) c1.newInstance(this);
			if (pluginManager == null) {
				pluginManager = new PluginManager(this);
			}
		} catch (Exception e) {
			System.out.println("Error loading custom plugin manager: " + className);
			e.printStackTrace();
			config.putProperty(CFIDEConfig.PLUGIN_MANAGER_CLASS_KEY, PluginManager.class.getCanonicalName());
			pluginManager = new PluginManager(this);
		}
		return pluginManager;
	}
	
	@SuppressWarnings("unchecked")
	private DecompilationUnit<ClassNode> getDecompilerImpl() {
		DecompilationUnit<ClassNode> decompilerImpl = null;
		String className = null;
		try {
			className = config.getProperty(CFIDEConfig.DECOMPILER_CLASS_KEY, ClassNodeDecompilationUnit.class.getCanonicalName());
			Class<?> c = Class.forName(className);
			Constructor<?> c1 = c.getConstructor(CFIDEContext.class);
			decompilerImpl = (DecompilationUnit<ClassNode>) c1.newInstance(this);
			if (decompilerImpl == null) {
				decompilerImpl = new ClassNodeDecompilationUnit(this);
			}
		} catch (Exception e) {
			System.out.println("Error loading custom compiler: " + className);
			e.printStackTrace();
			config.putProperty(CFIDEConfig.DECOMPILER_CLASS_KEY, ClassNodeDecompilationUnit.class.getCanonicalName());
			decompilerImpl = new ClassNodeDecompilationUnit(this);
		}
		return decompilerImpl;
	}
	
	@SuppressWarnings("unchecked")
	private BasicSourceCompiler<ClassNode[]> getCompilerImpl() {
		BasicSourceCompiler<ClassNode[]> compilerImpl = null;
		String className = null;
		try {
			className = config.getProperty(CFIDEConfig.COMPILER_CLASS_KEY, CFIDECompiler.class.getCanonicalName());
			Class<?> c = Class.forName(className);
			Constructor<?> c1 = c.getConstructor(CFIDEContext.class);
			compilerImpl = (BasicSourceCompiler<ClassNode[]>) c1.newInstance(this);
			if (compilerImpl == null) {
				compilerImpl = new CFIDECompiler(this);
			}
		} catch (Exception e) {
			System.out.println("Error loading custom compiler: " + className);
			e.printStackTrace();
			config.putProperty(CFIDEConfig.COMPILER_CLASS_KEY, CFIDECompiler.class.getCanonicalName());
			compilerImpl = new CFIDECompiler(this);
		}
		return compilerImpl;
	}
}