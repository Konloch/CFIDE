package eu.bibl.cfide;

import javax.swing.UIManager;

import eu.bibl.cfide.ui.IDEFrame;
import eu.bibl.cfide.ui.UISettings;

public class Boot {
	
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			UIManager.put("Tree.closedIcon", UISettings.PACKAGE_ICON);
			UIManager.put("Tree.openIcon", UISettings.PACKAGE_ICON);
			UIManager.put("Tree.leafIcon", UISettings.CLASS_ICON);
		} catch (Exception e) {
			e.printStackTrace();
		}
		IDEFrame frame = new IDEFrame();
		frame.setVisible(true);
	}
}