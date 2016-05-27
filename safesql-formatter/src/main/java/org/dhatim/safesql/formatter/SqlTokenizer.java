package org.dhatim.safesql.formatter;

import java.util.StringTokenizer;

import static org.dhatim.safesql.formatter.SqlTokenizer.State.*;

public class SqlTokenizer {
	
	enum State {STATE_0, STATE_DELIM_IDENT, STATE_STRING, STATE_MAY_IDENT}
	
	private final char[] data;
	private State state = State.STATE_0;
	private int pos;
	private final int len;
	private StringBuilder sb = new StringBuilder(32);
	
	public SqlTokenizer(String sql) {
		data = sql.toCharArray();
		len = data.length;
	}

	
}
