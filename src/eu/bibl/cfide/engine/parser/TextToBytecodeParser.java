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
	}
	
	@Override
	public ClassNode parse(String text) {
		List<String> tokens = parseTokens(text);
		for (String token : tokens) {
			System.out.println(token);
		}
		return null;
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
					tokens.add(token);
					i += token.length();// skip the amount of characters we just read
				}
			}
		}
		return tokens;
	}
	
	protected String getNextToken(char[] chars, int start) {
		StringBuilder sb = new StringBuilder();
		int len = chars.length;
		while (start < len) {
			char c = chars[start++];
			if (Character.isWhitespace(c))
				break;
			if (c == '\n')
				break;
			
			sb.append(c);
		}
		return sb.toString();
	}
}