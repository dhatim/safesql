package org.dhatim.safesql.builder;

import org.dhatim.safesql.SafeSqlBuilder;

public abstract class AbstractCondition<L extends Operand, O extends ConditionalOperator, R extends Operand> implements Condition {

    private L left;
    private O operator;
    private R right;

    protected AbstractCondition(L left, O operator, R right) {
        this.left = left;
        this.operator = operator;
        this.right = right;
        
    }
    
    @Override
    public void appendTo(SafeSqlBuilder builder) {
        //.appendConstant("(")
        builder.append(left)
        .appendConstant(" ")
        .append(operator)
        .appendConstant(" ")
        .append(right);
        //.appendConstant(")")
    }
    
    protected L getLeft() {
        return left;
    }
    
    protected O getOperator() {
        return operator;
    }
    
    protected R getRight() {
        return right;
    }
    
}
