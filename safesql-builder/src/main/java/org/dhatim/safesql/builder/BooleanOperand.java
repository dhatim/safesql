package org.dhatim.safesql.builder;

import org.dhatim.safesql.SafeSqlBuilder;

public class BooleanOperand implements Operand, Condition {

    private final boolean not;
    private final Expression expression;

    public BooleanOperand(Operand operand) {
        this(operand, false);
    }
    
    public BooleanOperand(Condition condition) {
        this(condition, false);
    }
    
    protected BooleanOperand(Expression expression, boolean not) {
        this.not = not;
        this.expression = expression;
    }
    
    protected Expression getExpression() {
        return expression;
    }
    
    protected boolean isNegative() {
        return not;
    }
    
    @Override
    public void appendTo(SafeSqlBuilder builder) {
        if (not) {
            builder.append("NOT ");
        }
        builder.append(expression);
    }

    @Override
    public BooleanOperand negate() {
        return new BooleanOperand(expression, !not);
    }
    
}
