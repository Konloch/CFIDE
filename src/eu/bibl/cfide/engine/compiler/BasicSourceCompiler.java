package eu.bibl.cfide.engine.compiler;

import java.util.List;

import eu.bibl.cfide.context.CFIDEContext;
import eu.bibl.cfide.engine.compiler.builder.IBuilder;
import eu.bibl.cfide.engine.compiler.parser.BasicTokenParser;
import eu.bibl.cfide.engine.compiler.parser.ParserToken;
import eu.bibl.cfide.engine.compiler.parser.cfideimpl.BytecodeSourceParser;

public abstract class BasicSourceCompiler<T> implements ICompiler<T, String> {
	
	protected CFIDEContext context;
	protected BasicTokenParser tokenParser;
	protected IBuilder<T, List<ParserToken>> tokenBuilder;
	
	public BasicSourceCompiler(CFIDEContext context) {
		this.context = context;
		this.tokenBuilder = getBuilderImpl();
		tokenParser = getTokenParserImpl();
	}
	
	protected BasicTokenParser getTokenParserImpl() {
		return new BytecodeSourceParser(context);
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