package eu.bibl.cfide.engine.compiler.parser.cfideimpl.tokens.member;

import java.util.ArrayList;
import java.util.List;

import eu.bibl.banalysis.filter.Filter;

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
	
	public String superName() {
		return superName;
	}
	
	public String[] getInterfaces() {
		return interfaces;
	}
	
	public static final List<String> CLASS_HEADER_MEMBERS = new ArrayList<String>();
	
	static {
		CLASS_HEADER_MEMBERS.add("ENUM");
		CLASS_HEADER_MEMBERS.add("CLASS");
		CLASS_HEADER_MEMBERS.add("INTERFACE");
		CLASS_HEADER_MEMBERS.add("ANNOTATION");
		CLASS_HEADER_MEMBERS.add("EXTENDS");
		CLASS_HEADER_MEMBERS.add("IMPLEMENTS");
		CLASS_HEADER_MEMBERS.add(",");
	}
	
	public static final Filter<String> USING_FILTER = new Filter<String>() {
		@Override
		public boolean accept(String s) {
			return s.toUpperCase().equals("USING");
		}
	};
	
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
	public static ClassMemberToken create(List<String> tokens, int memberEndIndex) {
		int access = 0;
		List<String> interfaces = new ArrayList<String>();
		
		int minIndex = findIndexPrev(tokens, memberEndIndex, USING_FILTER) + 3;
		for (int i = minIndex; i < memberEndIndex; i++) {
			String token = tokens.get(i);
			String uToken = token.toUpperCase();
			if (CLASS_HEADER_MEMBERS.contains(uToken)) {
				int index = findIndexNext(tokens, i, EXTENDS_IMPLEMENTS_FILTER);
				String nextToken = tokens.get(index);
				System.out.println(nextToken);
				if (nextToken.toUpperCase().equals("EXTENDS")) {
					
				} else if (nextToken.toUpperCase().equals("EXTENDS")) {
					
				}
			} else if (ACCESS_VALUES.containsKey(uToken)) {
				access |= ACCESS_VALUES.get(uToken);
			}
		}
		return null;
	}
	
	protected static int findIndexNext(List<String> tokens, int index, Filter<String> filter) {
		while (tokens.size() > index) {
			index++;
			String token = tokens.get(index).toUpperCase();
			
			if (filter.accept(token)) {
				return index;
			}
		}
		return -1;
	}
	
	protected static int findIndexPrev(List<String> tokens, int index, Filter<String> filter) {
		while (index > 0) {
			index--;
			String token = tokens.get(index).toUpperCase();
			
			if (filter.accept(token)) {
				return index;
			}
		}
		return -1;
	}
	
	// public static ClassMemberToken
}