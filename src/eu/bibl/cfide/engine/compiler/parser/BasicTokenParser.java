package eu.bibl.cfide.engine.compiler.parser;

import java.util.List;

public abstract class BasicTokenParser implements IParser<List<ParserToken>, String> {
	
	@Override
	public abstract List<ParserToken> parse(String text) throws ParserException;
}