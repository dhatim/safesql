package org.dhatim.safesql.builder;

import org.dhatim.safesql.SafeSql;
import org.dhatim.safesql.SafeSqlBuilder;

public class BooleanOperand implements Operand, Condition {

    private final boolean not;
    private final Operand operand;

    public BooleanOperand(Operand operand) {
        this(operand, false);
    }
    
    public BooleanOperand(Operand operand, boolean not) {
        this.not = not;
        this.operand = operand;
    }
    
    @Override
    public SafeSql toSafeSql() {
        return (not ? new SafeSqlBuilder().appendConstant("NOT ").append(operand) : operand).toSafeSql();
    }

    @Override
    public BooleanOperand negate() {
        return new BooleanOperand(operand, !not);
    }
    
}
