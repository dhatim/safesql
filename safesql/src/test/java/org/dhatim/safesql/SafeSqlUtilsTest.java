package org.dhatim.safesql;

import static org.assertj.core.api.Assertions.assertThat;
import static org.dhatim.safesql.assertion.Assertions.assertThat;

import java.math.BigDecimal;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.MissingFormatArgumentException;
import java.util.UUID;
import org.junit.Test;

public class SafeSqlUtilsTest {

    private static class MyData implements SafeSqlLiteralizable {

        private final String data;

        public MyData(String data) {
            this.data = data;
        }

        @Override
        public void appendLiteralized(SafeSqlBuilder sb) {
            sb.literal(data);
        }

    }

    private static final DateFormat TIME_FORMAT_WITH_TZ = new SimpleDateFormat("hh:mm:ss:SSSXXX");
    private static final DateFormat TIMESTAMP_FORMAT_WITH_TZ = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss:SSSXXX");

    @Test
    public void testFromConstant() {
        assertThat(SafeSqlUtils.fromConstant("select")).hasSql("select").hasEmptyParameters();
        assertThat(SafeSqlUtils.fromConstant("")).hasEmptySql().hasEmptyParameters();
    }

    @Test
    public void testEscape() {
        assertThat(SafeSqlUtils.escape(5)).hasSql("?").hasParameters(5);
    }

    @Test
    public void testFromIdentifier() {
        assertThat(SafeSqlUtils.fromIdentifier("file")).as("Without upper letter").hasSql("file");
        assertThat(SafeSqlUtils.fromIdentifier("S21.G00.23")).as("With upper letter").hasSql("\"S21.G00.23\"");
        assertThat(SafeSqlUtils.fromIdentifier("file")).hasEmptyParameters();
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
        assertThat(SafeSqlUtils.mustEscapeIdentifier("Étant")).as("no identifier character").isTrue();
        assertThat(SafeSqlUtils.mustEscapeIdentifier("%aA")).as("no identifier character (special)").isTrue();
    }

    @Test
    public void testToString() {
        assertThat(SafeSqlUtils.toString(safesql("SELECT {} FROM table", new Object[] {null}))).isEqualTo("SELECT NULL FROM table");

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

        assertThat(SafeSqlUtils.toString(safesql("SELECT {}", new Timestamp(0)))).as("Timestamp").isEqualTo("SELECT TIMESTAMP WITH TIME ZONE '" + TIMESTAMP_FORMAT_WITH_TZ.format(new Timestamp(0)) + "'");
        assertThat(SafeSqlUtils.toString(safesql("SELECT {}", new Time(0)))).as("Time").isEqualTo("SELECT TIME WITH TIME ZONE '" + TIME_FORMAT_WITH_TZ.format(new Timestamp(0)) + "'");
        assertThat(SafeSqlUtils.toString(safesql("SELECT {}", new java.sql.Date(0)))).as("java.sql.Date").isEqualTo("SELECT DATE '1970-01-01'");
        assertThat(SafeSqlUtils.toString(safesql("SELECT {}", LocalDate.of(1970, 1, 1)))).as("LocalDate").isEqualTo("SELECT DATE '1970-01-01'");
        assertThat(SafeSqlUtils.toString(safesql("SELECT {}", LocalTime.of(1, 10)))).as("LocalTime").isEqualTo("SELECT TIME '01:10'");
        assertThat(SafeSqlUtils.toString(safesql("SELECT {}", LocalDateTime.of(1970, 1, 1, 1, 10)))).as("LocalDateTime").isEqualTo("SELECT TIMESTAMP '1970-01-01 01:10'");
        assertThat(SafeSqlUtils.toString(safesql("SELECT {}", OffsetDateTime.of(1970, 1, 1, 1, 10, 0, 0, ZoneOffset.UTC)))).as("OffsetDateTime").isEqualTo("SELECT TIMESTAMP WITH TIME ZONE '1970-01-01 01:10:00.000Z'");

        assertThat(SafeSqlUtils.toString(safesql("SELECT {}", new UUID(0, 0)))).as("UUID").isEqualTo("SELECT UUID '00000000-0000-0000-0000-000000000000'");
        assertThat(SafeSqlUtils.toString(safesql("SELECT {}", new byte[] {1, 2, 3, 4}))).as("byte[]").isEqualTo("SELECT BYTEA '\\x01020304'");
        assertThat(SafeSqlUtils.toString(safesql("SELECT {}", new MyData("Hello")))).as("SafeSqlLiteralizable").isEqualTo("SELECT 'Hello'");
    }

    @Test
    public void testLiteralize() {
        assertThat(SafeSqlUtils.literalize(safesql("SELECT * FORM table WHERE column = {}", "Hello the world")))
                .hasSql("SELECT * FORM table WHERE column = 'Hello the world'")
                .hasEmptyParameters();
    }

    @Test
    public void testFormat() {
        assertThat(SafeSqlUtils.format("SELECT * FROM table WHERE col1 = {} AND col2 = {}", 5, "Hello"))
                .hasSql("SELECT * FROM table WHERE col1 = ? AND col2 = ?")
                .hasParameters(5, "Hello");

        assertThat(SafeSqlUtils.format("SELECT * FROM table WHERE col1 = {1} AND col2 = {2}", 5, "Hello"))
                .hasSql("SELECT * FROM table WHERE col1 = ? AND col2 = ?")
                .hasParameters(5, "Hello");

        assertThat(SafeSqlUtils.format("SELECT * FROM table WHERE col1 = {{}} AND col2 = {}", 5))
                .hasSql("SELECT * FROM table WHERE col1 = {} AND col2 = ?")
                .hasParameters(5);
    }

    @Test(expected=MissingFormatArgumentException.class)
    public void testFormatMissing() {
        SafeSqlUtils.format("SELECT {}");
    }

    @Test(expected=MissingFormatArgumentException.class)
    public void testFormatMissingCustom() {
        SafeSqlUtils.format("SELECT {3}");
    }

    @Test
    public void testEscapeLikeValue() {
        assertThat(SafeSqlUtils.escapeLikeValue("%hello%")).isEqualTo("\\%hello\\%");
    }

    @Test
    public void testCustomEscapeLikeValue() {
        assertThat(SafeSqlUtils.escapeLikeValue("%hello%", '/')).isEqualTo("/%hello/%");
    }

    private static SafeSql safesql(String sql, Object... args) {
        return SafeSqlUtils.format(sql, args);
    }

}
