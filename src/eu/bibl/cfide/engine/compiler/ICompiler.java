package eu.bibl.cfide.engine.compiler;

public abstract interface ICompiler<T, K> {
	
	public abstract T compile(K k) throws CompilerException;
}