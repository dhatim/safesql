package org.dhatim.safesql.builder;

import static org.dhatim.safesql.testing.Assertions.*;
import static org.dhatim.safesql.builder.Value.*;

import org.junit.Test;

public class SelectQueryTest {
    
    @Test
    public void testSelectQuery() {
        SelectQuery qb = new SelectQuery();
        qb.select(new Call("time"));
        
        assertThat(qb.toSafeSql()).hasSql("SELECT time()").hasEmptyParameters();
    }
    
    @Test
    public void testWithoutWhereQuery() {
        SelectQuery qb = new SelectQuery();
        qb.select("id").from("table_name");
        
        assertThat(qb.toSafeSql()).hasSql("SELECT id FROM table_name").hasEmptyParameters();
    }

    @Test
    public void testSimpleQuery() {
        SelectQuery qb = new SelectQuery();
        qb.select("id").from("table_name");
        qb.and(Condition.eq(new Column("name"), new Value("Lucie")));
        
        assertThat(qb.toSafeSql()).hasSql("SELECT id FROM table_name WHERE name = ?").hasParameters("Lucie");
    }
    
    @Test
    public void testInListQuery() {
        SelectQuery qb = new SelectQuery();
        qb.select("id").from("table_name");
        qb.and(Condition.in(new Column("name"), of("Lucie"), of("Clemence"), of("Anna")));
        
        assertThat(qb.toSafeSql()).hasSql("SELECT id FROM table_name WHERE name IN (?, ?, ?)").hasParameters("Lucie", "Clemence", "Anna");
    }
    
    @Test
    public void testNotInValueListQuery() {
        SelectQuery qb = new SelectQuery();
        qb.select("id").from("table_name");
        qb.and(Condition.notIn(new Column("name"), new Value("Lucie"), new Value("Clemence"), new Value("Anna")));
        
        assertThat(qb.toSafeSql()).hasSql("SELECT id FROM table_name WHERE name NOT IN (?, ?, ?)").hasParameters("Lucie", "Clemence", "Anna");
    }
    
    @Test
    public void testAliasQuery() {
        SelectQuery qb = new SelectQuery();
        Alias alias = qb.generate("t1");
        qb.select(alias, "id").from("table_name", alias);
        qb.and(Condition.eq(new Column(alias, "name"), new Value("Lucie")));
        
        assertThat(qb.toSafeSql()).hasSql("SELECT t1.id FROM table_name t1 WHERE t1.name = ?").hasParameters("Lucie");
    }
    
    @Test
    public void testAliasDoubleQuery() {
        SelectQuery qb = new SelectQuery();
        Alias alias = qb.generate("t1");
        Alias alias2 = qb.generate("t1");
        qb.select(alias, "id").from("table_name", alias);
        qb.from("other_table", alias2);
        qb.and(Condition.eq(new Column(alias, "name"), new Value("Lucie")));
        
        assertThat(qb.toSafeSql()).hasSql("SELECT t1.id FROM table_name t1, other_table t1_1 WHERE t1.name = ?").hasParameters("Lucie");
    }
    
    @Test
    public void testAliasTripleQuery() {
        SelectQuery qb = new SelectQuery();
        Alias alias = qb.generate("t1");
        Alias alias2 = qb.generate("t1");
        Alias alias3 = qb.generate("t1");
        qb.select(alias, "id").from("table_name", alias);
        qb.from("other_table", alias2);
        qb.from("other_other_table", alias3);
        qb.and(Condition.eq(new Column(alias, "name"), new Value("Lucie")));
        
        assertThat(qb.toSafeSql()).hasSql("SELECT t1.id FROM table_name t1, other_table t1_1, other_other_table t1_2 WHERE t1.name = ?").hasParameters("Lucie");
    }
    
    @Test
    public void testInnerJoinQuery() {
        SelectQuery qb = new SelectQuery();
        qb.select("id").from("table_name").innerJoin("other").and(Condition.eq(new Column("id"), new Column("oid")));
        qb.and(Condition.eq(new Column("name"), new Value("Lucie")));
        
        assertThat(qb.toSafeSql()).hasSql("SELECT id FROM table_name INNER JOIN other ON id = oid WHERE name = ?").hasParameters("Lucie");
    }
    
    @Test
    public void testLeftJoinWithSchemaQuery() {
        SelectQuery qb = new SelectQuery();
        qb.select("id").from("public", "table_name").leftJoin("schema2", "other").and(Condition.eq(new Column("id"), new Column("oid")));
        qb.and(Condition.eq(new Column("name"), new Value("Lucie")));
        
        assertThat(qb.toSafeSql()).hasSql("SELECT id FROM public.table_name LEFT JOIN schema2.other ON id = oid WHERE name = ?").hasParameters("Lucie");
    }
    
    @Test
    public void testLeftJoinWithInnerJoinQuery() {
        SelectQuery qb = new SelectQuery();
        Jointure j = qb.select("id").from("table_name").leftJoin("other").and(Condition.eq(new Column("id"), new Column("oid")));
        j.innerJoin("triple").and(Condition.eq(new Column("oid"), new Column("tid")));
        qb.and(Condition.eq(new Column("name"), new Value("Lucie")));
        
        assertThat(qb.toSafeSql()).hasSql("SELECT id FROM table_name LEFT JOIN (other INNER JOIN triple ON oid = tid) ON id = oid WHERE name = ?").hasParameters("Lucie");
    }
    
    @Test
    public void testGroupByQuery() {
        SelectQuery qb = new SelectQuery();
        qb.select("id").from("table_name");
        qb.and(Condition.eq(new Column("name"), new Value("Lucie")));
        qb.groupBy(new Column("col1"), new Column("col2"));
        
        assertThat(qb.toSafeSql()).hasSql("SELECT id FROM table_name WHERE name = ? GROUP BY col1, col2").hasParameters("Lucie");
    }
    
    @Test
    public void testComputeQuery() {
        SelectQuery qb = new SelectQuery();
        qb.select(Compute.add(new Constant(2), new Constant(2)));
        
        assertThat(qb.toSafeSql()).hasSql("SELECT (2 + 2)").hasEmptyParameters();
    }
    
    @Test
    public void testCast() {
        SelectQuery qb = new SelectQuery();
        qb.select(new Cast(new Constant(2), "text"));
        
        assertThat(qb.toSafeSql()).hasSql("SELECT 2::text").hasEmptyParameters();
    }
    
}
