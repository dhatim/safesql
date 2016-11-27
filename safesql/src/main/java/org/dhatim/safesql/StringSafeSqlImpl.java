package org.dhatim.safesql;

class StringSafeSqlImpl implements SafeSql {
    
    private static final Object[] EMPTY = {};
    
    private final String sql;
    
    public StringSafeSqlImpl(String sql) {
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

}
