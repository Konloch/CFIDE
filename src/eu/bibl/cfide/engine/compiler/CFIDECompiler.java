package eu.bibl.cfide.engine.compiler;

import java.lang.reflect.Constructor;
import java.util.List;

import eu.bibl.banalysis.asm.ClassNode;
import eu.bibl.cfide.context.CFIDEContext;
import eu.bibl.cfide.engine.compiler.builder.IBuilder;
import eu.bibl.cfide.engine.compiler.builder.cfideimpl.BytecodeClassNodeBuilder;
import eu.bibl.cfide.engine.compiler.parser.BasicTokenParser;
import eu.bibl.cfide.engine.compiler.parser.ParserToken;
import eu.bibl.cfide.engine.compiler.parser.cfideimpl.BytecodeSourceParser;
import eu.bibl.cfide.io.config.CFIDEConfig;

public class CFIDECompiler extends BasicSourceCompiler<ClassNode[]> {
	
	public CFIDECompiler(CFIDEContext context) {
		super(context);
	}
	
	@Override
	protected BasicTokenParser getTokenParserImpl() {
		BasicTokenParser parserImpl = null;
		String className = null;
		try {
			className = context.config.getProperty(CFIDEConfig.COMPILER_PARSER_CLASS_KEY, BytecodeSourceParser.class.getCanonicalName());
			Class<?> c = Class.forName(className);
			Constructor<?> c1 = c.getConstructor(CFIDEContext.class);
			parserImpl = (BasicTokenParser) c1.newInstance(context);
		} catch (Exception e) {
			System.out.println("Error loading custom parser: " + className);
			e.printStackTrace();
			context.config.putProperty(CFIDEConfig.COMPILER_PARSER_CLASS_KEY, BytecodeSourceParser.class.getCanonicalName());
			parserImpl = super.getTokenParserImpl();
		}
		return parserImpl;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected IBuilder<ClassNode[], List<ParserToken>> getBuilderImpl() {
		IBuilder<ClassNode[], List<ParserToken>> builderImpl = null;
		String className = null;
		try {
			className = context.config.getProperty(CFIDEConfig.COMPILER_BUILDER_CLASS_KEY, BytecodeClassNodeBuilder.class.getCanonicalName());
			Class<?> c = Class.forName(className);
			Constructor<?> c1 = c.getConstructor(CFIDEContext.class);
			builderImpl = (IBuilder<ClassNode[], List<ParserToken>>) c1.newInstance(context);
		} catch (Exception e) {
			System.out.println("Error loading custom builder: " + className);
			e.printStackTrace();
			context.config.putProperty(CFIDEConfig.COMPILER_BUILDER_CLASS_KEY, BytecodeClassNodeBuilder.class.getCanonicalName());
			builderImpl = new BytecodeClassNodeBuilder(context);
		}
		return builderImpl;
	}
	
	@Override
	public ClassNode[] compile(String source) throws CompilerException {
		List<ParserToken> tokens = tokenParser.parse(source);
		ClassNode[] builtObjects = tokenBuilder.build(tokens);
		if ((builtObjects == null))
			throw new CompilerException("Object was not compiled successfully.");
		return builtObjects;
	}
}