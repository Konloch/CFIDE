package eu.bibl.cfide.engine.parser;

public abstract interface BasicParser<T> {
	
	public abstract T parse(String text) throws ParserException;
}