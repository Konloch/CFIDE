package eu.bibl.cfide.engine.compiler.parser.cfideimpl.tokens.member;

import eu.bibl.cfide.engine.compiler.parser.ParserToken;

public class MetaItemToken extends ParserToken {
	
	protected String meta;
	
	public MetaItemToken(String meta) {
		this.meta = meta;
	}
	
	public String getMetadata() {
		return meta;
	}
}