package org.dhatim.safesql.builder;

import org.dhatim.safesql.SafeSql;
import org.dhatim.safesql.SafeSqlBuilder;

public class BetweenCondition extends AbstractCondition<Operand, RelationalOperator, LowHighOperand> {
    
    BetweenCondition(Operand left, boolean notBetween, Operand low, Operand high) {
        super(left, notBetween ? RelationalOperator.NOT_BETWEEN : RelationalOperator.BETWEEN, new LowHighOperand(low, high));
    }
    
    @Override
    public Condition negate() {
        return new BetweenCondition(getLeft(), getOperator() == RelationalOperator.NOT_BETWEEN, getRight().low, getRight().high);
    }

}

class LowHighOperand implements Operand {

    final Operand low;
    final Operand high;
    
    public LowHighOperand(Operand low, Operand high) {
        this.low = low;
        this.high = high;
    }
    
    @Override
    public SafeSql toSafeSql() {
        SafeSqlBuilder sb = new SafeSqlBuilder();
        sb.append(low).appendConstant(" AND ").append(high);
        return sb.toSafeSql();
    }
    
}