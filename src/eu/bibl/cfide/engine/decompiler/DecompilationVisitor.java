package eu.bibl.cfide.engine.decompiler;

public abstract interface DecompilationVisitor<T> {
	
	public abstract PrefixedStringBuilder decompile(PrefixedStringBuilder sb, T t);
}