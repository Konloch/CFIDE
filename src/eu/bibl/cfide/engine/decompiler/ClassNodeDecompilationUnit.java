package eu.bibl.cfide.engine.decompiler;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InnerClassNode;
import org.objectweb.asm.tree.MethodNode;

import eu.bibl.banalysis.asm.ClassNode;
import eu.bibl.banalysis.storage.classes.ClassContainer;
import eu.bibl.cfide.config.CFIDEConfig;

public class ClassNodeDecompilationUnit implements DecompilationUnit<ClassNode> {
	
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
	
	protected CFIDEConfig config;
	protected ClassContainer container;
	private DecompilationUnit<FieldNode> fndu;
	private DecompilationUnit<MethodNode> mndu;
	
	public ClassNodeDecompilationUnit(CFIDEConfig config, ClassContainer container) {
		this.config = config;
		this.container = container;
		fndu = getFieldNodeDecompilationUnitImpl();
		mndu = getMethodNodeDecompilationUnitImpl();
	}
	
	@SuppressWarnings("unchecked")
	protected DecompilationUnit<FieldNode> getFieldNodeDecompilationUnitImpl() {
		DecompilationUnit<FieldNode> fnduImpl = null;
		String className = null;
		try {
			className = config.getProperty(CFIDEConfig.DECOMPILER_FIELD_DECOMPILATION_UNIT_CLASS_KEY, FieldNodeDecompilationUnit.class.getCanonicalName());
			Class<?> c = Class.forName(className);
			
			for (Constructor<?> constructor : c.getDeclaredConstructors()) {
				if (constructor.toString().endsWith("CFIDEConfig)")) { // because the DecompilerVisitor<FieldNode> constructor needs to take a config instance to init the builder and parse
					fnduImpl = (DecompilationUnit<FieldNode>) constructor.newInstance(config);
				}
			}
			if (fnduImpl == null) {
				fnduImpl = new FieldNodeDecompilationUnit(config);
			}
		} catch (Exception e) {
			System.out.println("Error loading custom DecompilationVisitor<FieldNode>: " + className);
			e.printStackTrace();
			config.putProperty(CFIDEConfig.DECOMPILER_FIELD_DECOMPILATION_UNIT_CLASS_KEY, FieldNodeDecompilationUnit.class.getCanonicalName());
			fnduImpl = new FieldNodeDecompilationUnit(config);
		}
		return fnduImpl;
	}
	
	@SuppressWarnings("unchecked")
	protected DecompilationUnit<MethodNode> getMethodNodeDecompilationUnitImpl() {
		DecompilationUnit<MethodNode> mnduImpl = null;
		String className = null;
		try {
			className = config.getProperty(CFIDEConfig.DECOMPILER_METHOD_DECOMPILATION_UNIT_CLASS_KEY, MethodNodeDecompilationUnit.class.getCanonicalName());
			Class<?> c = Class.forName(className);
			
			for (Constructor<?> constructor : c.getDeclaredConstructors()) {
				if (constructor.toString().endsWith("CFIDEConfig)")) { // because the DecompilerVisitor<MethodNode> constructor needs to take a config instance to init the builder and parse
					mnduImpl = (DecompilationUnit<MethodNode>) constructor.newInstance(config);
				}
			}
			if (mnduImpl == null) {
				mnduImpl = new MethodNodeDecompilationUnit(config);
			}
		} catch (Exception e) {
			System.out.println("Error loading custom DecompilationVisitor<MethodNode>: " + className);
			e.printStackTrace();
			config.putProperty(CFIDEConfig.DECOMPILER_METHOD_DECOMPILATION_UNIT_CLASS_KEY, MethodNodeDecompilationUnit.class.getCanonicalName());
			mnduImpl = new MethodNodeDecompilationUnit(config);
		}
		return mnduImpl;
	}
	
	@Override
	public PrefixedStringBuilder decompile(PrefixedStringBuilder sb, ClassNode cn) {
		System.out.println("Decompiling: " + cn.name);
		buildClassNodeRepresentation(sb, cn.name, cn);
		return sb;
	}
	
	protected PrefixedStringBuilder buildClassNodeRepresentation(PrefixedStringBuilder sb, String parent, ClassNode cn) {
		// try {
		// Thread.sleep(100);
		// } catch (InterruptedException e) {
		// }
		sb.append("using asm ASM4\n");
		sb.append("using ver ");
		sb.append(VERSION_TABLE.get(cn.version));
		sb.append("\n\n");
		
		sb.append("class: ");
		sb.append(getAccessString(cn.access));
		sb.append(" ");
		sb.append(cn.name);
		sb.append(" extends ");
		sb.append(cn.superName);
		
		int amountOfInterfaces = cn.interfaces.size();
		if (amountOfInterfaces > 0) {
			sb.append(" implements ");
			sb.append(cn.interfaces.get(0));
			for (int i = 1; i < amountOfInterfaces; i++) {
				sb.append(", ");
				sb.append(cn.interfaces.get(i));
			}
		}
		sb.append(" {\n");
		if (cn.fields.size() > 0) {
			for (FieldNode fn : cn.fields()) {
				sb.append("\n     ");
				fndu.decompile(sb, fn);
			}
			sb.append("\n");
		}
		
		for (MethodNode mn : cn.methods()) {
			sb.append("\n");
			mndu.decompile(sb, mn);
		}
		
		int done = 0;
		for (Object o : cn.innerClasses) {
			InnerClassNode innerClassNode = (InnerClassNode) o;
			String innerClassName = innerClassNode.name;
			String outerClassName = innerClassNode.outerName;
			if ((innerClassName != null)) {
				ClassNode cn1 = container.getNodes().get(innerClassName);
				if (cn1 != null) {
					if (((parent == null) && (outerClassName != null)) || ((outerClassName == null) && (parent != null))) {
						System.out.println("----------------------------------------------------");
						System.out.println("Class: " + cn.name + " parent: " + parent);
						System.out.println("Inner: " + innerClassName + " parent: " + outerClassName);
						sb.appendPrefix("     ");
						sb.append("\n\n");
						sb = buildClassNodeRepresentation(sb, outerClassName, cn1);
						sb.trimPrefix(5);
						done++;
					}
				} else {
					sb.appendPrefix("     ");
					sb.append("\n");
					sb.append("NULL_INNER_CLASS: ");
					sb.append(innerClassName);
					sb.append("\n\n");
					sb.trimPrefix(5);
				}
			}
		}
		if (done > 0)// not logical but due to bad code, have to add an extra new line, just for aesthetics
			sb.append("\n");
		sb.append("} //end of ");
		sb.append(cn.name);
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
}