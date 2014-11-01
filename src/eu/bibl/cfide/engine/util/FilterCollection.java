package eu.bibl.cfide.engine.util;

import eu.bibl.banalysis.filter.Filter;

public final class FilterCollection {
	
	public static final Filter<String> NON_NULL_FILTER = new Filter<String>() {
		@Override
		public boolean accept(String s) {
			return s != null;
		}
	};
	
	public static final Filter<String> NON_NULL_NON_NEWLINE_FILTER = new Filter<String>() {
		@Override
		public boolean accept(String s) {
			return (s != null) && !s.equals("\n");
		}
	};
}