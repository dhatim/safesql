package org.dhatim.safesql.builder;

import org.dhatim.safesql.SafeSql;
import org.dhatim.safesql.SafeSqlAppendable;
import org.dhatim.safesql.SafeSqlUtils;

public enum MathOperator implements Operator {
    ADD("+"),
    SUB("-"),
    MUL("*"),
    DIV("/"),
    MOD("%");
    
    private final String sql;

    private MathOperator(String sql) {
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
    public void appendTo(SafeSqlAppendable builder) {
        builder.append(sql);
    }
    
}
