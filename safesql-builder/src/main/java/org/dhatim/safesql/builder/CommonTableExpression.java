package org.dhatim.safesql.builder;

import org.dhatim.safesql.SafeSqlBuilder;
import org.dhatim.safesql.SafeSqlizable;

public class CommonTableExpression implements SafeSqlizable {

    private String name;
    private Query query;

    public CommonTableExpression(String name, Query query) {
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
