package org.dhatim.safesql;

class StringSafeSqlImpl implements SafeSql {
    
    private static final Object[] EMPTY = {};

    private final Dialect dialect;
    private final String sql;
    
    public StringSafeSqlImpl(Dialect dialect, String sql) {
        this.dialect = dialect;
        this.sql = sql;
    }

    @Override
    public String asSql() {
        return sql;
    }
    
    @Override
    public String asString() {
        return sql;
    }

    @Override
    public Object[] getParameters() {
        return EMPTY;
    }

    @Override
    public Dialect getDialect() {
        return dialect;
    }

}
