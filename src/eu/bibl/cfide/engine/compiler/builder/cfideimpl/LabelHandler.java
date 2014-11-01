package eu.bibl.cfide.engine.compiler.builder.cfideimpl;

import java.util.HashMap;
import java.util.Map;

import org.objectweb.asm.tree.LabelNode;

public class LabelHandler {
	
	protected Map<String, LabelNode> labels = new HashMap<String, LabelNode>();
	
	public LabelNode resolveLabel(String s) {
		s = formatLabel(s);
		if (labels.containsKey(s)) {
			return labels.get(s);
		} else {
			LabelNode ln = new LabelNode();
			labels.put(s, ln);
			return ln;
		}
	}
	
	public String formatLabel(String s) {
		if (s.endsWith(":"))
			return s.substring(0, s.length() - 1);
		return s;
	}
	
	protected LabelNode lastLabel;
	
	public LabelNode getLastLabel() {
		return lastLabel;
	}
	
	public LabelNode retreiveLabel(String s) {
		s = formatLabel(s);
		return lastLabel = labels.get(s);
	}
	
	public void reset() {
		labels.clear();
	}
}