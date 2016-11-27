package org.dhatim.safesql.formatter;

import java.util.LinkedList;

import org.dhatim.safesql.SafeSql;
import org.dhatim.safesql.SafeSqlBuilder;
import org.dhatim.safesql.parser.SqlTokenType;
import org.dhatim.safesql.parser.SqlTokenizer;

public class SqlFormatter {

	private final LinkedList<FormatterState> stateStack = new LinkedList<>();
	private FormatterState state = FormatterState.STATE_0;
	
	public SafeSql formatTo(SafeSql sql, SafeSqlBuilder sb) {
		SqlTokenizer tokenizer = new SqlTokenizer(sql.asSql());
		boolean first = true;
		boolean whitespace = false;
		while (tokenizer.hasMoreTokens()) {
			String token = tokenizer.nextValue();
			SqlTokenType type = tokenizer.getCurrentType();
			
			if (type == SqlTokenType.KEYWORD) {
				FormatterState nextState = FormatterState.recognize(token);
				if (nextState != null) {
					state = nextState;
					// change section
					if (first) {
						first = false;
					} else {
						sb.append('\n');
						whitespace = false;
					}
				} 
				sb.append(token);
			} else if (type == SqlTokenType.COMMA) {
				sb.append(token).append('\n');
			} else if (type == SqlTokenType.WHITESPACE) {
				whitespace = true;
			} else {
				if (whitespace) {
					sb.append(' ');
				}
				sb.append(token);
			}
			
		}
		
		return sb.toSafeSql();
		
	}
	
}
