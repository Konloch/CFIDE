package eu.bibl.cfide.engine.compiler.parser;

import java.util.List;

import eu.bibl.cfide.engine.compiler.parser.tokens.ParserToken;

public abstract class BasicTokenParser implements IParser<List<ParserToken>, String> {
	
	@Override
	public abstract List<ParserToken> parse(String text) throws ParserException;
}