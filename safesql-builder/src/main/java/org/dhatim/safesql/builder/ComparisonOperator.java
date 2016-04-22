package org.dhatim.safesql.builder;

import org.dhatim.safesql.SafeSql;
import org.dhatim.safesql.SafeSqlBuilder;
import org.dhatim.safesql.SafeSqlUtils;

public enum ComparisonOperator implements ConditionalOperator {
    EQ("="), 
    NE("<>"), 
    GT(">"), 
    GE(">="), 
    LT("<"), 
    LE("<="),
    LIKE("LIKE"),
    NLIKE("NOT LIKE"),
    ILIKE("ILIKE"),
    NILIKE("NOT ILIKE");

    private String sql;

    private ComparisonOperator(String sql) {
        this.sql = sql;
    }

    @Override
    public String toString() {
        return sql;
    }
    
    @Override
    public void appendTo(SafeSqlBuilder builder) {
        builder.appendConstant(sql);
    }

    @Override
    public SafeSql toSafeSql() {
        return SafeSqlUtils.fromConstant(sql);
    }

}
