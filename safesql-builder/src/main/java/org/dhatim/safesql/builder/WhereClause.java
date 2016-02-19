package org.dhatim.safesql.builder;

public interface WhereClause {

    WhereClause and(Condition condition);

    default WhereClause and(Operand operand, ComparisonOperator op, int value) {
        return and(Condition.create(operand, op, new Value(value)));
    }

    default WhereClause and(Operand operand, ComparisonOperator op, String value) {
        return and(Condition.create(operand, op, new Value(value)));
    }

    default WhereClause and(Operand operand, ComparisonOperator op, double value) {
        return and(Condition.create(operand, op, new Value(value)));
    }

    default WhereClause and(Operand operand, ComparisonOperator op, Object value) {
        return and(Condition.create(operand, op, new Value(value)));
    }

    default WhereClause and(Operand operand1, ComparisonOperator op, Operand operand2) {
        return and(Condition.create(operand1, op, operand2));
    }

    default WhereClause andIn(Operand operand, Operand... values) {
        return and(Condition.in(operand, values));
    }
    
    default WhereClause andNotIn(Operand operand, Operand...values) {
        return and(Condition.notIn(operand, values));
    }

    default WhereClause andIsNull(Column column) {
        return and(Condition.isNull(column));
    }

    default WhereClause andIsNotNull(Column column) {
        return and(Condition.isNotNull(column));
    }

}
