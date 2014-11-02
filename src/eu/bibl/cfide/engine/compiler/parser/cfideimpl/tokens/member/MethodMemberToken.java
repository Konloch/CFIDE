package eu.bibl.cfide.engine.compiler.parser.cfideimpl.tokens.member;

import java.util.ArrayList;
import java.util.List;

import eu.bibl.banalysis.filter.Filter;
import eu.bibl.cfide.engine.compiler.parser.ParserException;
import eu.bibl.cfide.engine.decompiler.MethodNodeDecompilationUnit;
import eu.bibl.cfide.engine.util.FilterCollection;
import eu.bibl.cfide.engine.util.StringArrayReader;

public class MethodMemberToken extends MemberToken {
	
	protected String name;
	protected String desc;
	protected String[] code;// raw code, not processed
	protected String[] excThrows;
	
	public MethodMemberToken(int access, String name, String desc, String[] code, String[] excThrows) {
		super(access);
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
	
	public static MethodMemberToken create(StringArrayReader reader) throws ParserException {
		int access = 0;
		String name = null;
		String desc = null;
		
		List<String> codeList = new ArrayList<String>();
		List<String> throwsList = new ArrayList<String>();
		
		boolean codeMode = false;
		
		while (reader.canReadNext()) {
			String sToken = reader.read(FilterCollection.NON_NULL_NON_NEWLINE_FILTER);
			String uToken = sToken.toUpperCase();
			if (codeMode) {
				if (sToken.equals("}")) {
					reader.move(-1);
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
					reader.move(-1);
					String throwsToken = reader.readPrev(THROWS_FILTER);
					String uThrowsToken = throwsToken.toUpperCase();
					if (uThrowsToken.equals("THROWS")) {// public name desc throws exc, exc {
						reader.markPos();// mark the 'throws'
						desc = reader.readPrev(FilterCollection.NON_NULL_NON_NEWLINE_FILTER);
						name = reader.readPrev(FilterCollection.NON_NULL_NON_NEWLINE_FILTER);
						reader.resetPos();
						reader.move(2);// +2
						while (reader.canReadNext()) {
							String excThrow = reader.read(FilterCollection.NON_NULL_NON_NEWLINE_FILTER);
							if (excThrow.equals("{"))
								break;
							excThrow = excThrow.replace(",", "");
							throwsList.add(excThrow);
						}
					} else if (uThrowsToken.equals("METHOD:")) {
						reader.move(2);// pass "method:"
						while (reader.canReadNext()) {
							String s = reader.read(FilterCollection.NON_NULL_NON_NEWLINE_FILTER);
							if (!ACCESS_VALUES.containsKey(s.toUpperCase())) {
								break;
							}
						}
						reader.move(-1);
						name = reader.read(FilterCollection.NON_NULL_NON_NEWLINE_FILTER);
						desc = reader.read(FilterCollection.NON_NULL_NON_NEWLINE_FILTER);
					}
					continue;
				} else if (sToken.equals("}")) {
					reader.move(-1);
					break;
				}
			}
		}
		
		MethodMemberToken token = new MethodMemberToken(access, name, desc, codeList.toArray(new String[codeList.size()]), throwsList.toArray(new String[throwsList.size()]));
		return token;
	}
}