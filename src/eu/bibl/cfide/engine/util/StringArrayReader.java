package eu.bibl.cfide.engine.util;

import java.util.List;

import eu.bibl.banalysis.filter.Filter;

public class StringArrayReader {
	
	protected final String[] array;
	protected int index;
	protected int mark;
	
	public StringArrayReader(List<String> list) {
		array = list.toArray(new String[list.size()]);
	}
	
	public StringArrayReader(String[] array) {
		this.array = array;
	}
	
	public String read() {
		return array[index++];
	}
	
	public String read(Filter<String> filter) {
		while (index < array.length) {
			String s = read();
			if (filter.accept(s))
				return s;
		}
		return null;
	}
	
	public String readPrev(int amt) {
		index -= amt;
		return array[index];
	}
	
	public String readPrev(Filter<String> filter) {
		while (index >= 0) {
			String s = array[index--];
			if (filter.accept(s))
				return s;
		}
		return null;
	}
	
	public void markPos() {
		mark = index;
	}
	
	public void resetPos() {
		index = mark;
	}
	
	public boolean canReadNext() {
		return index < array.length;
	}
	
	public void move(int i) {
		index += i;
	}
	
	public void reset() {
		index = 0;
	}
	
	public final int size() {
		return array.length;
	}
	
	public final int index() {
		return index;
	}
	
	public boolean valid() {
		return index >= 0;
	}
	
	public void set(int i) {
		index = i;
	}
}