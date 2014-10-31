package eu.bibl.cfide.engine.compiler.parser.cfideimpl.tokens.using;

import java.util.List;

import eu.bibl.cfide.engine.compiler.parser.ParserException;
import eu.bibl.cfide.engine.compiler.parser.ParserToken;

public abstract class UsingToken extends ParserToken {
	
	protected String val;
	
	UsingToken(int id, String val) {
		super(id);
		this.val = val;
	}
	
	public String getValue() {
		return val;
	}
	
	public static UsingToken create(List<String> tokens, int memberStartIndex) throws ParserException {
		String first = tokens.get(memberStartIndex).toUpperCase();
		if (first.equals("USING"))
			memberStartIndex++;
		int tok1Index = findNextIndex(tokens, memberStartIndex++);
		int tok2Index = findNextIndex(tokens, memberStartIndex);
		return getByKey(tokens.get(tok1Index), tokens.get(tok2Index));
	}
	
	protected static int findNextIndex(List<String> tokens, int index) {
		while (tokens.size() > index) {
			String token = tokens.get(index).toUpperCase();
			
			if (!token.equals("\n")) {
				return index;
			}
			index++;
		}
		return -1;
	}
	
	public static UsingToken getByKey(String key, String val) {
		key = key.toUpperCase();
		switch (key) {
			case "ASM":
				return new UsingASMToken(val);
			case "VER":
				return new UsingVerToken(val);
		}
		throw new IllegalArgumentException("invalid key: " + key);
		// return null;// handle invalid keys in the builder
	}
}