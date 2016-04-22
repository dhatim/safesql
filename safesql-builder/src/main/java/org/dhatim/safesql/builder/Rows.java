package org.dhatim.safesql.builder;

import java.util.Arrays;
import org.dhatim.safesql.SafeSqlBuilder;

public class Rows implements Operand {

    private final Operand[] items;

    public Rows(Operand... items) {
        this.items = items.clone();
    }

    @Override
    public void appendTo(SafeSqlBuilder builder) {
        builder.appendConstant("(").appendJoined(", ", Arrays.asList(items)).appendConstant(")");
    }

}
