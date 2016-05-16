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

	public String nextToken() {
        while (pos < len) {
        	char cur = data[pos++];
        	if (Character.isLetter(cur)) {
        		if (state == STATE_0) {
        			
        		}
        	} else if (Character.isDigit(cur)) {
        		
        	} else if (Character.isWhitespace(cur)) {
        		
        	} else {
        		switch (cur) {
	            	case '"':
	            		if (state == STATE_0) {
	            			state = STATE_DELIM_IDENT;
	            		} else {
	            			state = STATE_0;
	            		}
	            		sb.append(cur);
	            		break;
	            	case '\'':
	            		 if (state == STATE_0) {
	                         state = STATE_STRING;
	                     } else if (state == STATE_STRING) {
	                         state = STATE_0;
	                     }
	                     sb.append(cur);
	                     break;
            	}
        	}
        	
        }
	}
	
}
