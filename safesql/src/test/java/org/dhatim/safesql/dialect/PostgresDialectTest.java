package org.dhatim.safesql.dialect;

import static org.assertj.core.api.Assertions.assertThat;

import org.dhatim.safesql.Dialect;
import org.dhatim.safesql.SafeSqlUtils;
import org.junit.Test;

public class PostgresDialectTest {

    @Test
    public void testEscapeIdentifier() {
        PostgresDialect dialect = newDialect();
        assertThat(dialect.escapeIdentifier(new StringBuilder(), "Char string \" with double quote")).isEqualTo("\"Char string \"\" with double quote\"");
    }

    @Test
    public void testMustEscapeIdentifier() {
        assertThat(PostgresDialect.needEscapeIdentifier("aA")).as("Upper").isTrue();
        assertThat(PostgresDialect.needEscapeIdentifier("aa")).as("Lower").isFalse();
        assertThat(PostgresDialect.needEscapeIdentifier("a\"a")).as("Double quote").isTrue();
        assertThat(PostgresDialect.needEscapeIdentifier("Étant")).as("no identifier character").isTrue();
        assertThat(PostgresDialect.needEscapeIdentifier("%aA")).as("no identifier character (special)").isTrue();
    }

    private static PostgresDialect newDialect() {
        return new PostgresDialect();
    }

}
