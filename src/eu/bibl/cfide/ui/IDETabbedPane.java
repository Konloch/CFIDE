package eu.bibl.cfide.ui;

import java.awt.Component;
import java.io.File;
import java.io.IOException;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import eu.bibl.cfide.eventbus.BusRegistry;
import eu.bibl.cfide.eventbus.events.ContextSwitchEvent;
import eu.bibl.cfide.io.config.CFIDEConfig;
import eu.bibl.cfide.io.config.ConfigUtils;

public class IDETabbedPane extends JTabbedPane implements ChangeListener {
	
	private static final long serialVersionUID = -8407666288357935339L;
	
	public IDETabbedPane() {
		setFocusable(false);
		addTab("Welcome", new JPanel());
		addChangeListener(this);
	}
	
	public void openJar(String location) {
		File loc = new File(location);
		if (!loc.exists()) {
			JOptionPane.showMessageDialog(null, "File doesn't exist.", "Invalid input file.", JOptionPane.ERROR_MESSAGE);
			return;
		}
		CFIDEConfig config = ConfigUtils.newConfig(location);
		String tabName = loc.getName().substring(0, loc.getName().length() - 4);// remove .jar from the end of the name
		
		try {
			ProjectPanel panel = new ProjectPanel();
			panel.init(IDEFrame.getInstance(), this, tabName, config);
			addTab(tabName, panel);
			panel.setupFinal();
			setSelectedComponent(panel);
		} catch (IOException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Error loading jar, check console", "Error", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	public void openProj(String location) {
		File loc = new File(location);
		if (!loc.exists()) {
			JOptionPane.showMessageDialog(null, "File doesn't exist.", "Invalid input file.", JOptionPane.ERROR_MESSAGE);
			return;
		}
		CFIDEConfig config = ConfigUtils.fromFile(loc);
		String tabName = loc.getName().substring(0, loc.getName().length() - 6);// remove .cfide from the end of the name
		
		try {
			ProjectPanel panel = new ProjectPanel();
			panel.init(IDEFrame.getInstance(), this, tabName, config);
			addTab(tabName, panel);
			panel.setupFinal();
			setSelectedComponent(panel);
		} catch (IOException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Error loading jar, check console", "Error", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	@Override
	public void stateChanged(ChangeEvent e) {
		Component panel = getSelectedComponent();
		if (panel instanceof ProjectPanel) {
			ContextSwitchEvent e1 = new ContextSwitchEvent(((ProjectPanel) panel).getContext());
			BusRegistry.getInstance().getGlobalBus().dispatch(e1);
		}
	}
}