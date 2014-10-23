package eu.bibl.cfide.engine.compiler.parser.impl.tokens;

import eu.bibl.cfide.engine.compiler.parser.tokens.ParserToken;

public class StringLiteralToken extends ParserToken {
	
	protected String value;
	
	public StringLiteralToken(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}
	
	public void setValue(String value) {
		this.value = value;
	}
}