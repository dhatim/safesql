package org.dhatim.safesql.builder;

import org.dhatim.safesql.SafeSqlBuilder;

public class BetweenCondition extends AbstractCondition<Operand, RelationalOperator, LowHighOperand> {
    
    BetweenCondition(Operand left, boolean notBetween, Operand low, Operand high) {
        super(left, notBetween ? RelationalOperator.NOT_BETWEEN : RelationalOperator.BETWEEN, new LowHighOperand(low, high));
    }
    
    @Override
    public Condition negate() {
        return new BetweenCondition(getLeft(), getOperator() == RelationalOperator.NOT_BETWEEN, getRight().getLow(), getRight().getHigh());
    }

}

class LowHighOperand implements Operand {

    private final Operand low;
    private final Operand high;
    
    public LowHighOperand(Operand low, Operand high) {
        this.low = low;
        this.high = high;
    }
    
    @Override
    public void appendTo(SafeSqlBuilder builder) {
        builder.append(low).append(" AND ").append(high);
    }
    
    public Operand getHigh() {
        return high;
    }
    
    public Operand getLow() {
        return low;
    }
    
}