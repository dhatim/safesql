package org.dhatim.safesql.builder;

import java.util.Arrays;
import org.dhatim.safesql.SafeSql;
import org.dhatim.safesql.SafeSqlBuilder;

public class InCondition extends AbstractCondition<Operand, RelationalOperator, Values> {

    InCondition(Operand left, boolean notIn, Operand[] elements) {
        super(left, notIn ? RelationalOperator.NOT_IN : RelationalOperator.IN, new Values(elements));
    }
    
    @Override
    public Condition negate() {
        return new InCondition(getLeft(), getOperator() == RelationalOperator.IN, getRight().elements);
    }
    
}

class Values implements Operand {

    final Operand[] elements;

    public Values(Operand[] elements) {
        this.elements = elements.clone();
    }

    @Override
    public SafeSql toSafeSql() {
        SafeSqlBuilder sb = new SafeSqlBuilder();
        sb.appendConstant("(").appendJoined(", ", Arrays.asList(elements)).appendConstant(")");
        return sb.toSafeSql();
    }

}
