package org.dhatim.safesql.builder;

import org.dhatim.safesql.SafeSqlBuilder;
import org.dhatim.safesql.SafeSqlizable;

public class CommonTableExpression implements SafeSqlizable {

    private final String name;
    private final SqlQuery query;

    public CommonTableExpression(String name, SqlQuery query) {
        this.name = name;
        this.query = query;
    }
    
    public String getName() {
        return name;
    }
    
    @Override
    public void appendTo(SafeSqlBuilder builder) {
        builder.appendIdentifier(name)
                .append(" AS (")
                .append(query)
                .append(")");
    }
    
}
