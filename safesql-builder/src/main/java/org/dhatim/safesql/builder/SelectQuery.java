package org.dhatim.safesql.builder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.dhatim.safesql.SafeSqlBuilder;

public class SelectQuery implements WhereClause, SqlQuery {
    
    private final BuilderContext context;
    
    private final List<CommonTableExpression> ctes = new ArrayList<>();

    private final List<Operand> selects = new ArrayList<>();
    private final List<From> froms = new ArrayList<>();

    private final List<Condition> conditions = new ArrayList<>();
    
    private final List<Operand> groupBy = new ArrayList<>();
    
    private final List<Condition> havings = new ArrayList<>();
    
    private final List<NamedWindow> windows = new ArrayList<>();
    
    private boolean distinct;
    
    public SelectQuery() {
         this(new BuilderContext());
    }
    
    public SelectQuery(SelectQuery other) {
        this(other.context, other.ctes, other.selects, other.froms, other.conditions, other.havings, other.groupBy, other.windows, other.distinct);
    }
    
    private SelectQuery(BuilderContext context) {
        this.context = context;
    }
    
    private SelectQuery(BuilderContext context, List<CommonTableExpression> ctes, List<Operand> selects, List<From> froms, List<Condition> conditions, 
            List<Condition> havings, List<Operand> groupBy, List<NamedWindow> windows, boolean distinct) {
        this(new BuilderContext(context));
        this.ctes.addAll(ctes);
        this.selects.addAll(selects);
        this.froms.addAll(froms);
        this.conditions.addAll(conditions);
        this.havings.addAll(havings);
        this.groupBy.addAll(groupBy);
        this.distinct = distinct;
        this.windows.addAll(windows);
    }

    public SelectQuery select(Operand operand) {
        selects.add(operand);
        return this;
    }
    
    public SelectQuery with(String name, SqlQuery query) {
        ctes.add(new CommonTableExpression(name, query));
        return this;
    }
    
    public SelectQuery select(String columnName) {
        return select(new Column(columnName));
    }

    public SelectQuery select(Alias alias, String columnName) {
        return select(new Column(alias, columnName));
    }

    public SelectQuery select(String columnName, From from) {
        return select(new Column(from.getAlias(), columnName));
    }
    
    public SelectQuery select(Operand operand, Alias alias) {
        return select(new NamedOperand(operand, alias));
    }

    public From from(String tableName) {
        return from(null, tableName, null);
    }

    public From from(String tableName, Alias alias) {
        return from(null, tableName, alias);
    }

    public From from(String schema, String tableName) {
        return from(schema, tableName, null);
    }

    public From from(String schema, String tableName, Alias alias) {
        return from(From.table(schema, tableName, alias));
    }
    
    public From from(SqlQuery query, Alias alias) {
        return from(From.query(query, alias));
    }

    private From from(From from) {
        froms.add(from);
        return from;
    }
    
    public SelectQuery groupBy(Column... columns) {
        groupBy.addAll(Arrays.asList(columns));
        return this;
    }
    
    public SelectQuery groupBy(Column column) {
        groupBy.add(column);
        return this;
    }

    public SelectQuery having(Condition condition) {
        having().and(condition);
        return this;
    }

    public Having having() {
        return new Having() {
            @Override
            public Having and(Condition condition) {
                havings.add(condition);
                return this;
            }
        };
    }
    
    public SelectQuery windows(NamedWindow... namedWindows) {
        windows.addAll(Arrays.asList(namedWindows));
        return this;
    }
    
    public SelectQuery window(NamedWindow window) {
        windows.add(window);
        return this;
    }
    
    public Alias generate() {
        return generate("_1");
    }

    public Alias generate(String suggestion) {
        return context.generate(suggestion);
    }
    
    public String generateIdentifier() {
        return generateIdentifier("_ident1");
    }
    
    public String generateIdentifier(String suggestion) {
        return context.generateIdentifier(suggestion);
    }

    @Override
    public void appendTo(SafeSqlBuilder sb) {
        if (!ctes.isEmpty()) {
            sb.append("WITH ");
            sb.appendJoined(", ", ctes);
            sb.append(" ");
        }
        sb.append("SELECT ");
        if (distinct) {
            sb.append("DISTINCT ");
        }
        sb.appendJoined(", ", selects);
        if (!froms.isEmpty()) {
            sb.append(" FROM ").appendJoined(", ", froms);
        }
        if (!conditions.isEmpty()) {
            sb.append(" WHERE ").appendJoined(" AND ", conditions);
        }
        if (!groupBy.isEmpty()) {
            sb.append(" GROUP BY ").appendJoined(", ", groupBy);
        }
        if (!havings.isEmpty()) {
            sb.append(" HAVING ").appendJoined(" AND ", havings);
        }
        if (!windows.isEmpty()) {
            sb.append(" WINDOW ").appendJoined(", ", windows);
        }
    }

    @Override
    public SelectQuery and(Condition condition) {
        conditions.add(condition);
        return this;
    }
    
    public SelectQuery distinct() {
        this.distinct = true;
        return this;
    }
    
    public static SelectQuery withContextOf(SelectQuery other) {
        return new SelectQuery(other.context);
    }

}
