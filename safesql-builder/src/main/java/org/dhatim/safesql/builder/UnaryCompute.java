package org.dhatim.safesql.builder;

import org.dhatim.safesql.SafeSqlAppendable;

public class UnaryCompute implements Operand {

    private final UnaryOperator operator;
    private final Operand operand;

    public UnaryCompute(UnaryOperator operator, Operand operand) {
        this.operator = operator;
        this.operand = operand;
    }
    
    public Operand getOperand() {
        return operand;
    }
    
    public UnaryOperator getOperator() {
        return operator;
    }
    
    @Override
    public void appendTo(SafeSqlAppendable builder) {
        builder.append(operator).append("(").append(operand).append(")");
    }

}
