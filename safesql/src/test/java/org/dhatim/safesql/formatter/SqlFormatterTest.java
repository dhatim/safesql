package org.dhatim.safesql.formatter;

import static org.assertj.core.api.Assertions.*;
import static org.dhatim.safesql.assertion.Assertions.*;

import org.dhatim.safesql.SafeSql;
import org.dhatim.safesql.SafeSqlBuilder;
import org.junit.Test;

public class SqlFormatterTest {

	@Test
	public void testSelectSimple() {
		assertThat(formatted("SELECT one FROM numbers WHERE name = 'Lorem'")).hasSql("SELECT one\nFROM numbers\nWHERE name = 'Lorem'");
		assertThat(formatted("SELECT one, two FROM numbers WHERE name = 'Lorem'")).hasSql("SELECT one\nFROM numbers\nWHERE name = 'Lorem'");
	}
	
	private static SafeSql formatted(String sql) {
		SafeSqlBuilder sb = new SafeSqlBuilder();
		SqlFormatter formatter = new SqlFormatter();
		formatter.formatTo(SafeSql.constant(sql), sb);
		return sb.toSafeSql();
	}
	
}
