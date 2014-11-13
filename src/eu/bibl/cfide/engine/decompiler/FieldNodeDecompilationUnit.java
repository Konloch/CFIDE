package eu.bibl.cfide.engine.decompiler;

import java.util.ArrayList;
import java.util.List;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.FieldNode;

import eu.bibl.cfide.context.CFIDEContext;

public class FieldNodeDecompilationUnit implements DecompilationUnit<FieldNode> {
	
	protected CFIDEContext context;
	
	public FieldNodeDecompilationUnit(CFIDEContext context) {
		this.context = context;
	}
	
	@Override
	public PrefixedStringBuilder decompile(PrefixedStringBuilder sb, FieldNode f) {
		addAttrList(f.attrs, "attr", sb);
		addAttrList(f.invisibleAnnotations, "invisAnno", sb);
		addAttrList(f.invisibleTypeAnnotations, "invisTypeAnno", sb);
		addAttrList(f.visibleAnnotations, "visAnno", sb);
		addAttrList(f.visibleTypeAnnotations, "visTypeAnno", sb);
		if (f.signature != null)
			sb.append("     <sig:").append(f.signature).append(">\n");
		
		sb.append("     field: ");
		String s = getAccessString(f.access);
		sb.append(s);
		if (s.length() > 0)
			sb.append(" ");
		sb.append(f.desc);
		sb.append(" ");
		sb.append(f.name);
		if (f.value != null) {
			sb.append(" = ");
			if (f.value instanceof String) {
				sb.append("\"");
				sb.append(f.value);
				sb.append("\"");
				sb.append(" (");
				sb.append(f.value.getClass().getCanonicalName());
				sb.append(")");
			} else {
				sb.append(f.value);
				sb.append(" (");
				sb.append(f.value.getClass().getCanonicalName());
				sb.append(")");
			}
		}
		sb.append(" :end");
		return sb;
	}
	
	private void addAttrList(List<?> list, String name, PrefixedStringBuilder sb) {
		if (list == null)
			return;
		if (list.size() > 0) {
			for (Object o : list) {
				sb.append("     <");
				sb.append(name);
				sb.append(":");
				sb.append(printAttr(o));
				sb.append(">");
				sb.append("\n");
			}
			sb.append("\n");
		}
	}
	
	private String printAttr(Object o) {
		if (o == null)
			return "";
		return o.toString();
	}
	
	public static String getAccessString(int access) {
		List<String> tokens = new ArrayList<String>();
		if ((access & Opcodes.ACC_PUBLIC) != 0)
			tokens.add("public");
		if ((access & Opcodes.ACC_PRIVATE) != 0)
			tokens.add("private");
		if ((access & Opcodes.ACC_PROTECTED) != 0)
			tokens.add("protected");
		if ((access & Opcodes.ACC_SYNTHETIC) != 0)
			tokens.add("synthetic");
		if ((access & Opcodes.ACC_STATIC) != 0)
			tokens.add("static");
		if ((access & Opcodes.ACC_FINAL) != 0)
			tokens.add("final");
		if ((access & Opcodes.ACC_TRANSIENT) != 0)
			tokens.add("transient");
		if ((access & Opcodes.ACC_VOLATILE) != 0)
			tokens.add("volatile");
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