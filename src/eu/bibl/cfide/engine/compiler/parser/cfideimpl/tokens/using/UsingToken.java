package eu.bibl.cfide.engine.compiler.parser.cfideimpl.tokens.using;

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
	
	public static UsingToken getByKey(String key, String val) {
		key = key.toUpperCase();
		switch (key) {
			case "ASM":
				return new UsingASMToken(val);
			case "VER":
				return new UsingVerToken(val);
		}
		return null;
	}
}