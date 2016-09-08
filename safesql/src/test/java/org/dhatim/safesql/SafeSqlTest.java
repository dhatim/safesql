package org.dhatim.safesql;

import static org.dhatim.safesql.SimpleSafeSqlAssert.*;
import org.junit.Test;

public class SafeSqlTest {

    @Test
    public void testAsStringEmpty() {
        assertThat(SafeSqlUtils.EMPTY).hasEmptySql();
    }
    
    @Test
    public void testAsString() {
        SafeSql sql = new SafeSqlBuilder().append("SELECT abc, ").param(42).append(" FROM mytable").toSafeSql();
        
        assertThat(sql)
                .hasSql("SELECT abc, ? FROM mytable")
                .hasParameters(42)
                .hasLiteralizedSql("SELECT abc, 42 FROM mytable");
    }
    
}
