package org.dhatim.safesql.builder;

import java.util.ArrayList;
import org.dhatim.safesql.SafeSql;
import org.dhatim.safesql.SafeSqlBuilder;
import org.dhatim.safesql.SafeSqlUtils;
import org.dhatim.safesql.SafeSqlizable;

public abstract class Case<T extends SafeSqlizable> implements Operand {
    
    public static class ConditionalCase extends Case<Condition> {

        @Override
        protected SafeSql getCaseClause() {
            return SafeSqlUtils.fromConstant("CASE");
        }
        
    }
    
    public static class SimpleCase extends Case<Value> {
        
        private final Operand expression;

        public SimpleCase(Operand expression) {
            this.expression = expression;
        }

        @Override
        protected SafeSql getCaseClause() {
            return new SafeSqlBuilder().appendConstant("CASE ").append(expression).toSafeSql();
        }
        
    }
    
    private static class When<T extends SafeSqlizable> implements Operand {
        
        private final T expression;
        private final Operand result;
        
        public When(T expression, Operand result) {
            this.expression = expression;
            this.result = result;
        }

        @Override
        public SafeSql toSafeSql() {
            return new SafeSqlBuilder().appendConstant("WHEN ").append(expression).appendConstant(" THEN ").append(result).toSafeSql();
        }
    }
    
    private ArrayList<When<T>> whens = new ArrayList<>();
    private Operand elseOperand;

    private Case() {
    }

    public void add(T expression, Operand result) {
        whens.add(new When<T>(expression, result));
    }
    
    public void setElse(Operand operand) {
        this.elseOperand = operand;
    }
    
    protected abstract SafeSql getCaseClause();
    
    @Override
    public SafeSql toSafeSql() {
        SafeSqlBuilder sb = new SafeSqlBuilder();
        sb.append(getCaseClause()).appendConstant(" ");
        sb.appendJoined(" ", whens).appendConstant(" ");
        if (elseOperand != null) {
            sb.appendConstant("ELSE ").append(elseOperand).appendConstant(" ");
        }
        sb.appendConstant("END");
        return sb.toSafeSql();
    }
    
    public static ConditionalCase create(Condition condition, Operand result) {
        ConditionalCase clause = new ConditionalCase();
        clause.add(condition, result);
        return clause;
    }
    
    public static ConditionalCase create(Condition condition, Operand result, Operand elseOperand) {
        ConditionalCase clause = create(condition, result);
        clause.setElse(elseOperand);
        return clause;
    }
    
    public static SimpleCase create(Operand expression, Value value, Operand result) {
        SimpleCase clause = new SimpleCase(expression);
        clause.add(value, result);
        return clause;
    }
    
    public static SimpleCase create(Operand expression, Value value, Operand result, Operand elseOperand) {
        SimpleCase clause = create(expression, value, result);
        clause.setElse(elseOperand);
        return clause;
    }
    
    /*public static Condition create(Condition condition, Condition result, Condition elseCondition) {
        return () -> {
            return new SafeSqlBuilder()
                    .appendConstant("CASE WHEN ")
                    .append(condition)
                    .appendConstant(" THEN ")
                    .append(result)
                    .appendConstant(" ELSE ")
                    .append(elseCondition)
                    .append(" END")
                    .toSafeSql();
        };
    }*/

}
