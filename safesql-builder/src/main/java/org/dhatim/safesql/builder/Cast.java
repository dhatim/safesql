package org.dhatim.safesql.builder;

import org.dhatim.safesql.SafeSql;
import org.dhatim.safesql.SafeSqlBuilder;

public class Cast implements Operand {

    private final Operand operand;
    private final String sqlType;

    public Cast(Operand operand, String sqlType) {
        this.operand = operand;
        this.sqlType = sqlType;
    }

    @Override
    public SafeSql toSafeSql() {
        return new SafeSqlBuilder()
                .append(operand)
                .appendConstant("::")
                .appendConstant(sqlType)
                .toSafeSql();
    }

    public Operand getOperand() {
        return operand;
    }
    
    public String getSqlType() {
        return sqlType;
    }
    
    @Override
    public String toString() {
        return "Cast{" + operand + "::" + sqlType + "}";
    }

}
