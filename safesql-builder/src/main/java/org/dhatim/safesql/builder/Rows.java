package org.dhatim.safesql.builder;

import java.util.Arrays;
import org.dhatim.safesql.SafeSql;
import org.dhatim.safesql.SafeSqlBuilder;

public class Rows implements Operand {

    private final Operand[] items;

    public Rows(Operand... items) {
        this.items = items.clone();
    }

    @Override
    public SafeSql toSafeSql() {
        SafeSqlBuilder sb = new SafeSqlBuilder();
        sb.appendConstant("(").appendJoined(", ", Arrays.asList(items)).appendConstant(")");
        return sb.toSafeSql();
    }

}
