package eu.bibl.cfide.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.filechooser.FileNameExtensionFilter;

public class IDEFrame extends JFrame implements ActionListener {
	
	private static final long serialVersionUID = 6900788093562837072L;
	
	private static final Dimension FRAME_SIZE = new Dimension(800, 600);
	
	private IDETabbedPane idePanel;
	
	public IDEFrame() {
		super("CFIDE - #Bibl");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(FRAME_SIZE);
		setPreferredSize(FRAME_SIZE);
		setLayout(new BorderLayout());
		
		createJMenuBar();
		idePanel = new IDETabbedPane();
		add(idePanel);
		
		pack();
		setLocationRelativeTo(null);
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
}