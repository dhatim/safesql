package org.dhatim.safesql.builder;

import org.dhatim.safesql.SafeSql;
import org.dhatim.safesql.SafeSqlBuilder;
import org.dhatim.safesql.SafeSqlUtils;

public enum LogicalOperator implements ConditionalOperator {
    AND("AND"), 
    OR("OR");

    private String sql;

    private LogicalOperator(String sql) {
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
        builder.appendConstant(sql);
    }
}
