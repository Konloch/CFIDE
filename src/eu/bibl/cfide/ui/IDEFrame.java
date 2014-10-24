package eu.bibl.cfide.ui;

import static eu.bibl.cfide.config.GlobalConfig.FRAME_HEIGHT_KEY;
import static eu.bibl.cfide.config.GlobalConfig.FRAME_LOCATION_X_KEY;
import static eu.bibl.cfide.config.GlobalConfig.FRAME_LOCATION_Y_KEY;
import static eu.bibl.cfide.config.GlobalConfig.FRAME_WIDTH_KEY;
import static eu.bibl.cfide.config.GlobalConfig.GLOBAL_CONFIG;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.filechooser.FileNameExtensionFilter;

public class IDEFrame extends JFrame implements ActionListener, ComponentListener {
	
	private static final long serialVersionUID = 6900788093562837072L;
	
	protected static Dimension FRAME_SIZE;
	
	private IDETabbedPane idePanel;
	
	public IDEFrame() {
		super("CFIDE - #Bibl");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		double sizeX = GLOBAL_CONFIG.getProperty(FRAME_WIDTH_KEY, 800D);
		double sizeY = GLOBAL_CONFIG.getProperty(FRAME_HEIGHT_KEY, 600D);
		FRAME_SIZE = new Dimension((int) sizeX, (int) sizeY);
		
		setSize(FRAME_SIZE);
		setPreferredSize(FRAME_SIZE);
		setLayout(new BorderLayout());
		
		createJMenuBar();
		idePanel = new IDETabbedPane();
		add(idePanel);
		
		pack();
		
		if (GLOBAL_CONFIG.exists(FRAME_LOCATION_X_KEY) && GLOBAL_CONFIG.exists(FRAME_LOCATION_Y_KEY)) {
			double locX = GLOBAL_CONFIG.<Double> getProperty(FRAME_LOCATION_X_KEY);
			double locY = GLOBAL_CONFIG.<Double> getProperty(FRAME_LOCATION_Y_KEY);
			setLocation(new Point((int) locX, (int) locY));
		} else {
			setLocationRelativeTo(null);
		}
		
		setVisible(true);
		
		Point loc = getLocationOnScreen();
		GLOBAL_CONFIG.putProperty(FRAME_LOCATION_X_KEY, Integer.valueOf(loc.x));
		GLOBAL_CONFIG.putProperty(FRAME_LOCATION_Y_KEY, Integer.valueOf(loc.y));
		
		addComponentListener(this);
	}
	
	private void createJMenuBar() {
		JMenuBar menuBar = new JMenuBar();
		JMenu fileMenu = new JMenu("File");
		
		JMenuItem openJarItem = new JMenuItem("Open Jar");
		JMenuItem openProjItem = new JMenuItem("Open Proj");
		JMenuItem exitMenuItem = new JMenuItem("Exit");
		
		openJarItem.setActionCommand("openJar");
		openProjItem.setActionCommand("openProj");
		exitMenuItem.setActionCommand("exit");
		
		openJarItem.addActionListener(this);
		openProjItem.addActionListener(this);
		exitMenuItem.addActionListener(this);
		
		fileMenu.add(openJarItem);
		fileMenu.add(openProjItem);
		fileMenu.add(exitMenuItem);
		
		menuBar.add(fileMenu);
		setJMenuBar(menuBar);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("openJar")) {
			JFileChooser chooser = new JFileChooser();
			FileNameExtensionFilter filter = new FileNameExtensionFilter("Jar Files", "jar");
			chooser.setFileFilter(filter);
			int returnValue = chooser.showOpenDialog(this);
			if (returnValue == JFileChooser.APPROVE_OPTION) {
				File file = chooser.getSelectedFile();
				idePanel.openJar(file.getAbsolutePath());
			}
		} else if (e.getActionCommand().equals("openProj")) {
			JFileChooser chooser = new JFileChooser();
			FileNameExtensionFilter filter = new FileNameExtensionFilter("CFIDE projects", "cfide");
			chooser.setFileFilter(filter);
			int returnValue = chooser.showOpenDialog(this);
			if (returnValue == JFileChooser.APPROVE_OPTION) {
				File file = chooser.getSelectedFile();
				idePanel.openProj(file.getAbsolutePath());
			}
		} else if (e.getActionCommand().equals("exit")) {
			System.exit(1);
		}
	}
	
	@Override
	public void componentResized(ComponentEvent e) {
		Dimension size = getSize();
		GLOBAL_CONFIG.putProperty(FRAME_WIDTH_KEY, Integer.valueOf(size.width));
		GLOBAL_CONFIG.putProperty(FRAME_HEIGHT_KEY, Integer.valueOf(size.height));
	}
	
	@Override
	public void componentMoved(ComponentEvent e) {
		Point loc = getLocationOnScreen();
		GLOBAL_CONFIG.putProperty(FRAME_LOCATION_X_KEY, Integer.valueOf(loc.x));
		GLOBAL_CONFIG.putProperty(FRAME_LOCATION_Y_KEY, Integer.valueOf(loc.y));
	}
	
	@Override
	public void componentShown(ComponentEvent e) {
	}
	
	@Override
	public void componentHidden(ComponentEvent e) {
	}
}