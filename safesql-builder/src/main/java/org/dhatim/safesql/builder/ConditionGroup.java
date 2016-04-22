package org.dhatim.safesql.builder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;
import org.dhatim.safesql.SafeSql;
import org.dhatim.safesql.SafeSqlBuilder;

public class ConditionGroup implements Condition {
    
    private final Collection<Condition> conditions;
    private final LogicalOperator operator;

    ConditionGroup(Collection<Condition> conditions, LogicalOperator operator) {
        if (conditions.size() < 2) {
            throw new IllegalArgumentException("Conditions array must be 2 elements or more");
        }
        this.conditions = new ArrayList<>(conditions);
        this.operator = operator;
    }

    @Override
    public void appendTo(SafeSqlBuilder builder) {
        SafeSql spacedOperator = new SafeSqlBuilder().appendConstant(' ').append(operator).appendConstant(' ').toSafeSql();
        builder.appendConstant("(")
                .appendJoined(spacedOperator, conditions)
                .appendConstant(")");
    }
    
    @Override
    public Condition negate() {
        return new ConditionGroup(conditions.stream().map(Condition::negate).collect(Collectors.toList()), operator == LogicalOperator.AND ? LogicalOperator.OR : LogicalOperator.AND);
    }
    
    public static Condition create(LogicalOperator operator, Condition left, Condition right, Condition... others) {
        ArrayList<Condition> conditions = new ArrayList<>();
        conditions.add(left);
        conditions.add(right);
        conditions.addAll(Arrays.asList(others));
        return new ConditionGroup(conditions, operator);
    }

}
