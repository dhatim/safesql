package org.dhatim.safesql.builder;

import org.dhatim.safesql.SafeSqlAppendable;

public class NullCondition implements Condition {

    private Operand operand;
    private boolean nullTest;

    private NullCondition(Operand operand, boolean nullTest) {
        this.operand = operand;
        this.nullTest = nullTest;
    }
    
    @Override
    public void appendTo(SafeSqlAppendable sb) {
        sb.append(operand).append(" IS " );
        if (!nullTest) {
            sb.append("NOT ");
        }
        sb.append("NULL");
    }
    
    @Override
    public Condition negate() {
        return nullTest ? isNot(operand) : is(operand);
    }
    
    public static NullCondition is(Operand operand) {
        return new NullCondition(operand, true);
    }
    
    public static NullCondition isNot(Operand operand) {
        return new NullCondition(operand, false);
    }

}
