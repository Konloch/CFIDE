package eu.bibl.cfide.engine.compiler.parser.cfideimpl.tokens.using;

import eu.bibl.cfide.engine.compiler.parser.ParserException;
import eu.bibl.cfide.engine.compiler.parser.ParserToken;
import eu.bibl.cfide.engine.util.FilterCollection;
import eu.bibl.cfide.engine.util.StringArrayReader;

public abstract class UsingToken extends ParserToken {
	
	protected String val;
	
	UsingToken(String val) {
		this.val = val;
	}
	
	public String getValue() {
		return val;
	}
	
	public static UsingToken create(StringArrayReader reader) throws ParserException {
		String key = reader.read(FilterCollection.NON_NULL_NON_NEWLINE_FILTER);
		String val = reader.read(FilterCollection.NON_NULL_NON_NEWLINE_FILTER);
		return getByKey(key, val);
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