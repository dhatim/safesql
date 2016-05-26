package org.dhatim.safesql;

@SuppressWarnings("serial")
public class SqlParseException extends RuntimeException {
    
    private final int position;
    private final String sql;

    public SqlParseException(String message, int position, String sql) {
        super(message);
        this.position = position;
        this.sql = sql;
    }
    
    public int getPosition() {
        return position;
    }
    
    public String getSql() {
        return sql;
    }

}
