package eu.bibl.cfide.engine.decompiler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.SwingUtilities;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InnerClassNode;
import org.objectweb.asm.tree.MethodNode;

import eu.bibl.banalysis.asm.ClassNode;
import eu.bibl.banalysis.storage.classes.ClassContainer;
import eu.bibl.cfide.ui.editor.EditorTabbedPane;
import eu.bibl.cfide.ui.editor.EditorTextTab;
import eu.bibl.cfide.ui.tree.ClassTreeNode;

public class BytecodeDecompilationEngine {
	
	public static final Map<Integer, String> VERSION_TABLE = new HashMap<Integer, String>();
	
	static {
		VERSION_TABLE.put(Opcodes.V1_1, "V1_1");
		VERSION_TABLE.put(Opcodes.V1_2, "V1_2");
		VERSION_TABLE.put(Opcodes.V1_3, "V1_3");
		VERSION_TABLE.put(Opcodes.V1_4, "V1_4");
		VERSION_TABLE.put(Opcodes.V1_5, "V1_5");
		VERSION_TABLE.put(Opcodes.V1_6, "V1_6");
		VERSION_TABLE.put(Opcodes.V1_7, "V1_7");
		VERSION_TABLE.put(Opcodes.V1_8, "V1_8");
	}
	
	protected final EditorTabbedPane editor;
	protected ClassContainer container;
	private FieldNodeDecompilationVisitor fndv;
	private MethodNodeDecompilationVisitor mndv;
	
	public BytecodeDecompilationEngine(EditorTabbedPane editor, ClassContainer container, FieldNodeDecompilationVisitor fndv, MethodNodeDecompilationVisitor mndv) {
		this.editor = editor;
		this.container = container;
		this.fndv = fndv;
		this.mndv = mndv;
	}
	
	protected ResetViewRunnable RESET_VIEW_RUNNABLE = new ResetViewRunnable(); // shared scrollpane scroller, technically not thread safe.
	
	public void decompile(ClassNode cn) {
		String simpleName = ClassTreeNode.getClassName(cn.name);
		EditorTextTab textTab = editor.getTextTab(simpleName);
		if (textTab != null) {
			if (!textTab.isShowing()) {
				editor.addTab(simpleName, textTab);
				textTab.setupFinal();
			}
			editor.setSelectedComponent(textTab);
			return;
		}
		textTab = editor.createTextTab(simpleName);
		editor.setSelectedComponent(textTab);
		
		PrefixedStringBuilder sb = new PrefixedStringBuilder();
		sb = buildClassNodeRepresentation(sb, cn.name, cn);
		
		textTab.getTextArea().setText(sb.toString());
		RESET_VIEW_RUNNABLE.name = simpleName;
		SwingUtilities.invokeLater(RESET_VIEW_RUNNABLE);// needed to scroll to the top properly
	}
	
	protected PrefixedStringBuilder buildClassNodeRepresentation(PrefixedStringBuilder sb, String parent, ClassNode cn) {
		sb.append("using asm:ASM4\n");
		sb.append("using ver:");
		sb.append(VERSION_TABLE.get(cn.version));
		sb.append("\n");
		
		sb.append("\n");
		
		sb.append(getAccessString(cn.access));
		sb.append(" ");
		sb.append(cn.name);
		sb.append(" extends ");
		sb.append(cn.superName);
		
		int amountOfInterfaces = cn.interfaces.size();
		if (amountOfInterfaces > 0) {
			sb.append(" implements ");
			sb.append(cn.interfaces.get(0));
			if (amountOfInterfaces > 1) {
				// sb.append(",");
			}
			for (int i = 1; i < amountOfInterfaces; i++) {
				sb.append(", ");
				sb.append(cn.interfaces.get(i));
			}
		}
		sb.append(" {\n");
		for (FieldNode fn : cn.fields()) {
			sb.append("\n     ");
			fndv.decompile(sb, fn);
		}
		if (cn.fields.size() > 0) {
			sb.append("\n");
		}
		for (MethodNode mn : cn.methods()) {
			sb.append("\n");
			mndv.decompile(sb, mn);
		}
		
		int done = 0;
		for (Object o : cn.innerClasses) {
			InnerClassNode innerClassNode = (InnerClassNode) o;
			String innerClassName = innerClassNode.name;
			if ((innerClassName != null) && !innerClassName.equals(parent)) {
				ClassNode cn1 = container.getNodes().get(innerClassName);
				if (cn1 != null) {
					sb.appendPrefix("     ");
					sb.append("\n\n");
					sb = buildClassNodeRepresentation(sb, cn1.name, cn1);
					sb.trimPrefix(5);
					done++;
				} else {
					sb.append("NULL INNER CLASS: ");
					sb.append(innerClassName);
					sb.append("\n\n");
				}
			}
		}
		if (done > 0)// not logical but due to bad code, have to add an extra new line, just for aesthetics
			sb.append("\n");
		sb.append("}");
		// System.out.println("Wrote end for " + cn.name + " with prefix length: " + sb.prefix.length());
		return sb;
	}
	
	public static String getAccessString(int access) {
		List<String> tokens = new ArrayList<String>();
		if ((access & Opcodes.ACC_PUBLIC) != 0)
			tokens.add("public");
		if ((access & Opcodes.ACC_PRIVATE) != 0)
			tokens.add("private");
		if ((access & Opcodes.ACC_PROTECTED) != 0)
			tokens.add("protected");
		if ((access & Opcodes.ACC_FINAL) != 0)
			tokens.add("final");
		if ((access & Opcodes.ACC_SYNTHETIC) != 0)
			tokens.add("synthetic");
		// if ((access & Opcodes.ACC_SUPER) != 0)
		// tokens.add("super"); implied by invokespecial insn
		if ((access & Opcodes.ACC_ABSTRACT) != 0)
			tokens.add("abstract");
		if ((access & Opcodes.ACC_INTERFACE) != 0)
			tokens.add("interface");
		if ((access & Opcodes.ACC_ENUM) != 0)
			tokens.add("enum");
		if ((access & Opcodes.ACC_ANNOTATION) != 0)
			tokens.add("annotation");
		if (!tokens.contains("interface") && !tokens.contains("enum") && !tokens.contains("annotation"))
			tokens.add("class");
		if (tokens.size() == 0)
			return "[Error parsing]";
		
		// hackery delimeters
		StringBuilder sb = new StringBuilder(tokens.get(0));
		for (int i = 1; i < tokens.size(); i++) {
			sb.append(" ");
			sb.append(tokens.get(i));
		}
		return sb.toString();
	}
	
	protected class ResetViewRunnable implements Runnable {
		
		public String name;// needs to be set just before the runnable is used, bit dirty but yolo
		
		@Override
		public void run() {
			editor.getTextTab(name).getHorizontalScrollBar().setValue(0);
			editor.getTextTab(name).getVerticalScrollBar().setValue(0);
		}
	}
}