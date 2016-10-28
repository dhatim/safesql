package org.dhatim.safesql.builder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.dhatim.safesql.SafeSqlAppendable;
import org.dhatim.safesql.SafeSqlizable;

public abstract class From extends AbstractHasJointure implements SafeSqlizable {
    
    private static class TableFrom extends From {
        
        private final String schema;
        private final String tableName;
        
        private TableFrom(String schema, String tableName, Alias alias, List<String> columnAliases) {
            super(alias, columnAliases);
            this.schema = schema;
            this.tableName = tableName;
        }
        
        @Override
        protected void render(SafeSqlAppendable sb) {
            if (schema != null) {
                sb.appendIdentifier(schema).append(".");
            }
            sb.appendIdentifier(tableName);
        }
        
    }
    
    private static class SubQueryFrom extends From {
        
        private final SqlQuery query;

        private SubQueryFrom(SqlQuery query, Alias alias, List<String> columnAliases) {
            super(alias, columnAliases);
            this.query = query;
        }
        
        @Override
        protected void render(SafeSqlAppendable sb) {
            sb.append('(');
            sb.append(query);
            sb.append(')');
        }
        
    }
    
    private final Alias alias;
    private final List<String> columnAliases;

    private From(Alias alias, List<String> columnAliases) {
        this.alias = alias;
        this.columnAliases = new ArrayList<>(columnAliases);
    }

    public Alias getAlias() {
        return alias;
    }

    protected abstract void render(SafeSqlAppendable sb);

    @Override
    public void appendTo(SafeSqlAppendable builder) {
        render(builder);
        if (alias != null) {
            builder.append(" ").append(alias);
        }
        if (!columnAliases.isEmpty()) {
            builder.append(" ").appendJoinedSqlizable(", ", "(", ")", columnAliases.stream().map(Identifier::new));
        }
        List<Jointure> jointures = getJointures();
        if (!jointures.isEmpty()) {
            builder.append(" ").appendJoinedSqlizable(" ", jointures);
        }
    }
    
    public static From table(String schema, String tableName, Alias alias) {
        return new TableFrom(schema, tableName, alias, Collections.emptyList());
    }
    
    public static From table(String schema, String tableName, Alias alias, List<String> columnAliases) {
        return new TableFrom(schema, tableName, alias, columnAliases);
    }
    
    public static From query(SqlQuery query, Alias alias) {
        return new SubQueryFrom(query, alias, Collections.emptyList());
    }
    
    public static From query(SqlQuery query, Alias alias, List<String> columnAliases) {
        return new SubQueryFrom(query, alias, columnAliases);
    }

}
