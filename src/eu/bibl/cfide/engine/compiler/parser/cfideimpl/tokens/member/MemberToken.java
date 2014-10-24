package eu.bibl.cfide.engine.compiler.parser.cfideimpl.tokens.member;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import org.objectweb.asm.Opcodes;

import eu.bibl.cfide.engine.compiler.parser.ParserToken;

public class MemberToken extends ParserToken {
	
	public static final Map<String, Integer> ACCESS_VALUES = new HashMap<String, Integer>();
	
	static {
		for (Field f : Opcodes.class.getDeclaredFields()) {
			if (f.getName().startsWith("ACC_")) {
				f.setAccessible(true);
				try {
					int value = f.getInt(null);
					ACCESS_VALUES.put(f.getName().substring(4), value);
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
}