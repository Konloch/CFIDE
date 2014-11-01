package eu.bibl.cfide.engine.compiler.parser.cfideimpl.tokens.using;

import org.objectweb.asm.Opcodes;

import eu.bibl.cfide.engine.compiler.parser.ParserException;

public class UsingASMToken extends UsingToken {
	
	UsingASMToken(String val) {
		super(val);
	}
	
	public int getASMValue() throws ParserException {
		val = val.toUpperCase();
		if (val.equals("ASM4")) {
			return Opcodes.ASM4;
		} else if (val.equals("ASM5")) {
			return Opcodes.ASM5;
		} else {
			try {// if the user put in the actual value for the ASM4/ASM5 constant, allow it anyway ;)
				int intVal = Integer.parseInt(val);
				if ((intVal == Opcodes.ASM4) || (intVal == Opcodes.ASM5))
					return intVal;
			} catch (NumberFormatException e) {
			}
		}
		throw new ParserException("Invalid 'using asm' value: " + val);
	}
}