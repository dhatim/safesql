package org.dhatim.safesql.builder;

import java.util.List;
import org.dhatim.safesql.SafeSql;
import org.dhatim.safesql.SafeSqlBuilder;
import org.dhatim.safesql.SafeSqlizable;

public abstract class From extends AbstractHasJointure implements SafeSqlizable {
    
    private static class TableFrom extends From {
        
        private final String schema;
        private final String tableName;
        
        private TableFrom(String schema, String tableName, Alias alias) {
            super(alias);
            this.schema = schema;
            this.tableName = tableName;
        }
        
        @Override
        protected void render(SafeSqlBuilder sb) {
            if (schema != null) {
                sb.appendConstant(schema).appendConstant(".");
            }
            sb.appendIdentifier(tableName);
        }
        
    }
    
    private static class SubQueryFrom extends From {
        
        private final Query query;

        private SubQueryFrom(Query query, Alias alias) {
            super(alias);
            this.query = query;
        }
        
        @Override
        protected void render(SafeSqlBuilder sb) {
            sb.appendConstant('(');
            sb.append(query);
            sb.appendConstant(')');
        }
        
    }
    
    private final Alias alias;

    private From(Alias alias) {
        this.alias = alias;
    }

    public Alias getAlias() {
        return alias;
    }

    protected abstract void render(SafeSqlBuilder sb);

    @Override
    public SafeSql toSafeSql() {
        SafeSqlBuilder sb = new SafeSqlBuilder();
        render(sb);
        if (alias != null) {
            sb.appendConstant(" ").append(alias);
        }
        List<Jointure> jointures = getJointures();
        if (!jointures.isEmpty()) {
            sb.appendConstant(" ").appendJoined(" ", jointures);
        }
        return sb.toSafeSql();
    }
    
    public static From table(String schema, String tableName, Alias alias) {
        return new TableFrom(schema, tableName, alias);
    }
    
    public static From query(Query query, Alias alias) {
        return new SubQueryFrom(query, alias);
    }

}
