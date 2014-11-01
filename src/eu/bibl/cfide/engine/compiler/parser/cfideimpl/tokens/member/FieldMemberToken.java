package eu.bibl.cfide.engine.compiler.parser.cfideimpl.tokens.member;

import eu.bibl.cfide.engine.compiler.parser.ParserException;
import eu.bibl.cfide.engine.decompiler.FieldNodeDecompilationUnit;
import eu.bibl.cfide.engine.util.StringArrayReader;

public class FieldMemberToken extends MemberToken {
	
	protected String name;
	protected String desc;
	protected String value;
	protected String valueType;
	
	public FieldMemberToken(int access, String name, String desc) {
		super(access);
		this.name = name;
		this.desc = desc;
	}
	
	public FieldMemberToken(int access, String name, String desc, String value, String valueType) {
		super(access);
		this.name = name;
		this.desc = desc;
		this.value = value;
		this.valueType = valueType;
	}
	
	public String getName() {
		return name;
	}
	
	public String getDesc() {
		return desc;
	}
	
	public String getValue() {
		return value;
	}
	
	public String getValueType() {
		return valueType;
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
	
	public static FieldMemberToken create(StringArrayReader reader) throws ParserException {
		int access = 0;
		String name = null;
		String desc = null;
		String value = null;
		String valueType = null;
		
		while (reader.canReadNext()) {
			String sToken = reader.read();
			String uToken = sToken.toUpperCase();
			if (ACCESS_VALUES.containsKey(uToken)) {
				access |= ACCESS_VALUES.get(uToken);
			} else if (uToken.equals(":END")) {
				break;
			} else {
				desc = sToken;
				name = reader.read();
				
				String nextToken = reader.read().toUpperCase();
				if (nextToken.equals(":END")) {
					break;
				} else if (nextToken.equals("=")) {
					value = reader.read();
					valueType = reader.read();
				}
				break;
			}
		}
		
		FieldMemberToken token = new FieldMemberToken(access, name, desc, value, valueType);
		return token;
	}
}