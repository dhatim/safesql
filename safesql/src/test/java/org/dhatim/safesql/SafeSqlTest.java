package org.dhatim.safesql;

import static org.dhatim.safesql.fixtures.IsSafeSql.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import org.dhatim.safesql.SafeSql;
import org.dhatim.safesql.SafeSqlBuilder;
import org.dhatim.safesql.SafeSqlUtils;
import org.hamcrest.Matchers;
import org.junit.Test;

public class SafeSqlTest {

    @Test
    public void testAsStringEmpty() {
        assertThat(SafeSqlUtils.EMPTY.asString(), isEmptyString());
    }
    
    @Test
    public void testAsString() {
        SafeSql sql = new SafeSqlBuilder().append("SELECT abc, ").param(42).append(" FROM mytable").toSafeSql();
        assertThat(sql, safesql(is("SELECT abc, ? FROM mytable"), Matchers.<Object>arrayContaining(42)));
        assertThat(sql.asString(), is("SELECT abc, 42 FROM mytable"));
    }
    
}
