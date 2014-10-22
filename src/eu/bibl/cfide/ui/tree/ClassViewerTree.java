package eu.bibl.cfide.ui.tree;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreePath;

import org.objectweb.asm.ClassWriter;

import eu.bibl.banalysis.asm.ClassNode;
import eu.bibl.banalysis.storage.classes.ClassContainer;
import eu.bibl.cfide.engine.decompiler.BytecodeDecompilationEngine;
import eu.bibl.cfide.engine.parser.BasicParser;
import eu.bibl.cfide.engine.parser.ParserException;
import eu.bibl.cfide.ui.ProjectPanel;

public class ClassViewerTree extends JTree implements TreeSelectionListener, MouseListener {
	
	private static final long serialVersionUID = -1731401270496103799L;
	private static final Icon JAR_ICON = new ImageIcon("res/jar.png");
	
	protected PackageTreeNode root;
	protected ClassContainer contents;
	protected ProjectPanel projectPanel;
	protected BytecodeDecompilationEngine engine;
	protected BasicParser<ClassNode> outParser;
	
	public ClassViewerTree(String jarName, ClassContainer contents, ProjectPanel projectPanel, BytecodeDecompilationEngine engine, BasicParser<ClassNode> outParser) {
		super(new DefaultPackageTreeNode(jarName));
		this.contents = contents;
		this.projectPanel = projectPanel;
		this.engine = engine;
		this.outParser = outParser;
		setRootVisible(true);
		populateTree();
		setComponentPopupMenu(createPopupMenu());
		setCellRenderer(new DefaultPackageTreeNodeRenderer());
		addTreeSelectionListener(this);
		addMouseListener(this);
		expandPath(new TreePath(root)); // automatically opens the root node.
	}
	
	protected JPopupMenu createPopupMenu() {
		JPopupMenu menu = new JPopupMenu() {
			private static final long serialVersionUID = 7505457795696357320L;
			
			@Override
			public void setVisible(boolean b) {
				if (b) {
					if (selectedNode instanceof ClassTreeNode) {
						super.setVisible(true);// only show if a node is selected
						TreePath path = new TreePath(selectedNode);
						ClassViewerTree.this.expandPath(path);// want to highlight the jtree leaf but that wont work,
						ClassViewerTree.this.setSelectionPath(path);// instead just to fire the open event to make sure the class is decompiled.
						
					}
				} else {
					super.setVisible(false);
				}
			}
		};
		JMenuItem item = new JMenuItem("Save Class");
		item.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (selectedNode instanceof ClassTreeNode) {
					ClassTreeNode ctn = (ClassTreeNode) selectedNode;
					try {
						ClassNode cn = outParser.parse(projectPanel.getText(ctn.getClassName()));
						ClassWriter cw = new ClassWriter(0);
						cn.accept(cw);
						try {
							DataOutputStream dos = new DataOutputStream(new FileOutputStream(new File("C:/Users/Bibl/Desktop/test.class")));
							dos.write(cw.toByteArray());
							dos.close();
						} catch (IOException e1) {
							e1.printStackTrace();
						}
					} catch (ParserException e1) {
						JOptionPane.showMessageDialog(ClassViewerTree.this, e1.getMessage(), "Parser error", JOptionPane.ERROR_MESSAGE);
						e1.printStackTrace();
					}
				}
			}
		});
		menu.add(item);
		return menu;
	}
	
	protected Map<String, PackageTreeNode> packages;
	
	private void populateTree() {
		packages = new HashMap<String, PackageTreeNode>();
		root = (PackageTreeNode) getModel().getRoot();
		Map<ClassTreeNode, PackageTreeNode> classesToAdd = new HashMap<ClassTreeNode, PackageTreeNode>();
		Map<PackageTreeNode, PackageTreeNode> packagesToAdd = new HashMap<PackageTreeNode, PackageTreeNode>();
		
		for (ClassNode cn : contents.getNodes().values()) {
			if (cn.name.contains("$"))
				continue;// don't list inner classes, have them decompiled in the class they came from
			String[] nameParts = cn.name.split("/");
			PackageTreeNode lastNode = root;
			StringBuilder packageDepthName = new StringBuilder();
			for (int i = 0; i < (nameParts.length - 1); i++) { // loop through just package names
				String packageName = nameParts[i];
				packageDepthName.append(packageName);
				packageDepthName.append("/");
				if (packages.containsKey(packageDepthName.toString())) {// if the package is already mapped, just set as last visited.
					lastNode = packages.get(packageDepthName.toString());
				} else {// if it's not mapped, create it, save it and set it as last visited
					PackageTreeNode packageTreeNode = new PackageTreeNode(packageName);
					packages.put(packageDepthName.toString(), packageTreeNode);
					packagesToAdd.put(packageTreeNode, lastNode); // add after to ensure alphabetical name ordering
					lastNode = packageTreeNode;
				}
			}
			ClassTreeNode classTreeNode = new ClassTreeNode(cn); // add class after for alphabetical name ordering
			classesToAdd.put(classTreeNode, lastNode);
		}
		
		// order the packages alphabetically
		List<PackageTreeNode> packageKeys = new ArrayList<PackageTreeNode>(packagesToAdd.keySet());
		Collections.sort(packageKeys, new Comparator<PackageTreeNode>() {
			@Override
			public int compare(PackageTreeNode o1, PackageTreeNode o2) {
				return o1.getPackageName().compareTo(o2.getPackageName());
			}
		});
		
		for (PackageTreeNode ptn : packageKeys) {
			PackageTreeNode ptn1 = packagesToAdd.get(ptn);
			ptn1.add(ptn);
		}
		
		// order the classes alphabetically
		List<ClassTreeNode> classKeys = new ArrayList<ClassTreeNode>(classesToAdd.keySet());
		Collections.sort(classKeys, new Comparator<ClassTreeNode>() {
			@Override
			public int compare(ClassTreeNode o1, ClassTreeNode o2) {
				return o1.getClassName().compareTo(o2.getClassName());
			}
		});
		
		for (ClassTreeNode ctn : classKeys) { // add classes after so we have that package list, then class list effect
			PackageTreeNode ptn = classesToAdd.get(ctn);
			ptn.add(ctn);
		}
	}
	
	@Override
	public void valueChanged(TreeSelectionEvent e) {
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) getLastSelectedPathComponent();
		if (node == null)
			return;
		
		if (node instanceof ClassTreeNode) {
			engine.decompile(((ClassTreeNode) node).getClassNode());
		}
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
	}
	
	protected DefaultMutableTreeNode selectedNode;
	
	@Override
	public void mousePressed(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON3) {
			TreePath pathForLocation = getPathForLocation(e.getPoint().x, e.getPoint().y);
			if (pathForLocation != null) {
				selectedNode = (DefaultMutableTreeNode) pathForLocation.getLastPathComponent();
			} else {
				selectedNode = null;
			}
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
	
	class DefaultPackageTreeNodeRenderer extends DefaultTreeCellRenderer {
		
		private static final long serialVersionUID = -7238675790138337723L;
		
		@Override
		public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
			super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
			if (value.equals(root)) // make sure the root node has the jar icon
				setIcon(JAR_ICON);
			return this;
		}
	}
}