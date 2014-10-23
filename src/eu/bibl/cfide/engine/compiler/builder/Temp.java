package eu.bibl.cfide.engine.compiler.builder;

import java.util.ArrayList;
import java.util.List;

import org.objectweb.asm.Opcodes;

import eu.bibl.banalysis.asm.ClassNode;
import eu.bibl.cfide.engine.compiler.parser.ParserException;
import eu.bibl.cfide.engine.compiler.parser.impl.TextToBytecodeParser.ParserState;

public class Temp {
	
	public void temp() {
		protected int getUsingClassfileVersion(String lookupValue) throws ParserException {
			if (REVERSE_VERSION_TABLE.containsKey(lookupValue)) {
				return REVERSE_VERSION_TABLE.get(lookupValue);
			} else {
				throw new ParserException("Invalid classfile version: " + lookupValue);
			}
		}
		
		protected int getUsingASMVesion(String lookupValue) throws ParserException {
			lookupValue = lookupValue.toUpperCase();
			if (lookupValue.equals("ASM4")) {
				return Opcodes.ASM4;
			} else if (lookupValue.equals("ASM5")) {
				return Opcodes.ASM5;
			} else {
				try {// if the user put in the actual value for the ASM4/ASM5 constant, allow it anyway ;)
					int val = Integer.parseInt(lookupValue);
					if ((val == Opcodes.ASM4) || (val == Opcodes.ASM5))
						return val;
				} catch (NumberFormatException e) {
				}
			}
			throw new ParserException("Invalid 'using asm:' value: " + lookupValue);
		}
		
		protected boolean isClassNameDeclaration(List<String> tokens, int index) {
			int endingIndex = Math.min(0, index - 20);
			for (int i = index; i >= 0; i--) {
				String token = tokens.get(i);
				if (token.equals("class"))
					return true;
				if (token.equals("enum"))
					return true;
				if (token.equals("annotation"))
					return true;
			}
			return false;
		}
	}
	
	
	//temp1
	int version = 0;
	int asm = 0;
	int access = 0; // shared var between cn.access, f.access and m.access
	String name = null;
	String superName = null;
	List<String> interfaces = new ArrayList<String>();
	
	ClassNode cn = null;
	
	for (int i = 0; i < tokens.size(); i++) {
		String token = tokens.get(i);
		switch (state) {
			case NONE:
				if (token.equals("using")) {
					state = ParserState.USING_SEARCH;
					i--;// 'unread' the last token 'using'
				} else if (token.equals("{")) {
					if (isClassNameDeclaration(tokens, i))
						;
				}
				break;
				
			case USING_SEARCH:
				if (token.equals("using")) {
					String usingWhat = tokens.get(++i);
					String[] splits = usingWhat.split(":");
					if (splits.length == 2) {
						String propertyKey = splits[0];
						String lookupValue = splits[1];
						switch (propertyKey.toUpperCase()) {
							case "ASM":
								version = getUsingASMVesion(lookupValue);
								break;
							case "VER":
								version = getUsingClassfileVersion(lookupValue);
								break;
							default:
								throw new ParserException("Unknown 'using' property:" + usingWhat);
						}
					} else {
						throw new ParserException("Invalid 'using' property: " + usingWhat);
					}
				} else {
					state = ParserState.NONE;
				}
			default:
				break;
		}
	}
	
	return cn;
}
