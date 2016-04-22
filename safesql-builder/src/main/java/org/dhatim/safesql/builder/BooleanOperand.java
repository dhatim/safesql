package org.dhatim.safesql.builder;

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
    public void appendTo(SafeSqlBuilder builder) {
        if (not) {
            builder.appendConstant("NOT ");
        }
        builder.append(operand);
    }

    @Override
    public BooleanOperand negate() {
        return new BooleanOperand(operand, !not);
    }
    
}
