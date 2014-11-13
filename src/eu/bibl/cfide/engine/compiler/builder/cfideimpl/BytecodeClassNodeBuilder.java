package eu.bibl.cfide.engine.compiler.builder.cfideimpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.LineNumberNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TryCatchBlockNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

import eu.bibl.banalysis.asm.ClassNode;
import eu.bibl.banalysis.asm.desc.OpcodeInfo;
import eu.bibl.cfide.context.CFIDEContext;
import eu.bibl.cfide.engine.compiler.builder.BuilderException;
import eu.bibl.cfide.engine.compiler.builder.IBuilder;
import eu.bibl.cfide.engine.compiler.parser.ParserToken;
import eu.bibl.cfide.engine.compiler.parser.cfideimpl.tokens.member.ClassMemberToken;
import eu.bibl.cfide.engine.compiler.parser.cfideimpl.tokens.member.FieldMemberToken;
import eu.bibl.cfide.engine.compiler.parser.cfideimpl.tokens.member.MemberCloseToken;
import eu.bibl.cfide.engine.compiler.parser.cfideimpl.tokens.member.MethodMemberToken;
import eu.bibl.cfide.engine.compiler.parser.cfideimpl.tokens.using.UsingASMToken;
import eu.bibl.cfide.engine.compiler.parser.cfideimpl.tokens.using.UsingVerToken;
import eu.bibl.cfide.engine.util.StringArrayReader;

public class BytecodeClassNodeBuilder implements IBuilder<ClassNode[], List<ParserToken>>, Opcodes {
	
	public static final Map<Integer, String> VERSION_TABLE = new HashMap<Integer, String>();
	
	protected CFIDEContext context;
	
	public BytecodeClassNodeBuilder(CFIDEContext context) {
		this.context = context;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public ClassNode[] build(List<ParserToken> tokens) throws BuilderException {
		List<ClassNode> cns = new ArrayList<ClassNode>();
		Stack<Object> stack = new Stack<Object>();
		for (int i = 0; i < tokens.size(); i++) {
			ParserToken token = tokens.get(i);
			if (token instanceof ClassMemberToken) {
				ClassMemberToken cmt = (ClassMemberToken) token;
				UsingASMToken tok1 = expect(tokens, i - 2, "Using ASM Declaration");
				UsingVerToken tok2 = expect(tokens, i - 1, "Using Ver Declaration");
				ClassNode cn;
				try {
					try {
						Object o = stack.peek();
						if (o instanceof ClassNode) {
						} else if (o != null) {
							throw new BuilderException("Invalid class declaration, cannot be inside a method.");
						}
					} catch (Exception e) {
					}
					cn = new ClassNode(tok1.getASMValue());
					cn.version = tok2.getClassFileVersion();
					cn.access = cmt.getAccess();
					cn.name = cmt.getName();
					cn.superName = cmt.getSuperName();
					for (String s : cmt.getInterfaces()) {
						cn.interfaces.add(s);
					}
					stack.push(cn);
					cns.add(cn);
				} catch (Exception e) {
					throw new BuilderException(e);
				}
				stack.push(cn);
			} else if (token instanceof FieldMemberToken) {
				try {
					FieldMemberToken fmt = (FieldMemberToken) token;
					FieldNode fn = null;
					if (fmt.getValue() == null)
						fn = new FieldNode(fmt.getAccess(), fmt.getName(), fmt.getDesc(), null, null);
					else
						fn = new FieldNode(fmt.getAccess(), fmt.getName(), fmt.getDesc(), null, getValue(fmt.getValue(), fmt.getValueType()));
					ClassNode cn = (ClassNode) stack.peek();
					cn.fields.add(fn);
				} catch (Exception e) {
					throw new BuilderException(e);
				}
			} else if (token instanceof MethodMemberToken) {
				Object o = stack.peek();
				if ((o == null) || !(o instanceof ClassNode)) {
					throw new BuilderException("Internal error: Stack.peek() returned: " + o);
				}
				ClassNode cn = (ClassNode) o;
				MethodMemberToken mmt = (MethodMemberToken) token;
				MethodNode mn = new MethodNode(mmt.getAccess(), mmt.getName(), mmt.getDesc(), null, mmt.getExceptionThrows());
				mn.instructions.add(parseCode(new StringArrayReader(mmt.getCode()), mn));
				// System.out.println(mn.instructions.size() + " code");
				// AdvancedInstructionPrinter.consolePrint(new AdvancedInstructionPrinter(mn).getLines());
				cn.methods.add(mn);
				stack.push(mn);
			} else if (token instanceof MemberCloseToken) {
				stack.pop();
			}
		}
		return cns.toArray(new ClassNode[cns.size()]);
	}
	
	protected LabelHandler labelHandler = new LabelHandler();
	
	public InsnList parseCode(StringArrayReader cr, MethodNode m) throws BuilderException {
		labelHandler.reset();
		InsnList list = new InsnList();
		
		// cache labels first
		for (int i = 0; i < cr.size(); i++) {
			String s = cr.read();
			if (s.endsWith(":")) {// its a label
				labelHandler.resolveLabel(s);
			}
		}
		
		// int highestVar = 0;
		cr.reset();
		while (cr.canReadNext()) {
			String s = cr.read();
			int val = isOpcode(s);
			if (val != -1) {
				AbstractInsnNode ain = resolveInstruction(val, cr);
				if (ain != null) {
					// if (ain instanceof VarInsnNode) {
					// VarInsnNode vin = (VarInsnNode) ain;
					// int v = vin.var;
					// if (v > highestVar)
					// highestVar = v;
					// }
					list.add(ain);
				}
			} else if (s.endsWith(":")) {// its a label
				LabelNode ln = labelHandler.retreiveLabel(s);
				list.add(ln);
			} else /*
				    * if (s.endsWith(":") && s.toUpperCase().equals("TRYCATCH:")) {
				    * LabelNode start = labelHandler.retreiveLabel(expectCode(cr, "trycatchblocknode start label."));
				    * LabelNode end = labelHandler.retreiveLabel(expectCode(cr, "trycatchblocknode start label."));
				    * LabelNode handler = labelHandler.retreiveLabel(expectCode(cr, "trycatchblocknode start label."));
				    * String exc = expectCode(cr, "exception type.");
				    * TryCatchBlockNode tcbn = new TryCatchBlockNode(start, end, handler, exc);
				    * m.tryCatchBlocks.add(tcbn);
				    * } else
				    */
			if (s.startsWith("<") && s.endsWith(">")) {
					AbstractInsnNode ain = parseCodeMetadata(m, s);
					if (ain != null)
						list.add(ain);
			}
		}
		// m.maxLocals = highestVar;
		
		return list;
	}
	
	@SuppressWarnings("unchecked")
	protected AbstractInsnNode parseCodeMetadata(MethodNode m, String meta) throws BuilderException {
		meta = meta.substring(1);// get rid of <
		meta = meta.substring(0, meta.length() - 1);// get rid of >
		String[] split = meta.split(":", 2);
		if (split.length != 2)
			throw new BuilderException("Invalid meta data: " + meta + ".");
		String key = split[0].toUpperCase();
		switch (key) {
			case "LINE": {
				try {
					int line = Integer.parseInt(split[1]);
					return new LineNumberNode(line, labelHandler.getLastLabel());
				} catch (NumberFormatException e) {
					throw new BuilderException("Invalid line number: " + split[1] + ".");
				}
			}
			case "TRYCATCH": {
				String[] parts = split[1].trim().split(" ");
				if (parts.length != 4)
					throw new BuilderException("Invalid trycatch: " + split[1].trim());
				LabelNode start = labelHandler.retreiveLabel(parts[0]);
				LabelNode end = labelHandler.retreiveLabel(parts[1]);
				LabelNode handler = labelHandler.retreiveLabel(parts[2]);
				String exc = parts[3];
				TryCatchBlockNode tcbn = new TryCatchBlockNode(start, end, handler, exc);
				m.tryCatchBlocks.add(tcbn);
				return null;
			}
			case "LOCALVAR": {
				// LocalVariableNode lvn = new LocalVariableNode(arg0, arg1, arg2, arg3, arg4, arg5)
				return null;
			}
			case "VISANNO": {
				
			}
			default:
				throw new BuilderException("Unknown metdata key: " + key + " <" + meta + ">.");
		}
	}
	
	protected AbstractInsnNode resolveInstruction(int opcode, StringArrayReader cr) throws BuilderException {
		switch (opcode) {
			case INVOKEDYNAMIC:
			case INVOKEINTERFACE:
			case INVOKESPECIAL:
			case INVOKESTATIC:
			case INVOKEVIRTUAL: {
				String operand = expectCode(cr, "method call operand.");
				String[] s1 = operand.split("\\.");
				if (s1.length != 2)
					throw new BuilderException("Invalid method call operand: " + operand);
				String[] s2 = s1[1].split(":");
				if (s2.length != 2)
					throw new BuilderException("Invalid method call operand: " + operand);
				String className = s1[0];
				String methodName = s2[0];
				String methodDesc = s2[1];
				MethodInsnNode min = new MethodInsnNode(opcode, className, methodName, methodDesc);
				return min;
			}
			
			case GETFIELD:
			case GETSTATIC:
			case PUTFIELD:
			case PUTSTATIC: {
				String operand = expectCode(cr, "field operation operand.");
				String[] s1 = operand.split("\\.");
				if (s1.length != 2)
					throw new BuilderException("Invalid field operation  operand: " + operand);
				String[] s2 = s1[1].split(":");
				if (s2.length != 2)
					throw new BuilderException("Invalid field operation operand: " + operand);
				String className = s1[0];
				String fieldName = s2[0];
				String fieldDesc = s2[1];
				FieldInsnNode fin = new FieldInsnNode(opcode, className, fieldName, fieldDesc);
				return fin;
			}
			
			case ALOAD:
			case ILOAD:
			case DLOAD:
			case FLOAD:
			case LLOAD:
			case ASTORE:
			case ISTORE:
			case DSTORE:
			case FSTORE:
			case LSTORE:
			case RET: {
				String operand = expectCode(cr, "index load/store operand.");
				try {
					int var = Integer.parseInt(operand);
					VarInsnNode vin = new VarInsnNode(opcode, var);
					return vin;
				} catch (NumberFormatException e) {
					throw new BuilderException("Invalid load/store index: " + operand);
				}
			}
			
			case NEW:
			case ANEWARRAY:
			case CHECKCAST:
			case INSTANCEOF: {
				String operand = expectCode(cr, "class name as operand.");
				TypeInsnNode tin = new TypeInsnNode(opcode, operand);
				return tin;
			}
			
			case BIPUSH:
			case SIPUSH:
			case NEWARRAY: {
				String operand = expectCode(cr, "int instruction operand.");
				try {
					int intOperand = Integer.parseInt(operand);
					IntInsnNode iin = new IntInsnNode(opcode, intOperand);
					return iin;
				} catch (NumberFormatException e) {
					throw new BuilderException("Invalid int instruction operand value.");
				}
			}
			case IFEQ:
			case IFNE:
			case IFLT:
			case IFGE:
			case IFGT:
			case IFLE:
			case IF_ICMPEQ:
			case IF_ICMPNE:
			case IF_ICMPLT:
			case IF_ICMPGE:
			case IF_ICMPGT:
			case IF_ICMPLE:
			case IF_ACMPEQ:
			case IF_ACMPNE:
			case GOTO:
			case JSR:
			case IFNULL:
			case IFNONNULL: {
				String operand = expectCode(cr, "jump label.");
				LabelNode ln = labelHandler.retreiveLabel(operand);
				if (ln == null) {
					throw new BuilderException("Invalid label to jump to: " + operand);
				}
				JumpInsnNode jin = new JumpInsnNode(opcode, ln);
				return jin;
			}
			case LDC: {
				String val = expectCode(cr, "ldc value.");
				String type = expectCode(cr, "ldc value type.");
				LdcInsnNode lin = new LdcInsnNode(getValue(val, type));
				return lin;
			}
			default: {
				InsnNode in = new InsnNode(opcode);
				// System.out.println("unknown opcode: " + OpcodeInfo.OPCODES.get(opcode));
				return in;
			}
		}
		// throw new BuilderException("Unknown opcode: " + OpcodeInfo.OPCODES.get(opcode));
	}
	
	protected int isOpcode(String s) {
		if (!OpcodeInfo.OPCODE_NAMES.containsKey(s = s.toUpperCase()))
			return -1;
		return OpcodeInfo.OPCODE_NAMES.get(s);
	}
	
	protected Object getValue(String value, String valueType) throws BuilderException {
		switch (valueType) {
			case "(java.lang.String)":
				if ((value.startsWith("\"") && value.endsWith("\""))) {
					value = value.substring(1);
					value = value.substring(0, value.length() - 1);
					return value;
				}
				throw new BuilderException("String default value fields must be surrounded in \"\"s : " + value);
			case "(java.lang.Integer)":
				try {
					int i = Integer.parseInt(value);
					return i;
				} catch (NumberFormatException e) {
					throw new BuilderException("Invalid integer value: " + value);
				}
			case "(java.lang.Double)":
				try {
					double d = Double.parseDouble(value);
					return d;
				} catch (NumberFormatException e) {
					throw new BuilderException("Invalid double value: " + value);
				}
			case "(java.lang.Long)":
				try {
					long l = Long.parseLong(value);
					return l;
				} catch (NumberFormatException e) {
					throw new BuilderException("Invalid double value: " + value);
				}
			case "(java.lang.Float)":
				try {
					float f = Float.parseFloat(value);
					return f;
				} catch (NumberFormatException e) {
					throw new BuilderException("Invalid double value: " + value);
				}
			case "(org.objectweb.asm.Type)":
				return org.objectweb.asm.Type.getType(value);
			default:
				throw new BuilderException("Unknown field default value type: " + valueType);
		}
	}
	
	protected <T extends ParserToken> T expect(List<ParserToken> tokens, int index, String e1) throws BuilderException {
		try {
			if (index < 0)
				throw new BuilderException("Expected token but index was " + index);
			@SuppressWarnings("unchecked")
			T token = (T) tokens.get(index);
			return token;
		} catch (RuntimeException e) {
			throw new BuilderException("Expecting " + e1, e);
		}
	}
	
	protected String expectCode(StringArrayReader cr, String e1) throws BuilderException {
		try {
			if (!cr.valid())
				throw new BuilderException("Expected token but index was " + cr.index());
			return cr.read();
		} catch (RuntimeException e) {
			throw new BuilderException("Expecting " + e1, e);
		}
	}
}