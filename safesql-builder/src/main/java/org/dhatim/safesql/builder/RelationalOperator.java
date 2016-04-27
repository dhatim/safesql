package org.dhatim.safesql.builder;

import org.dhatim.safesql.SafeSql;
import org.dhatim.safesql.SafeSqlBuilder;
import org.dhatim.safesql.SafeSqlUtils;

public enum RelationalOperator implements ConditionalOperator { 
    IN("IN"), 
    NOT_IN("NOT IN"),
    BETWEEN("BETWEEN"),
    NOT_BETWEEN("NOT BETWEEN");

    private String sql;

    private RelationalOperator(String sql) {
        this.sql = sql;
    }

    @Override
    public String toString() {
        return sql;
    }

    @Override
    public SafeSql toSafeSql() {
        return SafeSqlUtils.fromConstant(sql);
    }
    
    @Override
    public void appendTo(SafeSqlBuilder builder) {
        builder.append(sql);
    }
}
