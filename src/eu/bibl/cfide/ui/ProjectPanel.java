package eu.bibl.cfide.ui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTree;

import eu.bibl.bio.JarInfo;
import eu.bibl.bio.jfile.in.JarDownloader;
import eu.bibl.cfide.engine.decompiler.BytecodeDecompilationEngine;
import eu.bibl.cfide.engine.decompiler.FieldNodeDecompilationVisitor;
import eu.bibl.cfide.engine.decompiler.MethodNodeDecompilationVisitor;
import eu.bibl.cfide.project.WorkspaceProject;
import eu.bibl.cfide.ui.editor.EditorTabbedPane;
import eu.bibl.cfide.ui.tree.ClassViewerTree;

public class ProjectPanel extends JPanel implements MouseListener, ActionListener {
	
	private static final long serialVersionUID = 7392644318314592144L;
	
	protected JTabbedPane tabbedPane;
	protected String tabName;
	protected WorkspaceProject project;
	protected JSplitPane splitPane;
	protected JScrollPane scrollPane;
	protected JTree tree;
	protected EditorTabbedPane etp;
	
	public ProjectPanel(JTabbedPane tabbedPane, String tabName, WorkspaceProject project) {
		super(new BorderLayout());
		this.tabbedPane = tabbedPane;
		this.tabName = tabName;
		this.project = project;
		init();
	}
	
	private void init() {
		splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		File jarFile = new File(project.getJarLocation());
		JarDownloader dl = new JarDownloader(new JarInfo(jarFile));
		dl.parse();
		
		etp = new EditorTabbedPane();
		tree = new ClassViewerTree(jarFile.getName(), dl.getContents(), new BytecodeDecompilationEngine(etp, new FieldNodeDecompilationVisitor(), new MethodNodeDecompilationVisitor()));
		splitPane.setResizeWeight(0.115D);
		scrollPane = new JScrollPane(tree);
		splitPane.add(scrollPane);
		splitPane.add(etp);
		add(splitPane);
		createTabPanel();
	}
	
	protected int index;
	protected JPanel tabNamePanel;
	protected JLabel tabNameLabel;
	protected JButton tabCloseButton;
	
	protected void createTabPanel() {
		tabNamePanel = new JPanel(new BorderLayout(5, 2));
		tabNamePanel.setOpaque(false);
		tabNamePanel.setFocusable(false);
		
		tabNameLabel = new JLabel(tabName);
		tabNameLabel.addMouseListener(this);
		
		tabCloseButton = new JButton(UISettings.CLOSE_BUTTON_ICON);
		tabCloseButton.setFocusable(false);
		tabCloseButton.addActionListener(this);
		tabCloseButton.setSize(UISettings.CLOSE_BUTTON_SIZE);
		tabCloseButton.setPreferredSize(UISettings.CLOSE_BUTTON_SIZE);
		
		tabNamePanel.add(tabNameLabel, BorderLayout.WEST);
		tabNamePanel.add(tabCloseButton);
	}
	
	public void setupFinal() {
		index = tabbedPane.indexOfTab(tabName);
		tabbedPane.setTabComponentAt(index, tabNamePanel);
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
	}
	
	@Override
	public void mousePressed(MouseEvent e) {
		tabbedPane.setSelectedComponent(ProjectPanel.this);
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
		tabbedPane.remove(ProjectPanel.this);
	}
}