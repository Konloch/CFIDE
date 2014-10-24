package eu.bibl.cfide.engine.compiler;

import java.util.List;

import eu.bibl.cfide.engine.compiler.builder.IBuilder;
import eu.bibl.cfide.engine.compiler.parser.BasicTokenParser;
import eu.bibl.cfide.engine.compiler.parser.ParserToken;
import eu.bibl.cfide.engine.compiler.parser.cfideimpl.BytecodeSourceParser;
import eu.bibl.cfide.project.CFIDEProject;

public abstract class BasicSourceCompiler<T> implements ICompiler<T, String> {
	
	protected CFIDEProject project;
	protected BasicTokenParser tokenParser;
	protected IBuilder<T, List<ParserToken>> tokenBuilder;
	
	public BasicSourceCompiler(CFIDEProject project) {
		this.project = project;
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