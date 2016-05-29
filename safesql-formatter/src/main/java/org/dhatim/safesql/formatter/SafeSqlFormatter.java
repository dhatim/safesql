package org.dhatim.safesql.formatter;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Locale;

import org.dhatim.safesql.SafeSql;
import org.dhatim.safesql.SafeSqlBuilder;
import org.dhatim.safesql.SafeSqlUtils;
import org.dhatim.safesql.parser.SqlTokenType;
import org.dhatim.safesql.parser.SqlTokenizer;

public class SafeSqlFormatter {
	
	public static class Options {
		private boolean returnAfterSelect = true;
		private boolean returnAfterSelectElement = false;
		private boolean returnAfterFrom = true;
		private boolean returnAfterFromElement = true;
	}
	
	private enum State {
		STATE_0, STATE_SELECT, STATE_FROM, STATE_WHERE;
	}

	private static final String INNER_TOKEN = "inner";
	private static final String FROM_TOKEN = "from";
	private static final String SELECT_TOKEN = "select";
	
	private static final String CR = "\n";
	private static final String NONE = "";
	
	private final Options options;
	
	
	public SafeSqlFormatter() {
		this(new Options());
	}
	
	public SafeSqlFormatter(Options options) {
		this.options = options;
	}

	public void formatTo(SafeSql sql, SafeSqlBuilder builder) {
		SqlTokenizer tokenizer = new SqlTokenizer(sql.asSql());
		int level = 0;
		Deque<State> states = new ArrayDeque<>();
		states.push(State.STATE_0);
		
		while (tokenizer.hasMoreTokens()) {
			String value = tokenizer.nextValue();
			SqlTokenType type = tokenizer.getCurrentType();
			
			State nextState;
			
			//states.
			
			String before = NONE;
			String after = NONE;
			
			switch (type) {
				case KEYWORD:
					String lowerKeyword = value.toLowerCase(Locale.ROOT);
					switch (lowerKeyword) {
						case "select":
							nextState = State.STATE_SELECT;
							after = crIf(options.returnAfterSelect);
							break;
						default:
							
					}
				default:
					
			}
			
			if (before != NONE) {
				builder.append(before);
			}
			
			builder.append(value);
			
			if (after != NONE) {
				builder.append(after);
			}
			
			
			
		}
	}
	
	private static String setIf(boolean option, String value) {
		return option ? value : NONE;
	}
	
	private static String crIf(boolean option) {
		return option ? CR : NONE;
	}

	public SafeSql format(SafeSql sql) {
		SafeSqlBuilder sb = new SafeSqlBuilder();
		formatTo(sql, sb);
		return sb.toSafeSql();
	}

	public static void main(String[] args) {
		SafeSql sql = SafeSqlUtils.fromConstant("SELECT id, label, text FROM control INNER JOIN revision ON revision.id = control.revision");
		SafeSqlFormatter formatter = new SafeSqlFormatter();
		System.out.println(formatter.format(sql).asSql());
	}

}
