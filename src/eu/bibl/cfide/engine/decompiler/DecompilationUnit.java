package eu.bibl.cfide.engine.decompiler;

public abstract interface DecompilationUnit<T> {
	
	public abstract PrefixedStringBuilder decompile(PrefixedStringBuilder sb, T t);
}