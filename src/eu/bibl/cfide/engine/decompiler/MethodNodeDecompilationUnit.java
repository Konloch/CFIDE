package eu.bibl.cfide.engine.decompiler;

import java.util.ArrayList;
import java.util.List;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TryCatchBlockNode;

import eu.bibl.cfide.config.CFIDEConfig;

public class MethodNodeDecompilationUnit implements DecompilationUnit<MethodNode> {
	
	protected CFIDEConfig config;
	
	public MethodNodeDecompilationUnit(CFIDEConfig config) {
		this.config = config;
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
			AdvancedInstructionPrinter insnPrinter = new AdvancedInstructionPrinter(config, m);
			for (String insn : insnPrinter.createPrint()) {
				sb.append("         ");
				sb.append(insn);
				sb.append("\n");
			}
			for (Object o : m.tryCatchBlocks) {
				TryCatchBlockNode tcbn = (TryCatchBlockNode) o;
				sb.append("         ");
				sb.append("TryCatch: L");
				sb.append(insnPrinter.resolveLabel(tcbn.start));
				sb.append(" L");
				sb.append(insnPrinter.resolveLabel(tcbn.end));
				sb.append(" L");
				sb.append(insnPrinter.resolveLabel(tcbn.handler));
				sb.append(" ");
				sb.append(tcbn.type);
				sb.append("\n");
			}
			sb.append("     } //end of ");
			sb.append(m.name);
			sb.append(" ");
			sb.append(m.desc);
			sb.append("\n");
		}
		return sb;
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