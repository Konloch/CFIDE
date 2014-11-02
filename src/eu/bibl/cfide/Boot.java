package eu.bibl.cfide;

import java.io.IOException;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import eu.bibl.cfide.ui.IDEFrame;
import eu.bibl.cfide.ui.UISettings;

public class Boot {
	
	public static void main(String[] args) throws IOException {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			UIManager.put("Tree.closedIcon", UISettings.PACKAGE_ICON);
			UIManager.put("Tree.openIcon", UISettings.PACKAGE_ICON);
			UIManager.put("Tree.leafIcon", UISettings.CLASS_ICON);
		} catch (Exception e) {
			e.printStackTrace();
		}
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				new IDEFrame();
			}
		});
	}
	
	public void d() {
		System.out.println("yolo");
	}
}