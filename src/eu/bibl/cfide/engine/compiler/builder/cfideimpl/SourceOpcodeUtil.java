package eu.bibl.cfide.engine.compiler.builder.cfideimpl;

import org.objectweb.asm.Opcodes;

public final class SourceOpcodeUtil implements Opcodes {
	
	/**
	 * Amount of operands displayed in the CFIDE source code. <br>
	 * <b>eg.</b> invokestatic myClass.myMethod:desc() would have a SOURCE_OPCODE_LENGTH[INVOKESTATIC] value of 1
	 */
	public static final int[] SOURCE_OPCODE_LENGTH = new int[256];
	
	static {
		for (int i = 0; i < 256; i++) {
			SOURCE_OPCODE_LENGTH[i] = -1;
		}
		
		SOURCE_OPCODE_LENGTH[INVOKEVIRTUAL] = 1;
		SOURCE_OPCODE_LENGTH[INVOKEDYNAMIC] = 1;
		SOURCE_OPCODE_LENGTH[INVOKEINTERFACE] = 1;
		SOURCE_OPCODE_LENGTH[INVOKESPECIAL] = 1;
		SOURCE_OPCODE_LENGTH[INVOKESTATIC] = 1;
		
		SOURCE_OPCODE_LENGTH[GETFIELD] = 1;
		SOURCE_OPCODE_LENGTH[GETSTATIC] = 1;
		SOURCE_OPCODE_LENGTH[PUTFIELD] = 1;
		SOURCE_OPCODE_LENGTH[PUTSTATIC] = 1;
		
		SOURCE_OPCODE_LENGTH[ALOAD] = 1;
		SOURCE_OPCODE_LENGTH[DLOAD] = 1;
		SOURCE_OPCODE_LENGTH[FLOAD] = 1;
		SOURCE_OPCODE_LENGTH[ILOAD] = 1;
		SOURCE_OPCODE_LENGTH[LLOAD] = 1;
		
		SOURCE_OPCODE_LENGTH[ASTORE] = 1;
		SOURCE_OPCODE_LENGTH[DSTORE] = 1;
		SOURCE_OPCODE_LENGTH[FSTORE] = 1;
		SOURCE_OPCODE_LENGTH[ISTORE] = 1;
		SOURCE_OPCODE_LENGTH[LSTORE] = 1;
		
		SOURCE_OPCODE_LENGTH[AALOAD] = 0;
		SOURCE_OPCODE_LENGTH[BALOAD] = 0;
		SOURCE_OPCODE_LENGTH[SALOAD] = 0;
		SOURCE_OPCODE_LENGTH[DALOAD] = 0;
		SOURCE_OPCODE_LENGTH[FALOAD] = 0;
		SOURCE_OPCODE_LENGTH[IALOAD] = 0;
		SOURCE_OPCODE_LENGTH[LALOAD] = 0;
		
		SOURCE_OPCODE_LENGTH[AASTORE] = 0;
		SOURCE_OPCODE_LENGTH[BASTORE] = 0;
		SOURCE_OPCODE_LENGTH[CASTORE] = 0;
		SOURCE_OPCODE_LENGTH[DASTORE] = 0;
		SOURCE_OPCODE_LENGTH[FASTORE] = 0;
		SOURCE_OPCODE_LENGTH[IASTORE] = 0;
		SOURCE_OPCODE_LENGTH[LASTORE] = 0;
		
		SOURCE_OPCODE_LENGTH[ACONST_NULL] = 0;
		SOURCE_OPCODE_LENGTH[ICONST_0] = 0;
		SOURCE_OPCODE_LENGTH[ICONST_1] = 0;
		SOURCE_OPCODE_LENGTH[ICONST_2] = 0;
		SOURCE_OPCODE_LENGTH[ICONST_3] = 0;
		SOURCE_OPCODE_LENGTH[ICONST_4] = 0;
		SOURCE_OPCODE_LENGTH[ICONST_5] = 0;
		SOURCE_OPCODE_LENGTH[ICONST_M1] = 0;
		SOURCE_OPCODE_LENGTH[ICONST_2] = 0;
		SOURCE_OPCODE_LENGTH[ICONST_3] = 0;
		
		SOURCE_OPCODE_LENGTH[DCONST_0] = 0;
		SOURCE_OPCODE_LENGTH[DCONST_1] = 0;
		
		SOURCE_OPCODE_LENGTH[LCONST_0] = 0;
		SOURCE_OPCODE_LENGTH[LCONST_1] = 0;
		
		SOURCE_OPCODE_LENGTH[FCONST_0] = 0;
		SOURCE_OPCODE_LENGTH[FCONST_1] = 0;
		SOURCE_OPCODE_LENGTH[FCONST_2] = 0;
	}
}