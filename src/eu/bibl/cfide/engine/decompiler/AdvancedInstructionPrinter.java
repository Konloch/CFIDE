package eu.bibl.cfide.engine.decompiler;

import java.util.List;
import java.util.ListIterator;

import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.LookupSwitchInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TableSwitchInsnNode;

import eu.bibl.banalysis.asm.insn.InstructionPattern;
import eu.bibl.banalysis.asm.insn.InstructionPrinter;

public class AdvancedInstructionPrinter extends InstructionPrinter {
	
	public AdvancedInstructionPrinter(MethodNode m) {
		super(m);
	}
	
	public AdvancedInstructionPrinter(MethodNode m, InstructionPattern p) {
		super(m, p);
	}
	
	@Override
	protected String printLdcInsnNode(LdcInsnNode ldc, ListIterator<?> it) {
		if (ldc.cst instanceof String)
			return nameOpcode(ldc.getOpcode()) + " \"" + escapeJavaStyleString(ldc.cst.toString(), true) + "\" (" + ldc.cst.getClass().getCanonicalName() + ")";
		
		return nameOpcode(ldc.getOpcode()) + " " + escapeJavaStyleString(ldc.cst.toString(), true) + " (" + ldc.cst.getClass().getCanonicalName() + ")";
	}
	
	@Override
	protected String printTableSwitchInsnNode(TableSwitchInsnNode tin) {
		String line = nameOpcode(tin.getOpcode()) + " \n";
		List<?> labels = tin.labels;
		int count = 0;
		for (int i = tin.min; i < tin.max; i++) {
			line += "                val: " + i + " -> " + "L" + resolveLabel((LabelNode) labels.get(count++)) + "\n";
		}
		line += "                default" + " -> L" + resolveLabel(tin.dflt) + "";
		return line;
	}
	
	@Override
	protected String printLookupSwitchInsnNode(LookupSwitchInsnNode lin) {
		String line = nameOpcode(lin.getOpcode()) + ": \n";
		List<?> keys = lin.keys;
		List<?> labels = lin.labels;
		
		for (int i = 0; i < keys.size(); i++) {
			int key = (Integer) keys.get(i);
			LabelNode label = (LabelNode) labels.get(i);
			line += "                val: " + key + " -> " + "L" + resolveLabel(label) + "\n";
		}
		line += "                default" + " -> L" + resolveLabel(lin.dflt) + "";
		return line;
	}
	
	@Override
	protected String printFieldInsnNode(FieldInsnNode fin, ListIterator<?> it) {
		return nameOpcode(fin.getOpcode()) + " " + fin.owner + "." + fin.name + ":" + fin.desc;
	}
	
	@Override
	protected String printMethodInsnNode(MethodInsnNode min, ListIterator<?> it) {
		return nameOpcode(min.getOpcode()) + " " + min.owner + "." + min.name + ":" + min.desc;
	}
	
	public static String escapeJavaStyleString(String str, boolean escapeSingleQuote) {
		if (str == null) {
			return "";
		}
		int sz = str.length();
		StringBuilder sb = new StringBuilder(sz);
		for (int i = 0; i < sz; i++) {
			char ch = str.charAt(i);
			
			// handle unicode
			if (ch > 0xfff) {
				sb.append("\\u" + hex(ch));
			} else if (ch > 0xff) {
				sb.append("\\u0" + hex(ch));
			} else if (ch > 0x7f) {
				sb.append("\\u00" + hex(ch));
			} else if (ch < 32) {
				switch (ch) {
					case '\b':
						sb.append('\\');
						sb.append('b');
						break;
					case '\n':
						sb.append('\\');
						sb.append('n');
						break;
					case '\t':
						sb.append('\\');
						sb.append('t');
						break;
					case '\f':
						sb.append('\\');
						sb.append('f');
						break;
					case '\r':
						sb.append('\\');
						sb.append('r');
						break;
					default:
						if (ch > 0xf) {
							sb.append("\\u00" + hex(ch));
						} else {
							sb.append("\\u000" + hex(ch));
						}
						break;
				}
			} else {
				switch (ch) {
					case '\'':
						if (escapeSingleQuote) {
							sb.append('\\');
						}
						sb.append('\'');
						break;
					case '"':
						sb.append('\\');
						sb.append('"');
						break;
					case '\\':
						sb.append('\\');
						sb.append('\\');
						break;
					default:
						sb.append(ch);
						break;
				}
			}
		}
		return sb.toString();
	}
	
	public static String hex(char ch) {
		return Integer.toHexString(ch).toUpperCase();
	}
}