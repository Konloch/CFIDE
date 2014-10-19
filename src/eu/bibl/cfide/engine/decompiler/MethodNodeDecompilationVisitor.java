package eu.bibl.cfide.engine.decompiler;

import java.util.ArrayList;
import java.util.List;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.MethodNode;

public class MethodNodeDecompilationVisitor implements DecompilationVisitor<MethodNode> {
	
	@Override
	public StringBuilder decompile(StringBuilder sb, MethodNode m) {
		String s = getAccessString(m.access);
		sb.append("     ");
		sb.append(s);
		if (s.length() > 0)
			sb.append(" ");
		sb.append(m.name);
		sb.append(" ");
		sb.append(m.desc);
		if (s.contains("abstract")) {
			sb.append(" {}\n");
		} else {
			sb.append(" {\n");
			for (String insn : new AdvancedInstructionPrinter(m).createPrint()) {
				sb.append("         ");
				sb.append(insn);
				sb.append("\n");
			}
			sb.append("     }\n");
		}
		return sb;
	}
	
	private static String getAccessString(int access) {
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