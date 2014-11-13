package eu.bibl.cfide.ui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import eu.bibl.bio.JarInfo;
import eu.bibl.bio.jfile.in.JarDownloader;
import eu.bibl.cfide.context.CFIDEContext;
import eu.bibl.cfide.io.config.CFIDEConfig;
import eu.bibl.cfide.io.config.ConfigUtils;
import eu.bibl.cfide.ui.editor.EditorTabbedPane;
import eu.bibl.cfide.ui.editor.EditorTextTab;
import eu.bibl.cfide.ui.tree.ClassViewerTree;

public class ProjectPanel extends JPanel implements MouseListener, ActionListener {
	
	private static final long serialVersionUID = 7392644318314592144L;
	
	protected CFIDEContext context;
	
	public ProjectPanel() throws IOException {
		super(new BorderLayout());
	}
	
	protected void init(IDEFrame frame, IDETabbedPane tabbedPane, String tabName, CFIDEConfig config) throws IOException {
		File jarFile = new File(config.<String> getProperty(CFIDEConfig.JAR_LOCATION_KEY));
		JarDownloader dl = new JarDownloader(new JarInfo(jarFile));
		try {
			if (!dl.parse()) {
				throw new IOException("Couldn't load jarfile: " + jarFile);
			}
		} catch (RuntimeException e) {
			throw new IOException("Error loading jar", e);
		}
		
		context = new CFIDEContext(frame, tabbedPane, dl, this, config, tabName);
		
		EditorTabbedPane etp = new EditorTabbedPane(context);
		context.editorTabbedPane = etp;
		
		ClassViewerTree tree = new ClassViewerTree(jarFile.getName(), context);
		context.tree = tree;
		
		JScrollPane scrollPane = new JScrollPane(tree);
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		splitPane.setResizeWeight(0.115D);
		splitPane.add(scrollPane);
		splitPane.add(etp);
		
		add(splitPane);
		createPopupMenu();// needs to be called first
		createTabPanel();
	}
	
	public CFIDEContext getContext() {
		return context;
	}
	
	public String getText(String className) {
		EditorTextTab ett = context.editorTabbedPane.getTextTab(className);
		return ett.getTextArea().getText();
	}
	
	protected JPopupMenu popupMenu;
	
	protected void createPopupMenu() {
		// save menu popup
		popupMenu = new JPopupMenu();
		JMenuItem saveMenuItem = new JMenuItem("Save");
		saveMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();
				FileNameExtensionFilter filter = new FileNameExtensionFilter("CFIDE projects", "cfide");
				chooser.setFileFilter(filter);
				int returnValue = chooser.showSaveDialog(ProjectPanel.this);
				if (returnValue == JFileChooser.APPROVE_OPTION) {
					File file = chooser.getSelectedFile();
					if (file.exists()) {
						int yesNoValue = JOptionPane.showOptionDialog(ProjectPanel.this, "Do you want to overwrite the file?", "File already exists", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);
						if (yesNoValue == JOptionPane.NO_OPTION)// return value for no button press: 1
							return;
					}
					// if code reaches here, user pressed yes button
					ConfigUtils.save(context.config, file, true);
				}
			}
		});
		
		popupMenu.add(saveMenuItem);
	}
	
	protected int index;
	protected JPanel tabNamePanel;
	protected JLabel tabNameLabel;
	protected JButton tabCloseButton;
	
	protected void createTabPanel() {
		tabNamePanel = new JPanel(new BorderLayout(5, 2));
		tabNamePanel.setOpaque(false);
		tabNamePanel.setFocusable(false);
		
		tabNameLabel = new JLabel(context.tabName);
		tabNameLabel.addMouseListener(this);
		
		tabCloseButton = new JButton(UISettings.CLOSE_BUTTON_ICON);
		tabCloseButton.setFocusable(false);
		tabCloseButton.addActionListener(this);
		tabCloseButton.setSize(UISettings.CLOSE_BUTTON_SIZE);
		tabCloseButton.setPreferredSize(UISettings.CLOSE_BUTTON_SIZE);
		
		tabNamePanel.setComponentPopupMenu(popupMenu);
		tabNamePanel.add(tabNameLabel, BorderLayout.WEST);
		tabNamePanel.add(tabCloseButton);
	}
	
	public void setupFinal() { // called from IDETabbedPane.openJar and openProj
		// ISSUE #1: https://github.com/TheBiblMan/CFIDE/issues/1
		IDETabbedPane tabbedPane = context.ideTabbedPane;
		index = tabbedPane.getTabCount() - 1;
		tabbedPane.setTabComponentAt(index, tabNamePanel);
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
	}
	
	@Override
	public void mousePressed(MouseEvent e) {
		context.ideTabbedPane.setSelectedComponent(ProjectPanel.this);
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
		context.ideTabbedPane.remove(ProjectPanel.this);
	}
}