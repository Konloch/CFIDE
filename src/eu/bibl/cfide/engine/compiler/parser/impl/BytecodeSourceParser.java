package eu.bibl.cfide.engine.compiler.parser.impl;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.objectweb.asm.Opcodes;

import eu.bibl.cfide.engine.compiler.parser.BasicTokenParser;
import eu.bibl.cfide.engine.compiler.parser.ParserException;
import eu.bibl.cfide.engine.compiler.parser.impl.tokens.StringLiteralToken;
import eu.bibl.cfide.engine.compiler.parser.tokens.ParserToken;

public class BytecodeSourceParser extends BasicTokenParser {
	
	public static final Map<String, Integer> ACCESS_VALUES = new HashMap<String, Integer>();
	public static final Map<String, Integer> REVERSE_VERSION_TABLE = new HashMap<String, Integer>();
	
	static {
		for (Field f : Opcodes.class.getDeclaredFields()) {
			if (f.getName().startsWith("ACC_")) {
				f.setAccessible(true);
				try {
					int value = f.getInt(null);
					ACCESS_VALUES.put(f.getName().substring(4), value);
					System.out.println("Name: " + f.getName().substring(4));
				} catch (IllegalArgumentException | IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		}
		
		System.out.println(ACCESS_VALUES.keySet().size());
		
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
	public List<ParserToken> parse(String text) throws ParserException {
		List<String> lexedTokens = scanStringTokens(text);
		List<ParserToken> tokens = new ArrayList<ParserToken>();
		
		for (int i = 0; i < lexedTokens.size(); i++) {
			String sToken = lexedTokens.get(i);// s - string
			if (sToken.startsWith("\"")) {
				tokens.add(new StringLiteralToken(sToken));
			} else if (ACCESS_VALUES.containsKey(sToken)) {
				
			}
		}
		return tokens;
	}
	
	/**
	 * Turns a string, probably with spacing and new lines are filters out everything unnecessary such as: <br>
	 * <ul>
	 * <li>Spaces</li>
	 * <li>Tabs</li>
	 * <li>New lines</li>
	 * </ul>
	 * <code>
	 * 		invokestatic MyClass.myMethod:desc()
	 * 		ldc "my name is so \"cool\""
	 * </code> <br>
	 * would be turned into: <br>
	 * <ul>
	 * <li>invokestatic</li>,
	 * <li>MyClass.myMethod:desc()</li>,
	 * <li>ldc</li>,
	 * <li>"my name is so \"cool\""</li>
	 * </ul>
	 * Also supports single line comments, but does not add them as data to return.
	 * @see TextToBytecodeParser#getNextStringToken(char[], int)
	 * @param text Raw input text to
	 * @return {@link List} of Strings
	 */
	protected List<String> scanStringTokens(String text) {
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
					String token = getNextStringToken(chars, i);
					if (token.length() > 0) { // prevent empty tokens: http://xn--wxa.pw/dfk
						tokens.add(token);
						i += token.length();// skip the amount of characters we just read
					}
				}
			}
		}
		return tokens;
	}
	
	/**
	 * Reads characters until it finds either a line break, a space or a tab. <br>
	 * This method also has the ability to read string literals with escaped strings inside of them. <br>
	 * <code>"\"\""</code> <br>
	 * would be 1 token. <br>
	 * @param chars Input character array
	 * @param start Last tokens end index
	 * @return Next nonwhitespace valid token
	 */
	protected String getNextStringToken(char[] chars, int start) {
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
}