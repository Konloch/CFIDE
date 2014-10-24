package eu.bibl.cfide.engine.compiler;

import java.util.List;

import eu.bibl.cfide.config.CFIDEConfig;
import eu.bibl.cfide.engine.compiler.builder.IBuilder;
import eu.bibl.cfide.engine.compiler.parser.BasicTokenParser;
import eu.bibl.cfide.engine.compiler.parser.ParserToken;
import eu.bibl.cfide.engine.compiler.parser.cfideimpl.BytecodeSourceParser;

public abstract class BasicSourceCompiler<T> implements ICompiler<T, String> {
	
	protected CFIDEConfig config;
	protected BasicTokenParser tokenParser;
	protected IBuilder<T, List<ParserToken>> tokenBuilder;
	
	public BasicSourceCompiler(CFIDEConfig config) {
		this.config = config;
		this.tokenBuilder = getBuilderImpl();
		tokenParser = getTokenParserImpl();
	}
	
	protected BasicTokenParser getTokenParserImpl() {
		return new BytecodeSourceParser();
	}
	
	protected abstract IBuilder<T, List<ParserToken>> getBuilderImpl();
	
	@Override
	public T compile(String source) throws CompilerException {
		List<ParserToken> tokens = tokenParser.parse(source);
		T builtObject = tokenBuilder.build(tokens);
		if (builtObject == null)
			throw new CompilerException("Object was not compiled successfully.");
		return builtObject;
	}
}