package eu.bibl.cfide.engine.compiler.parser;

public abstract interface IParser<T, K> {
	
	public abstract T parse(K k) throws ParserException;
}