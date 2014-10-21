package eu.bibl.cfide.engine.parser;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.objectweb.asm.Opcodes;

import eu.bibl.banalysis.asm.ClassNode;

public class TextToBytecodeParser implements BasicParser<ClassNode> {
	
	public static final Map<String, Integer> ACCESS_VALUES = new HashMap<String, Integer>();
	public static final Map<String, Integer> REVERSE_VERSION_TABLE = new HashMap<String, Integer>();
	
	static {
		for (Field f : Opcodes.class.getDeclaredFields()) {
			if (f.getName().startsWith("ACC_")) {
				f.setAccessible(true);
				try {
					int value = f.getInt(null);
					ACCESS_VALUES.put(f.getName().substring(4), value);
				} catch (IllegalArgumentException | IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		}
		
		REVERSE_VERSION_TABLE.put("V1_1", Opcodes.V1_1);
		REVERSE_VERSION_TABLE.put("V1_2", Opcodes.V1_2);
		REVERSE_VERSION_TABLE.put("V1_3", Opcodes.V1_3);
		REVERSE_VERSION_TABLE.put("V1_4", Opcodes.V1_4);
		REVERSE_VERSION_TABLE.put("V1_5", Opcodes.V1_5);
		REVERSE_VERSION_TABLE.put("V1_6", Opcodes.V1_6);
		REVERSE_VERSION_TABLE.put("V1_7", Opcodes.V1_7);
		REVERSE_VERSION_TABLE.put("V1_8", Opcodes.V1_8);
	}
	
	@Override
	public ClassNode parse(String text) throws ParserException {
		ParserState state = ParserState.NONE;
		List<String> tokens = parseTokens(text);
		
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
						i--;// 'unread' the last token
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
	
	protected boolean isClassNameDeclaration(String token, int access) {
		if (token.equals("class"))
			return true;
		if (token.equals("enum"))
			return true;
		if (token.equals("annotation"))
			return true;
		if (token.equals("interface"))
			return true;
		return false;
	}
	
	protected List<String> parseTokens(String text) {
		char[] chars = text.toCharArray();
		List<String> tokens = new ArrayList<String>();
		boolean blocking = false;
		for (int i = 0; i < chars.length; i++) {
			char c = chars[i];
			if (blocking) {// if we're currently in a comment
				if (c == '\n') {// if new line, comment ends, start parsing
					blocking = false;
					continue;
				}
			} else {
				if ((c == '/') && (chars[i + 1] == '/')) {// comment start
					blocking = true;
					i++;
				} else {
					String token = getNextToken(chars, i);
					if (token.length() > 0) { // prevent empty tokens: http://λ.pw/dfk
						tokens.add(token);
						i += token.length();// skip the amount of characters we just read
					}
				}
			}
		}
		return tokens;
	}
	
	protected String getNextToken(char[] chars, int start) {
		StringBuilder sb = new StringBuilder();
		int len = chars.length;
		boolean locked = false;
		
		while (start < len) {
			char c = chars[start++];
			if (!locked) {
				if (c == '"') {
					locked = true;
				} else {
					if (Character.isWhitespace(c))
						break;
					if (c == '\n')
						break;
				}
			} else {
				if ((c == '"') && (chars[start - 2] != '\\')) { // check for unescapped " and -2 because of the ++ before
					locked = false;
				}
			}
			
			sb.append(c);
		}
		return sb.toString();
	}
	
	public enum ParserState {
		
		NONE(),
		USING_SEARCH(),
	}
}