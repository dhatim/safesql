package org.dhatim.safesql.builder;

import org.dhatim.safesql.SafeSqlBuilder;

public class Compute implements Operand {

    private final Operand left;
    private final Operand right;
    private final MathOperator operator;

    public Compute(Operand operand1, MathOperator op, Operand operand2) {
        this.left = operand1;
        this.operator = op;
        this.right = operand2;
    }

    public Operand getLeftOperand() {
        return left;
    }

    public Operand getRightOperand() {
        return right;
    }

    public MathOperator getOperator() {
        return operator;
    }

    @Override
    public void appendTo(SafeSqlBuilder builder) {
        builder.append("(")
            .append(left)
            .append(" ")
            .append(operator)
            .append(" ")
            .append(right)
            .append(")");
    }

    public static Compute add(Operand op1, Operand op2) {
        return new Compute(op1, MathOperator.ADD, op2);
    }

    public static Compute sub(Operand op1, Operand op2) {
        return new Compute(op1, MathOperator.SUB, op2);
    }

}
