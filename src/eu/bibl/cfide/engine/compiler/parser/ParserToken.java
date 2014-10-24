package eu.bibl.cfide.engine.compiler.parser;

public class ParserToken {
	
	protected final int id;
	
	public ParserToken(int id) {
		this.id = id;
	}
	
	public final int getID() {
		return id;
	}
}
