package eu.bibl.cfide.engine.compiler.parser.cfideimpl.tokens.member;

import java.util.ArrayList;
import java.util.List;

import eu.bibl.banalysis.filter.Filter;
import eu.bibl.cfide.engine.compiler.parser.ParserException;
import eu.bibl.cfide.engine.decompiler.MethodNodeDecompilationUnit;

public class MethodMemberToken extends MemberToken {
	
	protected String name;
	protected String desc;
	protected String[] code;// raw code, not processed
	protected String[] excThrows;
	
	public MethodMemberToken(int access, String name, String desc, String[] code, String[] excThrows) {
		super(5, access);
		this.name = name;
		this.desc = desc;
		this.code = code;
		this.excThrows = excThrows;
	}
	
	public String getName() {
		return name;
	}
	
	public String getDesc() {
		return desc;
	}
	
	public String[] getCode() {
		return code;
	}
	
	public String[] getExceptionThrows() {
		return excThrows;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(MethodNodeDecompilationUnit.getAccessString(access));
		sb.append(" ");
		sb.append(name);
		sb.append(" ");
		sb.append(desc);
		if ((excThrows != null) && (excThrows.length != 0)) {
			sb.append(" throws");
			for (String thrw : excThrows) {
				sb.append(" ");
				sb.append(thrw);
			}
		}
		return sb.toString();
	}
	
	public static final Filter<String> THROWS_FILTER = new Filter<String>() {
		@Override
		public boolean accept(String s) {
			return s.toUpperCase().equals("THROWS") || s.toUpperCase().equals("METHOD:");
		}
	};
	
	public static final Filter<String> NON_NEWLINE_FILTER = new Filter<String>() {
		@Override
		public boolean accept(String s) {
			return !s.equals("\n");
		}
	};
	
	public static MethodMemberToken create(List<String> tokens, int memberStartIndex) throws ParserException {
		int access = 0;
		String name = null;
		String desc = null;
		
		List<String> codeList = new ArrayList<String>();
		List<String> throwsList = new ArrayList<String>();
		
		boolean codeMode = false;
		
		for (int i = memberStartIndex; i < tokens.size(); i++) {
			String sToken = tokens.get(i);
			String uToken = sToken.toUpperCase();// // not really needed in method case
			
			if (codeMode) {
				if (sToken.equals("}")) {
					break;
				} else {
					if (!sToken.equals("\n")) {
						codeList.add(sToken);
					}
				}
			} else {
				if (ACCESS_VALUES.containsKey(uToken)) {
					access |= ACCESS_VALUES.get(uToken);
				} else if (sToken.equals("{")) {
					codeMode = true;
					int throwsIndex = findIndexPrev(tokens, i - 1, THROWS_FILTER);
					String throwsToken = tokens.get(throwsIndex);
					if (throwsToken.toUpperCase().equals("THROWS")) {
						int j = throwsIndex;
						desc = tokens.get(findIndexPrev(tokens, --j, NON_NEWLINE_FILTER));
						name = tokens.get(findIndexPrev(tokens, --j, NON_NEWLINE_FILTER));
						for (int k = throwsIndex + 1; k < (i - 1); k++) { // i="{" -1 to ignore it, throws+1 to ignore throws declaration
							String kToken = tokens.get(k);
							if (!kToken.equals("\n")) {
								throwsList.add(kToken);
							}
						}
					} else {
						int j = i - 1;
						desc = tokens.get(findIndexPrev(tokens, --j, NON_NEWLINE_FILTER));
						name = tokens.get(findIndexPrev(tokens, --j, NON_NEWLINE_FILTER));
					}
					continue;
				} else if (sToken.equals("}")) {
					break;
				}
			}
		}
		
		MethodMemberToken token = new MethodMemberToken(access, name, desc, codeList.toArray(new String[codeList.size()]), throwsList.toArray(new String[throwsList.size()]));
		return token;
	}
}