package eu.bibl.cfide.engine.compiler.parser.cfideimpl;

import java.util.ArrayList;
import java.util.List;

import eu.bibl.cfide.engine.compiler.parser.BasicTokenParser;
import eu.bibl.cfide.engine.compiler.parser.ParserException;
import eu.bibl.cfide.engine.compiler.parser.ParserToken;
import eu.bibl.cfide.engine.compiler.parser.cfideimpl.tokens.member.ClassMemberToken;
import eu.bibl.cfide.engine.compiler.parser.cfideimpl.tokens.member.FieldMemberToken;
import eu.bibl.cfide.engine.compiler.parser.cfideimpl.tokens.member.MethodMemberToken;
import eu.bibl.cfide.engine.compiler.parser.cfideimpl.tokens.using.UsingToken;

public class BytecodeSourceParser extends BasicTokenParser {
	
	@Override
	public List<ParserToken> parse(String text) throws ParserException {
		List<String> lexedTokens = scanStringTokens(text);
		List<ParserToken> tokens = new ArrayList<ParserToken>();
		
		int line = 1; // track line numbers
		try {
			for (int i = 0; i < lexedTokens.size(); i++) {
				String sToken = lexedTokens.get(i);// s - string
				String uToken = sToken.toUpperCase();// case insensitive
				
				if (sToken.equals("\n")) {
					line++;
					continue;
				}
				
				if (uToken.equals("USING")) {
					tokens.add(UsingToken.create(lexedTokens, i));
				} else if (uToken.equals("CLASS:")) {
					ParserToken token = ClassMemberToken.create(lexedTokens, i);
					tokens.add(token);
					System.out.println("Line: " + line + " " + token);
				} else if (uToken.equals("FIELD:")) {
					ParserToken token = FieldMemberToken.create(lexedTokens, i + 1);
					tokens.add(token);
					System.out.println("Line: " + line + " " + token);
				} else if (uToken.equals("METHOD:")) {
					ParserToken token = MethodMemberToken.create(lexedTokens, i + 1);
					tokens.add(token);
					System.out.println("Line: " + line + " " + token);
				}
			}
		} catch (Exception e) {
			throw new ParserException("Parser error on line " + line, e);
		}
		System.out.println(line);
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
	 * Also supports single line comments, but does not add them as data to return. <br>
	 * The returned list contains '\n' characters if present so provide line number support.
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
					tokens.add("\n");
					continue;
				}
			} else {
				if ((c == '/') && (chars[i + 1] == '/')) {// comment start
					blocking = true;
					i++;
				} else {
					String token = getNextStringToken(chars, i);
					if (token.length() > 0) { // prevent empty tokens: http://xn--wxa.pw/dfk
						int read = token.length();
						
						if (token.contains("\n")) {
							for (int j = i; j < (i + read); j++) {
								char c1 = chars[j];
								if (c1 == '\n') {
									tokens.add("\n");
								}
							}
							token = token.replace("\n", "");
							read = token.length();
							tokens.add(token);
							i += read;
						} else {
							tokens.add(token);
							i += read;// skip the amount of characters we just read
						}
					}
				}
			}
		}
		return tokens;
	}
	
	/**
	 * Reads characters until it finds either a line break, a space or a tab. <br>
	 * <b>N.B.</b> The line break is returned at the end.<br>
	 * This method also has the ability to read string literals with escaped strings inside of them. <br>
	 * <code>"\"\""</code> <br>
	 * would be 1 token. <br>
	 * @param chars Input character array
	 * @param start Last tokens end index
	 * @return Next nonwhitespace (except '\n') valid token
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
					if (Character.isWhitespace(c) && (c != '\n')) {
						break;
					} else if (c == '\n') {
						sb.append(c);
						break;
					}
				}
			} else {// in a string "" block, don't break loop on '\n'
				if ((c == '"') && (chars[start - 2] != '\\')) { // check for unescapped " and -2 because of the ++ before
					locked = false;
				}
			}
			
			sb.append(c);
		}
		return sb.toString();
	}
}
