package org.dhatim.safesql.builder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import org.dhatim.safesql.SafeSqlBuilder;

public class Concat implements Operand {

    private final ArrayList<Operand> list = new ArrayList<>();

    public Concat(Collection<? extends Operand> c) {
        if (c.size() < 2) {
            throw new IllegalArgumentException("A concat must have a least two element");
        }
        list.addAll(c);
    }

    public Concat(Operand left, Operand right) {
        list.add(left);
        list.add(right);
    }

    public Concat(Operand left, Operand right, Operand... others) {
        this(left, right);
        list.addAll(Arrays.asList(others));
    }

    @Override
    public void appendTo(SafeSqlBuilder builder) {
        builder.joinedSqlizables(" || ", list);
    }

}

