package org.dhatim.safesql;

import static org.dhatim.safesql.assertion.Assertions.*;

import org.junit.Test;

public class SafeSqlJoinerTest {

    @Test
    public void testAdd() {
        SafeSqlJoiner joiner = new SafeSqlJoiner(SafeSql.constant(", "));
        joiner.add(SafeSql.constant("Lorem"));
        joiner.add(SafeSql.identifier("Ipsum"));
        joiner.add(SafeSql.parameter(5));
        joiner.add("dolor");
        joiner.addParameter(10);

        assertThat(joiner.toSafeSql()).hasSql("Lorem, \"Ipsum\", ?, dolor, ?").hasParameters(5, 10);
    }

    @Test
    public void testAddWithPrefixSuffix() {
        SafeSqlJoiner joiner = new SafeSqlJoiner(SafeSql.constant(", "), SafeSql.constant("<<"), SafeSql.constant(">>"));
        joiner.add(SafeSql.constant("Lorem"));
        joiner.add(SafeSql.identifier("Ipsum"));
        joiner.add(SafeSql.parameter(5));
        joiner.add("dolor");
        joiner.addParameter(10);

        assertThat(joiner.toSafeSql()).hasSql("<<Lorem, \"Ipsum\", ?, dolor, ?>>").hasParameters(5, 10);
    }

    @Test
    public void testMerge() {
        SafeSqlJoiner joinerOther = new SafeSqlJoiner(SafeSql.constant(", "));
        joinerOther.add("consectetur");
        joinerOther.addParameter(15);
        joinerOther.add("adipiscing");

        SafeSqlJoiner joiner = new SafeSqlJoiner(SafeSql.constant(", "));
        joiner.add(SafeSql.constant("Lorem"));
        joiner.add(SafeSql.identifier("Ipsum"));
        joiner.add(SafeSql.parameter(5));
        joiner.add("dolor");
        joiner.addParameter(10);
        joiner.merge(joinerOther);

        assertThat(joiner.toSafeSql()).hasSql("Lorem, \"Ipsum\", ?, dolor, ?, consectetur, ?, adipiscing").hasParameters(5, 10, 15);
    }

    @Test
    public void testMergeEmpty() {
        SafeSqlJoiner joinerOther = new SafeSqlJoiner(SafeSql.constant(", "));

        SafeSqlJoiner joiner = new SafeSqlJoiner(SafeSql.constant(", "));
        joiner.add(SafeSql.constant("Lorem"));
        joiner.add(SafeSql.identifier("Ipsum"));
        joiner.add(SafeSql.parameter(5));
        joiner.add("dolor");
        joiner.addParameter(10);
        joiner.merge(joinerOther);

        assertThat(joiner.toSafeSql()).hasSql("Lorem, \"Ipsum\", ?, dolor, ?").hasParameters(5, 10);
    }

    @Test
    public void testEmpty() {
        SafeSqlJoiner joiner = new SafeSqlJoiner(SafeSql.constant(", "), SafeSql.constant("<<"), SafeSql.constant(">>"));
        assertThat(joiner.toSafeSql()).hasSql("<<>>").hasEmptyParameters();
    }

    @Test
    public void testSetEmpty() {
        SafeSqlJoiner joiner = new SafeSqlJoiner(SafeSql.constant(", "));
        joiner.setEmptyValue(SafeSql.constant("EMPTY"));
        assertThat(joiner.toSafeSql()).hasSql("EMPTY").hasEmptyParameters();
    }


}
