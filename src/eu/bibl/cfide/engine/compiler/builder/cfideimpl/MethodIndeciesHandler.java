package eu.bibl.cfide.engine.compiler.builder.cfideimpl;

import java.util.HashMap;
import java.util.Map;

public class MethodIndeciesHandler {
	
	protected Map<String, Integer> indecies = new HashMap<String, Integer>();
	
	public int resolveIndex(String s) {
		try {
			int i = Integer.parseInt(s);
			return i;
		} catch (NumberFormatException e) {
			if (indecies.containsKey(s)) {
				return indecies.get(s);
			} else {
				return indecies.put(s, indecies.size());
			}
		}
	}
	
	public void reset() {
		indecies.clear();
	}
}