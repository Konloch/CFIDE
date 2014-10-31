package eu.bibl.cfide.engine.compiler.builder.cfideimpl.util;

public class CodeArrayReader {
	
	protected final String[] code;
	protected int index;
	
	public CodeArrayReader(String[] code) {
		this.code = code;
	}
	
	public String read() {
		String s = code[index];
		index++;
		return s;
	}
	
	public boolean canReadNext() {
		return index < code.length;
	}
	
	public void move(int i) {
		index += i;
	}
	
	public void reset() {
		index = 0;
	}
	
	public final int size() {
		return code.length;
	}
	
	public final int index() {
		return index;
	}
	
	public boolean valid() {
		return index >= 0;
	}
}