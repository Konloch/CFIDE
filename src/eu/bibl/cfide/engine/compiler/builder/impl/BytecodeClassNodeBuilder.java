package eu.bibl.cfide.engine.compiler.builder.impl;

import java.util.List;

import eu.bibl.banalysis.asm.ClassNode;
import eu.bibl.cfide.engine.compiler.builder.BuilderException;
import eu.bibl.cfide.engine.compiler.builder.IBuilder;
import eu.bibl.cfide.engine.compiler.parser.tokens.ParserToken;

public class BytecodeClassNodeBuilder implements IBuilder<ClassNode[], List<ParserToken>> {
	
	@Override
	public ClassNode[] build(List<ParserToken> k) throws BuilderException {
		return new ClassNode[0];
	}
}