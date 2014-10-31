package eu.bibl.cfide.engine.compiler.parser.cfideimpl.tokens.member;

import eu.bibl.cfide.engine.compiler.parser.ParserToken;

public class MemberCloseToken extends ParserToken {
	
	public MemberCloseToken() {
		super(6);
	}
	
	@Override
	public String toString() {
		return "}";
	}
}