package eu.bibl.cfide.engine.compiler.parser.cfideimpl.tokens.member;

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
}