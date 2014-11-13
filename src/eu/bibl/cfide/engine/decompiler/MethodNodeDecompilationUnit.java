package eu.bibl.cfide.engine.decompiler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TryCatchBlockNode;

import eu.bibl.cfide.context.CFIDEContext;

public class MethodNodeDecompilationUnit implements DecompilationUnit<MethodNode> {
	
	protected CFIDEContext context;
	
	public MethodNodeDecompilationUnit(CFIDEContext context) {
		this.context = context;
	}
	
	@Override
	public PrefixedStringBuilder decompile(PrefixedStringBuilder sb, MethodNode m) {
		String s = getAccessString(m.access);
		sb.append("     ");
		sb.append("method: ");
		sb.append(s);
		if (s.length() > 0)
			sb.append(" ");
		sb.append(m.name);
		sb.append(" ");
		sb.append(m.desc);
		
		int amountOfThrows = m.exceptions.size();
		if (amountOfThrows > 0) {
			sb.append(" throws ");
			sb.append(m.exceptions.get(0));// exceptions is list<string>
			for (int i = 1; i < amountOfThrows; i++) {
				sb.append(", ");
				sb.append(m.exceptions.get(i));
			}
		}
		if (s.contains("abstract")) {
			sb.append(" {}\n");
		} else {
			sb.append(" {\n");
			
			if (m.annotationDefault != null) {
				sb.append(m.annotationDefault);
				sb.append("\n");
			}
			
			if (m.signature != null) {
				sb.append("         <sig:").append(m.signature).append(">\n");
			}
			
			AdvancedInstructionPrinter insnPrinter = new AdvancedInstructionPrinter(context, m);
			List<String> print = insnPrinter.createPrint();
			
			addAttrList(m.attrs, "attr", sb, insnPrinter);
			addAttrList(m.invisibleAnnotations, "invisAnno", sb, insnPrinter);
			addAttrList(m.invisibleAnnotations, "invisLocalVarAnno", sb, insnPrinter);
			addAttrList(m.invisibleTypeAnnotations, "invisTypeAnno", sb, insnPrinter);
			addAttrList(m.localVariables, "localVar", sb, insnPrinter);
			addAttrList(m.visibleAnnotations, "visAnno", sb, insnPrinter);
			addAttrList(m.visibleLocalVariableAnnotations, "visLocalVarAnno", sb, insnPrinter);
			addAttrList(m.visibleTypeAnnotations, "visTypeAnno", sb, insnPrinter);
			
			for (String insn : print) {
				sb.append("         ");
				sb.append(insn);
				sb.append("\n");
			}
			for (Object o : m.tryCatchBlocks) {
				TryCatchBlockNode tcbn = (TryCatchBlockNode) o;
				sb.append("         ");
				sb.append("<TryCatch: L");
				sb.append(insnPrinter.resolveLabel(tcbn.start));
				sb.append(" L");
				sb.append(insnPrinter.resolveLabel(tcbn.end));
				sb.append(" L");
				sb.append(insnPrinter.resolveLabel(tcbn.handler));
				sb.append(" ");
				sb.append(tcbn.type);
				sb.append(">\n");
			}
			
			sb.append("     } //end of ");
			sb.append(m.name);
			sb.append(" ");
			sb.append(m.desc);
			sb.append("\n");
		}
		return sb;
	}
	
	private void addAttrList(List<?> list, String name, PrefixedStringBuilder sb, AdvancedInstructionPrinter insnPrinter) {
		if (list == null)
			return;
		if (list.size() > 0) {
			for (Object o : list) {
				sb.append("         <");
				sb.append(name);
				sb.append(":");
				sb.append(printAttr(o, insnPrinter));
				sb.append(">");
				sb.append("\n");
			}
			sb.append("\n");
		}
	}
	
	private String printAttr(Object o, AdvancedInstructionPrinter insnPrinter) {
		if (o instanceof LocalVariableNode) {
			LocalVariableNode lvn = (LocalVariableNode) o;
			return "index=" + lvn.index + " , name=" + lvn.name + " , desc=" + lvn.desc + ", sig=" + lvn.signature + ", start=L" + insnPrinter.resolveLabel(lvn.start) + ", end=L" + insnPrinter.resolveLabel(lvn.end);
		} else if (o instanceof AnnotationNode) {
			AnnotationNode an = (AnnotationNode) o;
			StringBuilder sb = new StringBuilder();
			sb.append("desc = ");
			sb.append(an.desc);
			sb.append(" , values = ");
			if (an.values != null) {
				sb.append(Arrays.toString(an.values.toArray()));
			} else {
				sb.append("[]");
			}
			return sb.toString();
		}
		if (o == null)
			return "";
		return o.toString();
	}
	
	public static String getAccessString(int access) {
		// public, protected, private, abstract, static,
		// final, synchronized, native & strictfp are permitted
		List<String> tokens = new ArrayList<String>();
		if ((access & Opcodes.ACC_PUBLIC) != 0)
			tokens.add("public");
		if ((access & Opcodes.ACC_PRIVATE) != 0)
			tokens.add("private");
		if ((access & Opcodes.ACC_PROTECTED) != 0)
			tokens.add("protected");
		if ((access & Opcodes.ACC_STATIC) != 0)
			tokens.add("static");
		if ((access & Opcodes.ACC_ABSTRACT) != 0)
			tokens.add("abstract");
		if ((access & Opcodes.ACC_FINAL) != 0)
			tokens.add("final");
		if ((access & Opcodes.ACC_SYNCHRONIZED) != 0)
			tokens.add("synchronized");
		if ((access & Opcodes.ACC_NATIVE) != 0)
			tokens.add("native");
		if ((access & Opcodes.ACC_STRICT) != 0)
			tokens.add("strictfp");
		if ((access & Opcodes.ACC_BRIDGE) != 0)
			tokens.add("bridge");
		if ((access & Opcodes.ACC_VARARGS) != 0)
			tokens.add("varargs");
		if (tokens.size() == 0)
			return "";
		// hackery delimeters
		StringBuilder sb = new StringBuilder(tokens.get(0));
		for (int i = 1; i < tokens.size(); i++) {
			sb.append(" ");
			sb.append(tokens.get(i));
		}
		return sb.toString();
	}
}