package org.dhatim.safesql.builder;

import org.dhatim.safesql.SafeSql;
import org.dhatim.safesql.SafeSqlBuilder;
import org.dhatim.safesql.SafeSqlUtils;
import org.dhatim.safesql.SafeSqlizable;

public enum UnaryOperator implements SafeSqlizable {
    POSITIVE("+"),
    NEGATIVE("-");
    
    private final String sql;

    private UnaryOperator(String sql) {
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
