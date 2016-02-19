package org.dhatim.safesql.builder;

import org.dhatim.safesql.SafeSql;
import org.dhatim.safesql.SafeSqlBuilder;

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
    public SafeSql toSafeSql() {
        return new SafeSqlBuilder().append(operator).appendConstant("(").append(operand).appendConstant(")").toSafeSql();
    }

}
