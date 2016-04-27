package org.dhatim.safesql.builder;

import org.dhatim.safesql.SafeSqlBuilder;
import org.dhatim.safesql.SafeSqlizable;

public class BooleanOperand implements Operand, Condition {

    private final boolean not;
    private final SafeSqlizable expression;

    public BooleanOperand(Operand operand) {
        this(operand, false);
    }
    
    public BooleanOperand(Condition condition) {
        this(condition, false);
    }
    
    private BooleanOperand(SafeSqlizable expression, boolean not) {
        this.not = not;
        this.expression = expression;
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
