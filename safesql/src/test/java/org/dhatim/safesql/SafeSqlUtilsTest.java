package org.dhatim.safesql;

import static org.dhatim.safesql.fixtures.IsSafeSql.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import java.math.BigDecimal;
import org.dhatim.safesql.SafeSql;
import org.dhatim.safesql.SafeSqlBuilder;
import org.dhatim.safesql.SafeSqlUtils;
import org.dhatim.safesql.SafeSqlizable;
import org.junit.Test;

public class SafeSqlUtilsTest {

    private static class CustomSafeSql implements SafeSqlizable {
        
        private final SafeSql sql;
        
        public CustomSafeSql(String start, Object value, String end) {
            sql = new SafeSqlBuilder()
                    .appendConstant(start)
                    .append(value)
                    .appendConstant(end)
                    .toSafeSql();
        }
        
        @Override
        public SafeSql toSafeSql() {
            return sql;
        }
    }
    
    @Test
    public void testFromConstant() {
        assertThat(SafeSqlUtils.fromConstant("select"), safesql(equalTo("select"), emptyArray()));
        assertThat(SafeSqlUtils.fromConstant(""), safesql(isEmptyString(), emptyArray()));
    }
    
    @Test
    public void testEscape() {
        assertThat(SafeSqlUtils.escape(5), safesql(is("?"), arrayContaining(5)));
    }
    
    @Test
    public void testFromIdentifier() {
       assertThat("Without upper letter", SafeSqlUtils.fromIdentifier("file").asSql(), equalTo("file"));
       assertThat("With upper letter", SafeSqlUtils.fromIdentifier("S21.G00.23").asSql(), equalTo("\"S21.G00.23\""));
       assertThat(SafeSqlUtils.fromIdentifier("file").getParameters(), emptyArray());
    }
    
    @Test
    public void testEscapeIdentifier() {
        assertThat(SafeSqlUtils.escapeIdentifier("Char string \" with double quote"), equalTo("\"Char string \"\" with double quote\""));
    }
    
    @Test
    public void testMustEscapeIdentifier() {
        assertThat("Upper", SafeSqlUtils.mustEscapeIdentifier("aA"), is(true));
        assertThat("Lower", SafeSqlUtils.mustEscapeIdentifier("aa"), is(false));
        assertThat("double quote", SafeSqlUtils.mustEscapeIdentifier("a\"a"), is(true));
    }
    
    @Test
    public void testToString() {
        assertThat(SafeSqlUtils.toString(new CustomSafeSql("SELECT ", 5, " FROM table").toSafeSql()), is("SELECT 5 FROM table"));
        assertThat(SafeSqlUtils.toString(new CustomSafeSql("SELECT ", "Cheveux d'ange", " FROM table").toSafeSql()), is("SELECT 'Cheveux d''ange' FROM table"));
        assertThat(SafeSqlUtils.toString(new CustomSafeSql("SELECT '?', ", 5, " FROM table").toSafeSql()), is("SELECT '?', 5 FROM table"));
        assertThat(SafeSqlUtils.toString(new CustomSafeSql("SELECT \"?\", ", 5, " FROM table").toSafeSql()), is("SELECT \"?\", 5 FROM table"));
        assertThat(SafeSqlUtils.toString(new CustomSafeSql("SELECT \"hello\"\"world\", ", 5, " FROM table").toSafeSql()), is("SELECT \"hello\"\"world\", 5 FROM table"));
        assertThat(SafeSqlUtils.toString(new CustomSafeSql("SELECT \"hello'world\", ", 5, " FROM table").toSafeSql()), is("SELECT \"hello'world\", 5 FROM table"));
        assertThat(SafeSqlUtils.toString(new CustomSafeSql("SELECT 'hello\"world', ", 5, " FROM table").toSafeSql()), is("SELECT 'hello\"world', 5 FROM table"));
        assertThat("Boolean true", SafeSqlUtils.toString(new CustomSafeSql("SELECT ", true, " FROM table").toSafeSql()), is("SELECT TRUE FROM table"));
        assertThat("Boolean false", SafeSqlUtils.toString(new CustomSafeSql("SELECT ", false, " FROM table").toSafeSql()), is("SELECT FALSE FROM table"));
        assertThat("BigDecimal", SafeSqlUtils.toString(new CustomSafeSql("SELECT ", new BigDecimal("0"), " FROM table").toSafeSql()), is("SELECT 0::numeric FROM table"));
    }
    
    @Test
    public void testLiteralize() {
        assertThat(SafeSqlUtils.literalize(new CustomSafeSql("SELECT * FORM table WHERE column = ", "Hello the world", "").toSafeSql()), 
                safesql(is("SELECT * FORM table WHERE column = 'Hello the world'"), emptyArray()));
    }
    
}
