package eu.bibl.cfide.engine.plugin;

import eu.bibl.cfide.context.CFIDEContext;

public abstract class AbstractCFIDEPlugin {
	
	protected final String pluginName;
	
	public AbstractCFIDEPlugin(String pluginName) {
		this.pluginName = pluginName;
	}
	
	public abstract void load(CFIDEContext context);
	
	public abstract void unload(CFIDEContext context);
}