package org.dhatim.safesql.builder;

import org.dhatim.safesql.SafeSql;
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
    public SafeSql toSafeSql() {
        return new SafeSqlBuilder()
                .appendIdentifier(name)
                .appendConstant(" AS (")
                .append(query)
                .appendConstant(")")
                .toSafeSql();
    }
    
}
