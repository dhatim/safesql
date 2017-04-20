package org.dhatim.safesql;

import org.dhatim.safesql.assertion.Assertions;
import org.junit.Test;

public class SafeSqlTest {

    @Test
    public void testAsStringEmpty() {
        Assertions.assertThat(SafeSqlUtils.EMPTY).hasEmptySql();
    }

    @Test
    public void testAsString() {
        SafeSql sql = new SafeSqlBuilder().append("SELECT abc, ").param(42).append(" FROM mytable").toSafeSql();

        Assertions.assertThat(sql)
                .hasSql("SELECT abc, ? FROM mytable")
                .hasParameters(42)
                .hasLiteralizedSql("SELECT abc, 42 FROM mytable");
    }

    @Test
    public void testConstant() {
        Assertions.assertThat(SafeSql.constant("SELECT 1"))
                .hasSql("SELECT 1")
                .hasEmptyParameters();
    }

    @Test
    public void testParameter() {
        Assertions.assertThat(SafeSql.parameter("Hello"))
        .hasSql("?")
        .hasParameters("Hello");
    }

    @Test
    public void testIdentifier() {
        Assertions.assertThat(SafeSql.identifier("WithUpperCase"))
        .hasSql("\"WithUpperCase\"")
        .hasEmptyParameters();
    }

}
