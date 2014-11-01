package eu.bibl.cfide.engine.compiler.parser.cfideimpl.tokens.member;

import java.util.ArrayList;
import java.util.List;

import eu.bibl.banalysis.filter.Filter;
import eu.bibl.cfide.engine.compiler.parser.ParserException;
import eu.bibl.cfide.engine.decompiler.ClassNodeDecompilationUnit;
import eu.bibl.cfide.engine.util.FilterCollection;
import eu.bibl.cfide.engine.util.StringArrayReader;

public class ClassMemberToken extends MemberToken {
	
	protected String name;
	protected String superName;
	protected String[] interfaces;
	
	public ClassMemberToken(int access, String name, String superName, String[] interfaces) {
		super(3, access);
		this.name = name;
		this.superName = superName;
		this.interfaces = interfaces;
	}
	
	public String getName() {
		return name;
	}
	
	public String getSuperName() {
		return superName;
	}
	
	public String[] getInterfaces() {
		return interfaces;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(ClassNodeDecompilationUnit.getAccessString(access));
		sb.append(" ");
		sb.append(name);
		sb.append(" extends ");
		sb.append(superName);
		if (interfaces.length > 0) {
			sb.append(" implements");
			for (String i : interfaces) {
				sb.append(" ");
				sb.append(i);
			}
		}
		return sb.toString();
	}
	
	public static final List<String> CLASS_HEADER_MEMBERS = new ArrayList<String>();
	
	static {
		CLASS_HEADER_MEMBERS.add("EXTENDS");
		CLASS_HEADER_MEMBERS.add("IMPLEMENTS");
	}
	
	public static final Filter<String> EXTENDS_IMPLEMENTS_FILTER = new Filter<String>() {
		@Override
		public boolean accept(String s) {
			s = s.toUpperCase().replace(",", "");
			return s.equals("EXTENDS") || s.equals("IMPLEMENTS") || s.equals("{");
		}
	};
	
	/**
	 * Creates a new ClassMemberToken by parsing the tokens via backwards processing.
	 * @param tokens Raw lexed text tokens.
	 * @param memberEndIndex Must be the index of the last '{' before called.
	 * @return A ClassMemberToken configured correctly.
	 */
	public static ClassMemberToken create(StringArrayReader reader) throws ParserException {
		int access = 0;
		List<String> interfaces = new ArrayList<String>();
		String name = "";
		String superName = "";
		
		mainWhile: while (reader.canReadNext()) {
			String sToken = reader.read(FilterCollection.NON_NULL_NON_NEWLINE_FILTER);
			String uToken = sToken.toUpperCase();
			if (ACCESS_VALUES.containsKey(uToken)) {
				access |= ACCESS_VALUES.get(uToken);
			} else if (CLASS_HEADER_MEMBERS.contains(uToken)) {
				if (uToken.equals("EXTENDS")) {
					reader.markPos();
					reader.move(-2);
					name = reader.read(FilterCollection.NON_NULL_NON_NEWLINE_FILTER);
					reader.resetPos();
					superName = reader.read(FilterCollection.NON_NULL_NON_NEWLINE_FILTER);
				} else if (uToken.equals("IMPLEMENTS")) {
					while (reader.canReadNext()) {
						String jToken = reader.read(FilterCollection.NON_NULL_NON_NEWLINE_FILTER);
						if (jToken.equals("{")) {// pretty sure this will never happen
							break mainWhile;
						}
						interfaces.add(jToken);
					}
				} else if (uToken.equals("{")) {// pretty sure this will never happen
					break;
				}
			} else if (sToken.equals("{")) {
				break;
			}
		}
		ClassMemberToken token = new ClassMemberToken(access, name, superName, interfaces.toArray(new String[interfaces.size()]));
		return token;
	}
}