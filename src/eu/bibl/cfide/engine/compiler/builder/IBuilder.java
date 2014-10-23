package eu.bibl.cfide.engine.compiler.builder;

public interface IBuilder<T, K> {
	
	public abstract T build(K k) throws BuilderException;
}