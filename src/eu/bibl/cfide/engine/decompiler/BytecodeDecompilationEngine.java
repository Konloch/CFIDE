package eu.bibl.cfide.engine.decompiler;

import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingUtilities;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

import eu.bibl.banalysis.asm.ClassNode;
import eu.bibl.cfide.ui.editor.EditorTabbedPane;
import eu.bibl.cfide.ui.editor.EditorTextTab;
import eu.bibl.cfide.ui.tree.ClassTreeNode;

public class BytecodeDecompilationEngine {
	
	protected final EditorTabbedPane editor;
	private FieldNodeDecompilationVisitor fndv;
	private MethodNodeDecompilationVisitor mndv;
	
	public BytecodeDecompilationEngine(EditorTabbedPane editor, FieldNodeDecompilationVisitor fndv, MethodNodeDecompilationVisitor mndv) {
		this.editor = editor;
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
		
		StringBuilder sb = new StringBuilder();
		sb.append(getAccessString(cn.access));
		sb.append(" ");
		sb.append(cn.name);
		
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
		sb.append("}");
		textTab.getTextArea().setText(sb.toString());
		RESET_VIEW_RUNNABLE.name = simpleName;
		SwingUtilities.invokeLater(RESET_VIEW_RUNNABLE);// needed to scroll to the top properly
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
		
		public String name;
		
		@Override
		public void run() {
			editor.getTextTab(name).getHorizontalScrollBar().setValue(0);
			editor.getTextTab(name).getVerticalScrollBar().setValue(0);
		}
	}
}