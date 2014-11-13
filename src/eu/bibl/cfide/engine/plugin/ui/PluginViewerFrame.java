package eu.bibl.cfide.engine.plugin.ui;

import javax.swing.JFrame;
import javax.swing.JScrollPane;

public class PluginViewerFrame extends JFrame {
	
	private static final long serialVersionUID = -4280007804260779512L;
	
	protected PluginViewerTable table;
	
	public PluginViewerFrame() {
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		
		table = new PluginViewerTable();
		JScrollPane scrollPane = new JScrollPane(table);
		scrollPane.setSize(600, 400);
		scrollPane.setPreferredSize(scrollPane.getSize());
		add(scrollPane);
		
		pack();
		setLocationRelativeTo(null);
	}
	
	@Override
	public void setVisible(boolean b) {
		if (b) {
			setTitle(table.currentContext.tabName);
			table.reload();
		}
		
		super.setVisible(b);
	}
}