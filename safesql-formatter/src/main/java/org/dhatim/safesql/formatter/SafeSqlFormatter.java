package org.dhatim.safesql.formatter;

import java.util.StringTokenizer;

import org.dhatim.safesql.SafeSql;
import org.dhatim.safesql.SafeSqlBuilder;
import org.dhatim.safesql.SafeSqlUtils;

public class SafeSqlFormatter {

	private static final String INNER_TOKEN = "inner";
	private static final String FROM_TOKEN = "from";
	private static final String SELECT_TOKEN = "select";

	public void formatTo(SafeSql sql, SafeSqlBuilder builder) {
		StringTokenizer tokenizer = new StringTokenizer(sql.asSql(), " (),\t", true);
		while (tokenizer.hasMoreTokens()) {
			String token = tokenizer.nextToken();
			switch (token.toLowerCase()) {
			case SELECT_TOKEN:
			case FROM_TOKEN:
			case INNER_TOKEN:
				builder.append("\n");
				builder.append(token);
				break;
			default:
				builder.append(token);
			}
		}
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
