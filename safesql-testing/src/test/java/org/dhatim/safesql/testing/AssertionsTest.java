package org.dhatim.safesql.testing;

import static org.dhatim.safesql.testing.Assertions.*;
import static org.dhatim.safesql.testing.matcher.Matchers.*;

import org.dhatim.safesql.SafeSql;
import org.dhatim.safesql.SafeSqlUtils;
import org.junit.Test;

public class AssertionsTest {
    
    private static SafeSql sql(String s) {
        return SafeSqlUtils.fromConstant(s);
    }
    
    @Test
    public void testSimpleQuery() {
        assertThat(SafeSqlUtils.fromConstant("SELECT col1 FROM table1 WHERE col2 = 4")).isQueryWith(where(equal(column("col2"), literal(4))));
    }
    
    @Test
    public void testSimpleFromTableName() {
        assertThat(sql("select id from mytable")).isQueryWith(from(table("mytable")));
    }
    
    @Test
    public void testSimpleLeftJoin() {
        assertThat(sql("SELECT * FROM mytable m LEFT JOIN myothertable o ON my.id = o.outid")).isQueryWith(from(table("mytable"), leftJoin("myothertable")));
    }
    
    @Test
    public void testPosition() {
        assertThat(sql("SELECT POSITION('3' IN '123456')")).isQueryWith(position(any(), literal("123456")));
    }
    
    @Test
    public void testNot() {
        assertThat(sql("SELECT NOT funcName('hello')")).isQueryWith(not(call("funcName", any())));
    }
    
    @Test
    public void testCast() {
        assertThat(sql("SELECT '5'::integer")).isQueryWith(select(cast(literal("5"), "integer")));
    }
    
    @Test
    public void testNullCast() {
        assertThat(sql("SELECT NULL::integer")).isQueryWith(select(nullCast("integer")));
    }
    
    @Test
    public void testJsonCast() {
        assertThat(sql("SELECT '{}'::jsonb")).isQueryWith(select(cast(literal("{}"), "jsonb")));
    }
    
    @Test
    public void testRowSelect() {
        assertThat(sql("SELECT 'E', (4, 'E')")).isQueryWith(select(row(literal(4), literal("E"))));
    }
    
    @Test
    public void testRowWhere() {
        assertThat(sql("SELECT * FROM t1 WHERE (col1, col2) = (1, 2)")).isQueryWith(where(equal(row(column("col1"), column("col2")), row(literal(1), literal(2)))));
    }
    
    @Test
    public void testAnyUuidCast() {
        assertThat(sql("SELECT * FROM t1 WHERE col1 = ANY('{}'::uuid[])")).isQueryWith();
    }
    
    @Test
    public void testLike() {
        assertThat(sql("SELECT * FROM t1 WHERE col1 LIKE '%Lorem'")).isQueryWith(where(like(column("col1"), "%Lorem")));
    }
    
    @Test
    public void testUUIDLiteral() {
        assertThat(sql("SELECT UUID 'c96ff414-8559-484c-bd43-c978130a5ee4'")).isQueryWith(uuidLiteral("c96ff414-8559-484c-bd43-c978130a5ee4"));
    }
    
    @Test
    public void testDateLiteral() {
        assertThat(sql("SELECT DATE '2010-10-10'")).isQueryWith(dateLiteral("2010-10-10"));
    }
    
    @Test
    public void testConds() {
        assertThat(sql("SELECT * FROM t1 WHERE col1 = 1")).isQueryWith(where(equal(column("col1"), literal(1))));
        assertThat(sql("SELECT * FROM t1 WHERE col1 > 1")).isQueryWith(where(greater(column("col1"), literal(1))));
        assertThat(sql("SELECT * FROM t1 WHERE col1 < 1")).isQueryWith(where(less(column("col1"), literal(1))));
        assertThat(sql("SELECT * FROM t1 WHERE col1 >= 1")).isQueryWith(where(greaterEqual(column("col1"), literal(1))));
        assertThat(sql("SELECT * FROM t1 WHERE col1 <= 1")).isQueryWith(where(lessEqual(column("col1"), literal(1))));
        assertThat(sql("SELECT * FROM t1 WHERE col1 <> 1")).isQueryWith(where(unequal(column("col1"), literal(1))));
    }
    
    @Test
    public void testAnd() {
        assertThat(sql("SELECT * FROM t1 WHERE col1 = 1 AND col2 = 2")).isQueryWith(where(and(equal(column("col1"), literal(1)), equal(column("col2"), literal(2)))));
        assertThat(sql("SELECT * FROM t1 WHERE col1 = 1 AND col2 = 2 AND col3 = 3")).isQueryWith(where(and(equal(column("col1"), literal(1)), equal(column("col2"), literal(2)), equal(column("col3"), literal(3)))));
    }
    
    @Test
    public void testOr() {
        assertThat(sql("SELECT * FROM t1 WHERE col1 = 1 OR col2 = 2")).isQueryWith(where(or(equal(column("col1"), literal(1)), equal(column("col2"), literal(2)))));
        assertThat(sql("SELECT * FROM t1 WHERE col1 = 1 OR col2 = 2 OR col3 = 3")).isQueryWith(where(or(equal(column("col1"), literal(1)), equal(column("col2"), literal(2)), equal(column("col3"), literal(3)))));
    }
    
    @Test
    public void testCompute() {
        assertThat(sql("SELECT * FROM t1 WHERE col1 = 1 + 2")).isQueryWith(where(equal(column("col1"), add(literal(1), literal(2)))));
        assertThat(sql("SELECT * FROM t1 WHERE col1 = (1 + 2) * 5")).isQueryWith(where(equal(column("col1"), mul(add(literal(1), literal(2)), literal(5)))));
    }

}
