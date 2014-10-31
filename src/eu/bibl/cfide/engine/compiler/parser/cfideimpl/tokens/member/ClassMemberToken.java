package eu.bibl.cfide.engine.compiler.parser.cfideimpl.tokens.member;

import java.util.ArrayList;
import java.util.List;

import eu.bibl.banalysis.filter.Filter;
import eu.bibl.cfide.engine.compiler.parser.ParserException;
import eu.bibl.cfide.engine.decompiler.ClassNodeDecompilationUnit;

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
	public static ClassMemberToken create(List<String> tokens, int memberStartIndex) throws ParserException {
		int access = 0;
		List<String> interfaces = new ArrayList<String>();
		String name = "";
		String superName = "";
		
		int i;
		mainFor: for (i = memberStartIndex; i < tokens.size(); i++) {
			String sToken = tokens.get(i);
			String uToken = sToken.toUpperCase();
			if (ACCESS_VALUES.containsKey(uToken)) {
				access |= ACCESS_VALUES.get(uToken);
			} else if (CLASS_HEADER_MEMBERS.contains(uToken)) {
				int index = findIndexNext(tokens, i, EXTENDS_IMPLEMENTS_FILTER);
				String uTokenAtIndex = tokens.get(index).toUpperCase();
				if (uTokenAtIndex.equals("EXTENDS")) {
					name = tokens.get(index - 1);
					superName = tokens.get(index + 1);
					i = index + 1;
				} else if (uTokenAtIndex.equals("IMPLEMENTS")) {
					for (int j = index; j < tokens.size(); j++) {
						String jToken = tokens.get(j);
						if (jToken.equals("{")) {// pretty sure this will never happen
							break mainFor;
						}
						interfaces.add(jToken);
					}
				} else if (uTokenAtIndex.equals("{")) {// pretty sure this will never happen
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