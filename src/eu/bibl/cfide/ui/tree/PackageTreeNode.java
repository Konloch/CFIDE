package eu.bibl.cfide.ui.tree;

import java.util.HashMap;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;

public class PackageTreeNode extends DefaultMutableTreeNode {
	
	private static final long serialVersionUID = 1668122196928246361L;
	
	private PackageTreeNode parentPackage;
	private String packageName;
	private HashMap<String, ClassTreeNode> classes;
	
	public PackageTreeNode(String packageName) {
		super(packageName);
		this.packageName = packageName;
		classes = new HashMap<String, ClassTreeNode>();
	}
	
	public void setParentPacket(PackageTreeNode parentPackage) {
		this.parentPackage = parentPackage;
	}
	
	public String getPackageName() {
		return packageName;
	}
	
	public PackageTreeNode getParentPackage() {
		return parentPackage;
	}
	
	public HashMap<String, ClassTreeNode> getClasses() {
		return classes;
	}
	
	@Override
	public void add(MutableTreeNode node) {
		super.add(node);
		
		if (node instanceof ClassTreeNode) {
			classes.put(((ClassTreeNode) node).getClassName(), (ClassTreeNode) node);
		} else if (node instanceof PackageTreeNode) {
			((PackageTreeNode) node).setParentPacket(this);
		}
	}
}