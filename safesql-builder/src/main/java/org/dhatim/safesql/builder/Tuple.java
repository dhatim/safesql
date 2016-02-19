package org.dhatim.safesql.builder;

import java.util.ArrayList;
import java.util.Arrays;
import org.dhatim.safesql.SafeSql;
import org.dhatim.safesql.SafeSqlBuilder;

public class Tuple implements Operand {
    
    private final ArrayList<Operand> operands = new ArrayList<>();
    
    public Tuple(Operand operand1, Operand operand2, Operand... others) {
        operands.add(operand1);
        operands.add(operand2);
        operands.addAll(Arrays.asList(others));
    }

    @Override
    public SafeSql toSafeSql() {
        return new SafeSqlBuilder()
                .appendConstant("(")
                .appendJoined(", ", operands)
                .appendConstant(")")
                .toSafeSql();
    }

}
