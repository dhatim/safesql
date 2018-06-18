package org.dhatim.safesql;

class SafeSqlImpl implements SafeSql {

    private final Dialect dialect;
    private final String sql;
    private final Object[] parameters;
    
    private String string;

    SafeSqlImpl(Dialect dialect, String sql, Object[] parameters) {
        this.dialect = dialect;
        this.sql = sql;
        this.parameters = parameters;
    }

    @Override
    public String asSql() {
        return sql;
    }

    @Override
    public Object[] getParameters() {
        return parameters.clone();
    }

    @Override
    public Dialect getDialect() {
        return dialect;
    }

    @Override
    public String asString() {
        if (string == null) {
            string = SafeSql.super.asString();
        }
        return string;
    }

}
