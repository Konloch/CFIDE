package eu.bibl.cfide.engine.compiler.parser.cfideimpl.tokens.member;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.objectweb.asm.Opcodes;

import eu.bibl.banalysis.filter.Filter;
import eu.bibl.cfide.engine.compiler.parser.ParserToken;

public class MemberToken extends ParserToken {
	
	public static final Map<String, Integer> ACCESS_VALUES = new HashMap<String, Integer>();
	
	static {
		for (Field f : Opcodes.class.getDeclaredFields()) {
			if (f.getName().startsWith("ACC_")) {
				f.setAccessible(true);
				try {
					int value = f.getInt(null);
					String name = f.getName().substring(4);
					ACCESS_VALUES.put(name, value);
					// System.out.println("adding: " + name);
				} catch (IllegalArgumentException | IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	protected int access;
	
	public MemberToken(int id, int access) {
		super(id);
		this.access = access;
	}
	
	public int getAccess() {
		return access;
	}
	
	protected static int findIndexNext(List<String> tokens, int index, Filter<String> filter) {
		while (tokens.size() > index) {
			String token = tokens.get(index).toUpperCase();
			
			if (filter.accept(token)) {
				return index;
			}
			index++;
		}
		return -1;
	}
	
	protected static int findIndexPrev(List<String> tokens, int index, Filter<String> filter) {
		while (index > 0) {
			String token = tokens.get(index).toUpperCase();
			
			if (filter.accept(token)) {
				return index;
			}
			index--;
		}
		return -1;
	}
}