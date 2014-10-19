package eu.bibl.cfide.ui;

import java.awt.Dimension;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;

public final class UISettings {

	public static Icon CLOSE_BUTTON_ICON;
	public static Icon CLASS_ICON;
	public static Icon PACKAGE_ICON;

	public static Dimension CLOSE_BUTTON_SIZE = new Dimension(16, 16);

	static {
		try {
			CLOSE_BUTTON_ICON = new ImageIcon(ImageIO.read(new File("res/close.png")));
			CLASS_ICON = new ImageIcon(ImageIO.read(new File("res/class.png")));
			PACKAGE_ICON = new ImageIcon(ImageIO.read(new File("res/package.png")));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}