package eu.bibl.cfide.engine.plugin.ui;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import eu.bibl.cfide.context.CFIDEContext;
import eu.bibl.cfide.engine.plugin.PluginManager;
import eu.bibl.cfide.eventbus.BusRegistry;
import eu.bibl.cfide.eventbus.EventPriority;
import eu.bibl.cfide.eventbus.EventTarget;
import eu.bibl.cfide.eventbus.events.ContextSwitchEvent;

public class PluginViewerTable extends JTable {
	
	private static final long serialVersionUID = 3540772471127689178L;
	public CFIDEContext currentContext;
	protected Runnable reloader;
	
	public PluginViewerTable() {
		super(new DefaultTableModel(new String[] { "File", "Manfiest", "State" }, 0));
		reloader = new PopulateTableRunnable();
		BusRegistry.getInstance().getGlobalBus().register(this, ContextSwitchEvent.class);
	}
	
	@EventTarget(priority = EventPriority.HIGHEST)
	public synchronized void onContextSwitch(ContextSwitchEvent e) {
		currentContext = e.getNewContext();
	}
	
	public void switchContext(CFIDEContext currentContext) {
		this.currentContext = currentContext;
	}
	
	class PopulateTableRunnable implements Runnable {
		
		@Override
		public void run() {
			if (currentContext != null) {
				PluginManager pluginManager = currentContext.pluginManager;
				
			}
		}
	}
	
	public void reload() {
		if (currentContext != null)// to avoid extra threads
			new Thread(reloader).start();
	}
}
