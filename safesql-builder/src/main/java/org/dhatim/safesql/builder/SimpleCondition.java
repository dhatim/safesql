package org.dhatim.safesql.builder;

import java.util.HashMap;

public class SimpleCondition extends AbstractCondition<Operand, ConditionalOperator, Operand> {

    private static class ReversedOperators {

        private static final ReversedOperators INSTANCE = new ReversedOperators();

        private final HashMap<ConditionalOperator, ConditionalOperator> OPS = new HashMap<>();

        public ReversedOperators() {
            OPS.put(ComparisonOperator.EQ, ComparisonOperator.NE);
            OPS.put(ComparisonOperator.NE, ComparisonOperator.EQ);
            OPS.put(ComparisonOperator.GT, ComparisonOperator.LE);
            OPS.put(ComparisonOperator.LT, ComparisonOperator.GE);
            OPS.put(ComparisonOperator.GE, ComparisonOperator.LT);
            OPS.put(ComparisonOperator.LE, ComparisonOperator.GT);
            OPS.put(RelationalOperator.IN, RelationalOperator.NOT_IN);
            OPS.put(RelationalOperator.BETWEEN, RelationalOperator.NOT_BETWEEN);
        }

        public ConditionalOperator get(ConditionalOperator op) {
            return OPS.get(op);
        }

    }

    protected SimpleCondition(Operand left, ConditionalOperator operator, Operand right) {
        super(left, operator, right);
    }

    @Override
    public Condition negate() {
        return new SimpleCondition(getLeft(), ReversedOperators.INSTANCE.get(getOperator()), getRight());
    }

}
