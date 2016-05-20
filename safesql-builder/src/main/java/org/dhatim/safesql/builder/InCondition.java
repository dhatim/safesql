package org.dhatim.safesql.builder;

import java.util.List;

public class InCondition extends AbstractCondition<Operand, RelationalOperator, Row> {

    InCondition(Operand left, boolean notIn, List<Operand> elements) {
        super(left, notIn ? RelationalOperator.NOT_IN : RelationalOperator.IN, new Row(elements));
    }
    
    @Override
    public Condition negate() {
        return new InCondition(getLeft(), getOperator() == RelationalOperator.IN, getRight().getElements());
    }
    
}
