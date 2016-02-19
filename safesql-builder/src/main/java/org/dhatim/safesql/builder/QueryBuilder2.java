package org.dhatim.safesql.builder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.dhatim.safesql.SafeSql;
import org.dhatim.safesql.SafeSqlBuilder;

public class QueryBuilder2 implements WhereClause, Query {
    
    private final QueryBuilderContext context;
    
    private final List<CommonTableExpression> ctes = new ArrayList<>();

    private final List<Operand> selects = new ArrayList<>();
    private final List<From> froms = new ArrayList<>();

    private final List<Condition> conditions = new ArrayList<>();
    
    private final List<Operand> groupBy = new ArrayList<>();
    
    private final List<Condition> havings = new ArrayList<>();
    
    private final List<NamedWindow> windows = new ArrayList<>();
    
    private boolean distinct;
    
    public QueryBuilder2() {
         this(new QueryBuilderContext());
    }
    
    public QueryBuilder2(QueryBuilder2 other) {
        this(other.context, other.ctes, other.selects, other.froms, other.conditions, other.havings, other.groupBy, other.windows, other.distinct);
    }
    
    private QueryBuilder2(QueryBuilderContext context) {
        this.context = context;
    }
    
    private QueryBuilder2(QueryBuilderContext context, List<CommonTableExpression> ctes, List<Operand> selects, List<From> froms, List<Condition> conditions, 
            List<Condition> havings, List<Operand> groupBy, List<NamedWindow> windows, boolean distinct) {
        this(new QueryBuilderContext(context));
        this.ctes.addAll(ctes);
        this.selects.addAll(selects);
        this.froms.addAll(froms);
        this.conditions.addAll(conditions);
        this.havings.addAll(havings);
        this.groupBy.addAll(groupBy);
        this.distinct = distinct;
        this.windows.addAll(windows);
    }

    public QueryBuilder2 select(Operand operand) {
        selects.add(operand);
        return this;
    }
    
    public QueryBuilder2 with(String name, Query query) {
        ctes.add(new CommonTableExpression(name, query));
        return this;
    }
    
    public QueryBuilder2 select(String columnName) {
        return select(new Column(columnName));
    }

    public QueryBuilder2 select(Alias alias, String columnName) {
        return select(new Column(alias, columnName));
    }

    public QueryBuilder2 select(String columnName, From from) {
        return select(new Column(from.getAlias(), columnName));
    }
    
    public QueryBuilder2 select(Operand operand, Alias alias) {
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
    
    public From from(Query query, Alias alias) {
        return from(From.query(query, alias));
    }

    private From from(From from) {
        froms.add(from);
        return from;
    }
    
    public QueryBuilder2 groupBy(Column... columns) {
        groupBy.addAll(Arrays.asList(columns));
        return this;
    }
    
    public QueryBuilder2 groupBy(Column column) {
        groupBy.add(column);
        return this;
    }

    public QueryBuilder2 having(Condition condition) {
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
    
    public QueryBuilder2 windows(NamedWindow... namedWindows) {
        windows.addAll(Arrays.asList(namedWindows));
        return this;
    }
    
    public QueryBuilder2 window(NamedWindow window) {
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
    public SafeSql toSafeSql() {
        SafeSqlBuilder sb = new SafeSqlBuilder();
        if (!ctes.isEmpty()) {
            sb.appendConstant("WITH ");
            sb.appendJoined(", ", ctes);
            sb.appendConstant(" ");
        }
        sb.appendConstant("SELECT ");
        if (distinct) {
            sb.appendConstant("DISTINCT ");
        }
        sb.appendJoined(", ", selects);
        if (!froms.isEmpty()) {
            sb.appendConstant(" FROM ").appendJoined(", ", froms);
        }
        if (!conditions.isEmpty()) {
            sb.appendConstant(" WHERE ").appendJoined(" AND ", conditions);
        }
        if (!groupBy.isEmpty()) {
            sb.appendConstant(" GROUP BY ").appendJoined(", ", groupBy);
        }
        if (!havings.isEmpty()) {
            sb.appendConstant(" HAVING ").appendJoined(" AND ", havings);
        }
        if (!windows.isEmpty()) {
            sb.appendConstant(" WINDOW ").appendJoined(", ", windows);
        }
        return sb.toSafeSql();
    }

    @Override
    public QueryBuilder2 and(Condition condition) {
        conditions.add(condition);
        return this;
    }
    
    public QueryBuilder2 distinct() {
        this.distinct = true;
        return this;
    }
    
    public static QueryBuilder2 withContextOf(QueryBuilder2 other) {
        return new QueryBuilder2(other.context);
    }

}
