package eu.bibl.cfide.ui;

import static eu.bibl.cfide.config.GlobalConfig.FRAME_HEIGHT_KEY;
import static eu.bibl.cfide.config.GlobalConfig.FRAME_LOCATION_X_KEY;
import static eu.bibl.cfide.config.GlobalConfig.FRAME_LOCATION_Y_KEY;
import static eu.bibl.cfide.config.GlobalConfig.FRAME_MAXIMIZED_KEY;
import static eu.bibl.cfide.config.GlobalConfig.FRAME_WIDTH_KEY;
import static eu.bibl.cfide.config.GlobalConfig.GLOBAL_CONFIG;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowStateListener;
import java.io.File;

import javax.help.HelpSet;
import javax.help.JHelp;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.filechooser.FileNameExtensionFilter;

public class IDEFrame extends JFrame implements ActionListener, ComponentListener, WindowStateListener {
	
	private static final long serialVersionUID = 6900788093562837072L;
	private static IDEFrame instance;
	
	private IDETabbedPane idePanel;
	
	public IDEFrame() {
		super("CFIDE - #Bibl");
		instance = this;
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		double sizeX = GLOBAL_CONFIG.getProperty(FRAME_WIDTH_KEY, 800D);
		double sizeY = GLOBAL_CONFIG.getProperty(FRAME_HEIGHT_KEY, 600D);
		Dimension size = new Dimension((int) sizeX, (int) sizeY);
		setSize(size);
		setPreferredSize(size);
		
		if (GLOBAL_CONFIG.getProperty(FRAME_MAXIMIZED_KEY, false)) {// do both a size set and maximize cuz
			setExtendedState(getExtendedState() | JFrame.MAXIMIZED_BOTH);// when unmaximising the window, it goes really small
		}
		
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
		addWindowStateListener(this);
	}
	
	private void createJMenuBar() {
		JMenuBar menuBar = new JMenuBar();
		
		// file menu stuff =====================================
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
		// =====================================
		
		// help menu stuff =====================================
		JMenu helpMenu = new JMenu("Help");
		
		JMenuItem helpMenuItem = new JMenuItem("Help");
		JMenuItem aboutMenuItem = new JMenuItem("About");
		
		helpMenuItem.setActionCommand("help");
		aboutMenuItem.setActionCommand("about");
		
		helpMenuItem.addActionListener(this);
		aboutMenuItem.addActionListener(this);
		
		helpMenu.add(helpMenuItem);
		helpMenu.add(aboutMenuItem);
		
		menuBar.add(helpMenu);
		// =====================================
		setJMenuBar(menuBar);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();
		switch (cmd) {
			case "openJar": {
				JFileChooser chooser = new JFileChooser();
				FileNameExtensionFilter filter = new FileNameExtensionFilter("Jar Files", "jar");
				chooser.setFileFilter(filter);
				int returnValue = chooser.showOpenDialog(this);
				if (returnValue == JFileChooser.APPROVE_OPTION) {
					File file = chooser.getSelectedFile();
					idePanel.openJar(file.getAbsolutePath());
				}
				break;
			}
			case "openProj": {
				JFileChooser chooser = new JFileChooser();
				FileNameExtensionFilter filter = new FileNameExtensionFilter("CFIDE projects", "cfide");
				chooser.setFileFilter(filter);
				int returnValue = chooser.showOpenDialog(this);
				if (returnValue == JFileChooser.APPROVE_OPTION) {
					File file = chooser.getSelectedFile();
					idePanel.openProj(file.getAbsolutePath());
				}
				break;
			}
			case "help":
				HelpSet helpSet = new HelpSet();
				helpSet.createHelpBroker("test");
				JHelp jHelp = new JHelp(helpSet);
				idePanel.addTab("Help", jHelp);
				idePanel.setSelectedComponent(jHelp);
				// JDialog dialog = new JDialog(this, "Help", true);
				// dialog.add("Center", jHelp);
				// dialog.setSize(new Dimension(400, 300));
				// dialog.setLocationRelativeTo(IDEFrame.this);
				// dialog.setVisible(true);
				break;
			case "about":
				
				break;
			case "exit": {
				System.exit(1);
				break;
			}
		}
	}
	
	@Override
	public void componentResized(ComponentEvent e) {
		Dimension size = getSize();
		if (size.equals(lastMax)) // resize event is called after window state maximize
			return;
		GLOBAL_CONFIG.putProperty(FRAME_MAXIMIZED_KEY, false);
		GLOBAL_CONFIG.putProperty(FRAME_WIDTH_KEY, Integer.valueOf(size.width));
		GLOBAL_CONFIG.putProperty(FRAME_HEIGHT_KEY, Integer.valueOf(size.height));
	}
	
	@Override
	public void componentMoved(ComponentEvent e) {
		Point loc = getLocationOnScreen();
		
		GLOBAL_CONFIG.putProperty(FRAME_LOCATION_X_KEY, Integer.valueOf(loc.x));
		GLOBAL_CONFIG.putProperty(FRAME_LOCATION_Y_KEY, Integer.valueOf(loc.y));
	}
	
	protected Dimension lastMax = null;
	
	@Override
	public void windowStateChanged(WindowEvent e) {
		if ((e.getNewState() & MAXIMIZED_BOTH) != 0) {
			GLOBAL_CONFIG.putProperty(FRAME_MAXIMIZED_KEY, true);
			lastMax = getSize();
		}
	}
	
	@Override
	public void componentShown(ComponentEvent e) {
	}
	
	@Override
	public void componentHidden(ComponentEvent e) {
	}
	
	public static IDEFrame getInstance() {
		return instance;
	}
}