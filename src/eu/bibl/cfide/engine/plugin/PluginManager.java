package eu.bibl.cfide.engine.plugin;

import java.util.List;

import eu.bibl.cfide.context.CFIDEContext;

public class PluginManager {
	
	protected List<AbstractCFIDEPlugin> loadedPlugins;
	protected CFIDEContext context;
	
	public PluginManager(CFIDEContext context) {
		this.context = context;
	}
}