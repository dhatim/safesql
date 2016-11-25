package org.dhatim.safesql.builder;

import org.dhatim.safesql.SafeSql;
import org.dhatim.safesql.SafeSqlAppendable;
import org.dhatim.safesql.SafeSqlBuilder;

public class NamedOperand implements Operand {

    private final Alias alias;
    private final Operand operand;

    public NamedOperand(Operand operand, Alias alias) {
        this.operand = operand;
        this.alias = alias;
    }
    
    public Alias getAlias() {
        return alias;
    }
    
    public Operand getOperand() {
        return operand;
    }
    
    @Override
    public SafeSql toSafeSql() {
        return new SafeSqlBuilder()
                .append(operand)
                .append(" AS ")
                .append(alias)
                .toSafeSql();
    }
    
    @Override
    public void appendTo(SafeSqlAppendable builder) {
        builder.append(operand)
                .append(" AS ")
                .append(alias);
    }

}
