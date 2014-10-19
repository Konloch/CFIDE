package eu.bibl.cfide.ui.tree;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;

import eu.bibl.banalysis.asm.ClassNode;

public class ClassTreeNode extends DefaultMutableTreeNode {
	
	private static final long serialVersionUID = -1408052678662427532L;
	
	private ClassNode cn;
	
	public ClassTreeNode(ClassNode cn) {
		super(getClassName(cn.name));
		this.cn = cn;
	}
	
	public ClassNode getClassNode() {
		return cn;
	}
	
	public String getClassName() {
		return getClassName(cn.name);
	}
	
	public static String getClassName(String name) {
		if (!name.contains("/"))
			return name;
		String[] parts = name.split("/");
		return parts[parts.length - 1];
	}
	
	@Override
	public void add(MutableTreeNode treeNode) {
	}
}