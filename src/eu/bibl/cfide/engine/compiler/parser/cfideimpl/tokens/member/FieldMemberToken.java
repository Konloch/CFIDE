package eu.bibl.cfide.engine.compiler.parser.cfideimpl.tokens.member;

import java.util.List;

import eu.bibl.cfide.engine.compiler.parser.ParserException;
import eu.bibl.cfide.engine.decompiler.FieldNodeDecompilationUnit;

public class FieldMemberToken extends MemberToken {
	
	protected String name;
	protected String desc;
	protected String value;
	protected String valueType;
	
	public FieldMemberToken(int access, String name, String desc) {
		super(4, access);
		this.name = name;
		this.desc = desc;
	}
	
	public FieldMemberToken(int access, String name, String desc, String value, String valueType) {
		super(4, access);
		this.name = name;
		this.desc = desc;
		this.value = value;
		this.valueType = valueType;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(FieldNodeDecompilationUnit.getAccessString(access));
		sb.append(" ");
		sb.append(name);
		sb.append(" ");
		sb.append(desc);
		if (value != null) {
			sb.append(" = ");
			sb.append(value);
			sb.append(" ");
			sb.append(valueType);
		}
		return sb.toString();
	}
	
	public static FieldMemberToken create(List<String> tokens, int memberStartIndex) throws ParserException {
		int access = 0;
		String name = null;
		String desc = null;
		String value = null;
		String valueType = null;
		
		int i;
		for (i = memberStartIndex; i < tokens.size(); i++) {
			String sToken = tokens.get(i);
			String uToken = sToken.toUpperCase();
			if (ACCESS_VALUES.containsKey(uToken)) {
				access |= ACCESS_VALUES.get(uToken);
			} else if (uToken.equals(":END")) {
				break;
			} else {
				desc = sToken;
				name = tokens.get(++i);
				
				String nextToken = tokens.get(++i).toUpperCase();
				if (nextToken.equals(":END")) {
					break;
				} else if (nextToken.equals("=")) {
					value = tokens.get(++i);
					valueType = tokens.get(++i);
				}
				break;
			}
		}
		
		FieldMemberToken token = new FieldMemberToken(access, name, desc, value, valueType);
		return token;
	}
}