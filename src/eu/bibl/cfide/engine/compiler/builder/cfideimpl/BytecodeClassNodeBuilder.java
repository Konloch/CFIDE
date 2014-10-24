package eu.bibl.cfide.engine.compiler.builder.cfideimpl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eu.bibl.banalysis.asm.ClassNode;
import eu.bibl.cfide.engine.compiler.builder.BuilderException;
import eu.bibl.cfide.engine.compiler.builder.IBuilder;
import eu.bibl.cfide.engine.compiler.parser.ParserException;
import eu.bibl.cfide.engine.compiler.parser.ParserToken;

public class BytecodeClassNodeBuilder implements IBuilder<ClassNode[], List<ParserToken>> {
	
	public static final Map<Integer, String> VERSION_TABLE = new HashMap<Integer, String>();
	
	@Override
	public ClassNode[] build(List<ParserToken> tokens) throws BuilderException {
		return new ClassNode[0];
	}
	
	protected void expect(List<ParserToken> tokens, ParserToken currentToken) throws ParserException {
		
	}
}