package org.dhatim.safesql.builder;

import org.dhatim.safesql.SafeSql;
import org.dhatim.safesql.SafeSqlBuilder;

public class BooleanColumn extends Column implements Condition {

    private final boolean not;

    public BooleanColumn(Alias alias, String name) {
        this(alias, name, false);
    }
    
    public BooleanColumn(Alias alias, String name, boolean not) {
        super(alias, name);
        this.not = not;
    }
    
    @Override
    public SafeSql toSafeSql() {
        if (not) {
            return new SafeSqlBuilder().appendConstant("NOT ").append(super.toSafeSql()).toSafeSql();
        } else {
            return super.toSafeSql();
        }
    }

    @Override
    public Condition negate() {
        return new BooleanColumn(getAlias(), getName(), !not);
    }

}