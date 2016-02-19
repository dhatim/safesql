package org.dhatim.safesql.builder;

import org.dhatim.safesql.SafeSql;
import org.dhatim.safesql.SafeSqlBuilder;

public class NullCondition implements Condition {

    private Operand operand;
    private boolean nullTest;

    private NullCondition(Operand operand, boolean nullTest) {
        this.operand = operand;
        this.nullTest = nullTest;
    }
    
    @Override
    public SafeSql toSafeSql() {
        SafeSqlBuilder sb = new SafeSqlBuilder().append(operand).appendConstant(" IS " );
        if (!nullTest) {
            sb.appendConstant("NOT ");
        }
        sb.appendConstant("NULL");
        return sb.toSafeSql();
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
