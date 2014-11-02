package eu.bibl.cfide.ui.editor;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.net.URL;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rtextarea.RTextScrollPane;

import eu.bibl.banalysis.asm.ClassNode;
import eu.bibl.banalysis.storage.classes.ClassContainer;
import eu.bibl.bio.jfile.in.JarDownloader;
import eu.bibl.cfide.config.CFIDEConfig;
import eu.bibl.cfide.engine.compiler.BasicSourceCompiler;
import eu.bibl.cfide.engine.launch.JarLauncher;
import eu.bibl.cfide.ui.ProjectPanel;
import eu.bibl.cfide.ui.UISettings;

public class EditorTextTab extends RTextScrollPane implements MouseListener, ActionListener {
	
	private static final long serialVersionUID = -9001184665877228717L;
	
	protected CFIDEConfig config;
	protected EditorTabbedPane tabbedPane;
	protected ProjectPanel projPanel;
	protected String title;
	protected JPopupMenu popupMenu;
	
	public EditorTextTab(CFIDEConfig config, EditorTabbedPane tabbedPane, ProjectPanel projPanel, String title) {
		super(new RSyntaxTextArea());
		this.config = config;
		this.tabbedPane = tabbedPane;
		this.projPanel = projPanel;
		this.title = title;
		init();
	}
	
	protected void init() {
		((RSyntaxTextArea) getTextArea()).setAntiAliasingEnabled(true);
		createPopupMenu(); // needs to be first
		createTabPanel();
	}
	
	protected String lastMainClass = null;
	
	protected void createPopupMenu() {
		// Close menu popup
		popupMenu = new JPopupMenu();
		JMenuItem saveJarMenuItem = new JMenuItem("Save Jar");
		saveJarMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser();
				FileNameExtensionFilter filter = new FileNameExtensionFilter("Jar Files", "jar");
				fileChooser.setFileFilter(filter);
				int returnValue = fileChooser.showSaveDialog(EditorTextTab.this);
				if (returnValue == JFileChooser.APPROVE_OPTION) {
					final File file = fileChooser.getSelectedFile();
					BasicSourceCompiler<ClassNode[]> compiler = projPanel.getCompilerImpl();
					final JarDownloader dl = projPanel.getJarDownloader();
					tabbedPane.compileAndDump(dl, compiler, file, false, null);
				}
			}
		});
		JMenuItem saveJarRunMenuItem = new JMenuItem("Save & Run Jar");
		saveJarRunMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					JarLauncher.getInstance().poll();
				} catch (Exception e1) {
					JOptionPane.showMessageDialog(null, e1.getMessage(), "Already running Jar", JOptionPane.ERROR_MESSAGE);
					return;
				}
				
				JFileChooser fileChooser = new JFileChooser();
				FileNameExtensionFilter filter = new FileNameExtensionFilter("Jar Files", "jar");
				fileChooser.setFileFilter(filter);
				int returnValue = fileChooser.showSaveDialog(EditorTextTab.this);
				if (returnValue == JFileChooser.APPROVE_OPTION) {
					final File file = fileChooser.getSelectedFile();
					BasicSourceCompiler<ClassNode[]> compiler = projPanel.getCompilerImpl();
					final JarDownloader dl = projPanel.getJarDownloader();
					ClassContainer container = dl.getContents();
					String mainClass = lastMainClass;
					
					if (mainClass == null) {
						urlFor: for (URL url : container.resources.keySet()) {
							Map<String, byte[]> res = container.resources.get(url);
							for (String name : res.keySet()) {
								if (name.equals("META-INF/MANIFEST.MF")) {
									String data = new String(res.get(name));
									String[] lines = data.split("\n");
									for (String line : lines) {
										String[] splits = line.split(":");
										if (splits.length == 2) {
											String key = splits[0];
											if (key.equals("Main-Class")) {
												String val = splits[1];
												lastMainClass = mainClass = val.trim();
												break urlFor;
											}
										}
									}
								}
							}
						}
					}
					
					if (mainClass == null) {
						mainClass = JOptionPane.showInputDialog(null, "No Main-Class", mainClass).trim();
						lastMainClass = mainClass;
						if (mainClass == null)
							return;
					}
					tabbedPane.compileAndDump(dl, compiler, file, true, mainClass);
					
				}
			}
		});
		// Close this menu button
		JMenuItem closeMenuItem = new JMenuItem("Close");
		closeMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				tabbedPane.remove(EditorTextTab.this);
			}
		});
		// Close others menu button
		JMenuItem closeOthers = new JMenuItem("Close Others");
		closeOthers.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				for (EditorTextTab sp : tabbedPane.tabs.values()) {
					if (!EditorTextTab.this.equals(sp)) {
						remove(sp);
					}
				}
			}
		});
		
		// Close all menu button
		JMenuItem closeAll = new JMenuItem("Close All");
		closeAll.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				for (EditorTextTab sp : tabbedPane.tabs.values()) {
					tabbedPane.remove(sp);
				}
			}
		});
		
		popupMenu.add(saveJarMenuItem);
		popupMenu.add(saveJarRunMenuItem);
		popupMenu.add(closeMenuItem);
		popupMenu.add(closeOthers);
		popupMenu.add(closeAll);
	}
	
	protected int index;
	protected JPanel tabNamePanel;
	protected JLabel tabNameLabel;
	protected JButton tabCloseButton;
	
	protected void createTabPanel() {
		tabNamePanel = new JPanel(new BorderLayout(5, 2));
		tabNamePanel.setOpaque(false);
		tabNamePanel.setFocusable(false);
		
		tabNameLabel = new JLabel(title);
		tabNameLabel.addMouseListener(this);
		
		tabCloseButton = new JButton(UISettings.CLOSE_BUTTON_ICON);
		tabCloseButton.setFocusable(false);
		tabCloseButton.setActionCommand("close");
		tabCloseButton.addActionListener(this);
		tabCloseButton.setSize(UISettings.CLOSE_BUTTON_SIZE);
		tabCloseButton.setPreferredSize(UISettings.CLOSE_BUTTON_SIZE);
		
		tabNamePanel.setComponentPopupMenu(popupMenu);
		tabNamePanel.add(tabNameLabel, BorderLayout.WEST);
		tabNamePanel.add(tabCloseButton);
	}
	
	public void setupFinal() {// called from EditorTabbedPane.createTextTab
		index = tabbedPane.indexOfTab(title);
		tabbedPane.setTabComponentAt(index, tabNamePanel);
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
	}
	
	@Override
	public void mousePressed(MouseEvent e) {
		tabbedPane.setSelectedComponent(EditorTextTab.this);
		if (e.getButton() != MouseEvent.BUTTON1) {
			if (popupMenu.isShowing())
				popupMenu.setVisible(false);
			popupMenu.show(tabNamePanel, e.getX(), e.getY());
		}
	}
	
	@Override
	public void mouseReleased(MouseEvent e) {
	}
	
	@Override
	public void mouseEntered(MouseEvent e) {
	}
	
	@Override
	public void mouseExited(MouseEvent e) {
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();
		if (cmd.equals("close")) {
			tabbedPane.remove(EditorTextTab.this);
		}
	}
	
	public void setText(String text) {
		getTextArea().setText(text);
		SwingUtilities.invokeLater(RESET_VIEW_RUNNABLE);
	}
	
	protected ResetViewRunnable RESET_VIEW_RUNNABLE = new ResetViewRunnable(); // shared scrollpane scroller, technically not thread safe.
	
	protected class ResetViewRunnable implements Runnable {
		
		@Override
		public void run() {
			getHorizontalScrollBar().setValue(0);
			getVerticalScrollBar().setValue(0);
		}
	}
}