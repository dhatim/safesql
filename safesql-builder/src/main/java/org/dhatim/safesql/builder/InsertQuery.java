package org.dhatim.safesql.builder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.dhatim.safesql.SafeSqlAppendable;
import org.dhatim.safesql.SafeSqlizable;

public class InsertQuery implements SafeSqlizable {
    
    private final String schema;
    private final String tableName;
    
    private final List<String> columns;
    
    private final SqlQuery query;
    
    private final List<CommonTableExpression> ctes = new ArrayList<>();
    
    public InsertQuery(String schema, String tableName, List<String> columns, SqlQuery query) {
        this.schema = schema;
        this.tableName = tableName;
        this.columns = new ArrayList<>(columns);
        this.query = query;
    }
    
    public InsertQuery(String schema, String tableName, SqlQuery query, String...columns) {
        this(schema, tableName, Arrays.asList(columns), query);
    }
    
    @Override
    public void appendTo(SafeSqlAppendable builder) {
        if (!ctes.isEmpty()) {
            builder.append("WITH ");
            builder.joinedSqlizables(", ", ctes);
            builder.append(" ");
        }
        builder.append("INSERT INTO ");
        if (schema != null) {
            builder.identifier(schema).append('.');
        }
        builder.identifier(tableName).append(' ');
        builder.joinedSqlizables(", ", "(", ")", columns.stream().map(Identifier::new));
        builder.append(' ');
        query.appendTo(builder);
    }
    
    public InsertQuery with(String name, SqlQuery query) {
        ctes.add(new CommonTableExpression(name, query));
        return this;
    }
    
    public InsertQuery with(String name, List<String> columnNames, SqlQuery query) {
        ctes.add(new CommonTableExpression(name, columnNames, query));
        return this;
    }

}
