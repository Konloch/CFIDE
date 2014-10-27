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
		String first = tokens.get(memberStartIndex);
		if (first.equals("USING"))
			memberStartIndex++;
		return getByKey(tokens.get(memberStartIndex = findNextIndex(tokens, memberStartIndex++)), tokens.get(memberStartIndex = findNextIndex(tokens, memberStartIndex++)));
	}
	
	protected static int findNextIndex(List<String> tokens, int index) {
		while (tokens.size() > index) {
			String token = tokens.get(index).toUpperCase();
			
			if (token.equals("\n")) {
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
		return null;// handle invalid keys in the builder
	}
}