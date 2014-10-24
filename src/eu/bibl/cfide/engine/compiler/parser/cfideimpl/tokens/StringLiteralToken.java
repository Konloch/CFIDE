package eu.bibl.cfide.engine.compiler.parser.cfideimpl.tokens;

import eu.bibl.cfide.engine.compiler.parser.ParserToken;

public class StringLiteralToken extends ParserToken {
	
	protected String value;
	
	public StringLiteralToken(String value) {
		super(1);
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}
	
	public void setValue(String value) {
		this.value = value;
	}
}