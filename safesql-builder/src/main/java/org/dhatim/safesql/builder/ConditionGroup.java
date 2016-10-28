package org.dhatim.safesql.builder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import org.dhatim.safesql.SafeSql;
import org.dhatim.safesql.SafeSqlAppendable;
import org.dhatim.safesql.SafeSqlBuilder;

public class ConditionGroup implements Condition {
    
    private final List<Condition> conditions;
    private final LogicalOperator operator;
    
    ConditionGroup(LogicalOperator operator) {
        this.conditions = new ArrayList<>();
        this.operator = operator;
    }

    ConditionGroup(Collection<Condition> conditions, LogicalOperator operator) {
        if (conditions.size() < 2) {
            throw new IllegalArgumentException("Conditions array must be 2 elements or more");
        }
        this.conditions = new ArrayList<>(conditions);
        this.operator = operator;
    }

    @Override
    public void appendTo(SafeSqlAppendable builder) {
        SafeSql spacedOperator = new SafeSqlBuilder().append(' ').append(operator).append(' ').toSafeSql();
        builder.append("(")
                .joinSqlizables(spacedOperator, conditions)
                .append(")");
    }
    
    @Override
    public Condition negate() {
        return new ConditionGroup(conditions.stream().map(Condition::negate).collect(Collectors.toList()), operator == LogicalOperator.AND ? LogicalOperator.OR : LogicalOperator.AND);
    }
    
    public void add(Condition condition) {
        if (condition instanceof ConditionGroup && ((ConditionGroup) condition).operator == operator) {
            conditions.addAll(((ConditionGroup) condition).conditions);
        } else {
            conditions.add(condition);
        }
    }
    
    public static Condition create(LogicalOperator operator, Condition left, Condition right, Condition... others) {
        ConditionGroup group = new ConditionGroup(operator);
        group.add(left);
        group.add(right);
        return group;
    }

}
