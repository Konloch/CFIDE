package eu.bibl.cfide.engine.compiler.parser.cfideimpl.tokens.using;

import java.util.HashMap;
import java.util.Map;

import org.objectweb.asm.Opcodes;

import eu.bibl.cfide.engine.compiler.parser.ParserException;

public class UsingVerToken extends UsingToken {
	
	public static final Map<String, Integer> REVERSE_VERSION_TABLE = new HashMap<String, Integer>();
	
	{
		REVERSE_VERSION_TABLE.put("V1_1", Opcodes.V1_1);
		REVERSE_VERSION_TABLE.put("V1_2", Opcodes.V1_2);
		REVERSE_VERSION_TABLE.put("V1_3", Opcodes.V1_3);
		REVERSE_VERSION_TABLE.put("V1_4", Opcodes.V1_4);
		REVERSE_VERSION_TABLE.put("V1_5", Opcodes.V1_5);
		REVERSE_VERSION_TABLE.put("V1_6", Opcodes.V1_6);
		REVERSE_VERSION_TABLE.put("V1_7", Opcodes.V1_7);
		REVERSE_VERSION_TABLE.put("V1_8", Opcodes.V1_8);
	}
	
	UsingVerToken(String val) {
		super(2, val);
	}
	
	public int getClassFileVersion() throws ParserException {
		val = val.toUpperCase();
		if (REVERSE_VERSION_TABLE.containsKey(val))
			return REVERSE_VERSION_TABLE.get(val);
		throw new ParserException("Invalid 'using ver' value: " + val);
	}
}