package org.dhatim.safesql.builder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.dhatim.safesql.SafeSqlAppendable;
import org.dhatim.safesql.SafeSqlizable;

public class CommonTableExpression implements SafeSqlizable {

    private final String name;
    private final SqlQuery query;
    private final List<String> columnNames;

    public CommonTableExpression(String name, SqlQuery query) {
        this(name, Collections.emptyList(), query);
    }
    
    public CommonTableExpression(String name, List<String> columnNames, SqlQuery query) {
        this.name = name;
        this.query = query;
        this.columnNames = new ArrayList<>(columnNames);
    }
    
    public String getName() {
        return name;
    }
    
    @Override
    public void appendTo(SafeSqlAppendable builder) {
        builder.appendIdentifier(name);
        if (!columnNames.isEmpty()) {
            builder.appendJoined(", ", "(", ")", columnNames.stream().map(Identifier::new));
        }
        builder.append(" AS (")
                .append(query)
                .append(")");
    }
    
}
