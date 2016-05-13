package org.dhatim.safesql.builder;

import org.dhatim.safesql.SafeSqlizable;

public interface Condition extends SafeSqlizable {
    
    
    Condition negate();
    
    public static Condition create(Operand left, ConditionalOperator operator, Operand right) {
        return new SimpleCondition(left, operator, right);
    }
    
    public static Condition and(Condition left, Condition right, Condition... others) {
        return ConditionGroup.create(LogicalOperator.AND, left, right, others);
    }
    
    public static Condition and(Condition left, Condition right) {
        return ConditionGroup.create(LogicalOperator.AND, left, right);
    }
    
    public static Condition or(Condition left, Condition right, Condition... others) {
        return ConditionGroup.create(LogicalOperator.OR, left, right, others);
    }
    
    public static Condition or(Condition left, Condition right) {
        return ConditionGroup.create(LogicalOperator.OR, left, right);
    }

    /**
     * Create a equals condition '='
     * 
     * @param left left operand
     * @param right right operand
     * @return new equals {@code Condition}
     * 
     * @see ComparisonOperator#EQ
     */
    public static Condition eq(Operand left, Operand right) {
        return new SimpleCondition(left, ComparisonOperator.EQ, right);
    }
    
    public static Condition ge(Operand left, Operand right) {
        return new SimpleCondition(left, ComparisonOperator.GE, right);
    }
    
    public static Condition le(Operand left, Operand right) {
        return new SimpleCondition(left, ComparisonOperator.LE, right);
    }

    public static Condition in(Operand left, Operand... values) {
        return new InCondition(left, false, values);
    }
    
    public static Condition notIn(Operand left, Operand... values) {
        return new InCondition(left, true, values);
    }
    
    public static Condition isNotNull(Operand operand) {
        return NullCondition.isNot(operand);
    }

    public static Condition isNull(Operand operand) {
        return NullCondition.is(operand);
    }

    public static Condition between(Operand left, Operand low, Operand high) {
        return new BetweenCondition(left, false, low, high);
    }
    
    public static Condition notBetween(Operand left, Operand low, Operand high) {
        return new BetweenCondition(left, true, low, high);
    }
    
}
