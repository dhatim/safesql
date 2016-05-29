package org.dhatim.safesql.parser;

public final class SqlToken {

    private final SqlTokenType type;
    private final String value;

    public SqlToken(SqlTokenType type, String value) {
        this.type = type;
        this.value = value;
    }

    public SqlTokenType type() {
        return type;
    }
    
    public SqlTokenKind kind() {
        return type.getKind();
    }

    public String value() {
        return value;
    }
    
    @Override
    public String toString() {
        return type + "{" + value + "}";
    }

}