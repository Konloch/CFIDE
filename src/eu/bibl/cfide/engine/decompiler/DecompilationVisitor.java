package eu.bibl.cfide.engine.decompiler;

public abstract interface DecompilationVisitor<T> {
	
	public abstract StringBuilder decompile(StringBuilder sb, T t);
}