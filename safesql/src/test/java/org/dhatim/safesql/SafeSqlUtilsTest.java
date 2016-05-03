package org.dhatim.safesql;

import static org.assertj.core.api.Assertions.assertThat;
import static org.dhatim.safesql.assertion.Assertions.assertThat;

import java.math.BigDecimal;
import org.junit.Test;

public class SafeSqlUtilsTest {

    @Test
    public void testFromConstant() {
        assertThat(SafeSqlUtils.fromConstant("select")).hasSql("select").hasEmptyParamters();
        assertThat(SafeSqlUtils.fromConstant("")).hasEmptySql().hasEmptyParamters();
    }
    
    @Test
    public void testEscape() {
        assertThat(SafeSqlUtils.escape(5)).hasSql("?").hasParameters(5);
    }
    
    @Test
    public void testFromIdentifier() {
        assertThat(SafeSqlUtils.fromIdentifier("file")).as("Without upper letter").hasSql("file");
        assertThat(SafeSqlUtils.fromIdentifier("S21.G00.23")).as("With upper letter").hasSql("\"S21.G00.23\"");
        assertThat(SafeSqlUtils.fromIdentifier("file")).hasEmptyParamters();
    }
    
    @Test
    public void testEscapeIdentifier() {
        assertThat(SafeSqlUtils.escapeIdentifier("Char string \" with double quote")).isEqualTo("\"Char string \"\" with double quote\"");
    }
    
    @Test
    public void testMustEscapeIdentifier() {
        assertThat(SafeSqlUtils.mustEscapeIdentifier("aA")).as("Upper").isTrue();
        assertThat(SafeSqlUtils.mustEscapeIdentifier("aa")).as("Lower").isFalse();
        assertThat(SafeSqlUtils.mustEscapeIdentifier("a\"a")).as("Double quote").isTrue();
    }
    
    @Test
    public void testToString() {
        assertThat(SafeSqlUtils.toString(safesql("SELECT {} FROM table", 5))).isEqualTo("SELECT 5 FROM table");
        assertThat(SafeSqlUtils.toString(safesql("SELECT {} FROM table", "Cheveux d'ange"))).isEqualTo("SELECT 'Cheveux d''ange' FROM table");
        assertThat(SafeSqlUtils.toString(safesql("SELECT '?', {} FROM table", 5))).isEqualTo("SELECT '?', 5 FROM table");
        assertThat(SafeSqlUtils.toString(safesql("SELECT \"?\", {} FROM table", 5))).isEqualTo("SELECT \"?\", 5 FROM table");
        assertThat(SafeSqlUtils.toString(safesql("SELECT \"hello\"\"world\", {} FROM table", 5))).isEqualTo("SELECT \"hello\"\"world\", 5 FROM table");
        assertThat(SafeSqlUtils.toString(safesql("SELECT \"hello'world\", {} FROM table", 5))).isEqualTo("SELECT \"hello'world\", 5 FROM table");
        assertThat(SafeSqlUtils.toString(safesql("SELECT 'hello\"world', {} FROM table", 5))).isEqualTo("SELECT 'hello\"world', 5 FROM table");
        
        assertThat(SafeSqlUtils.toString(safesql("SELECT {} FROM table", true))).as("Boolean true").isEqualTo("SELECT TRUE FROM table");
        assertThat(SafeSqlUtils.toString(safesql("SELECT {} FROM table", false))).as("Boolean false").isEqualTo("SELECT FALSE FROM table");
        assertThat(SafeSqlUtils.toString(safesql("SELECT {} FROM table", new BigDecimal("0")))).as("BigDecimal").isEqualTo("SELECT 0::numeric FROM table");
    }
    
    @Test
    public void testLiteralize() {
        assertThat(SafeSqlUtils.literalize(safesql("SELECT * FORM table WHERE column = {}", "Hello the world")))
                .hasSql("SELECT * FORM table WHERE column = 'Hello the world'")
                .hasEmptyParamters();
    }
    
    @Test
    public void testFormat() {
        assertThat(SafeSqlUtils.format("SELECT * FROM table WHERE col1 = {} AND col2 = {}", 5, "Hello"))
                .hasSql("SELECT * FROM table WHERE col1 = ? AND col2 = ?")
                .hasParameters(5, "Hello");
    }
    
    private static SafeSql safesql(String sql, Object... args) {
        return SafeSqlUtils.format(sql, args);
    }
    
}
