package org.dhatim.safesql.builder;

import org.dhatim.safesql.SafeSqlAppendable;

public class QueryOperand implements Operand {
    
    private final SelectQuery query;

    public QueryOperand(SelectQuery query) {
        this.query = query;
    }

    @Override
    public void appendTo(SafeSqlAppendable builder) {
        builder.append('(');
        query.appendTo(builder);
        builder.append(')');
    }

}
