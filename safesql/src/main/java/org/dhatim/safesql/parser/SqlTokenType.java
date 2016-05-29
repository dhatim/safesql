package org.dhatim.safesql.parser;

public enum SqlTokenType {
    KEYWORD                     (SqlTokenKind.KEYWORD), 
    IDENTIFIER                  (SqlTokenKind.IDENTIFIER), 
    QUOTED_IDENTIFIER           (SqlTokenKind.QUOTED_IDENTIFIER), 
    UNICODE_QUOTED_IDENTIFIER   (SqlTokenKind.QUOTED_IDENTIFIER), 
    STRING                      (SqlTokenKind.LITERAL), 
    ESCAPED_STRING              (SqlTokenKind.LITERAL), 
    UNICODE_STRING              (SqlTokenKind.LITERAL), 
    DOLLAR_QUOTED_STRING        (SqlTokenKind.LITERAL), 
    BITSTRING                   (SqlTokenKind.LITERAL), 
    HEXSTRING                   (SqlTokenKind.LITERAL), 
    NUMERIC                     (SqlTokenKind.LITERAL),
    OPERATOR                    (SqlTokenKind.SYMBOL), 
    WHITESPACE                  (SqlTokenKind.WHITESPACE), 
    LINE_COMMENT                (SqlTokenKind.WHITESPACE), 
    BLOCK_COMMENT               (SqlTokenKind.WHITESPACE), 
    POSITIONAL_PARAMETER        (SqlTokenKind.SYMBOL), 
    LPAREN                      (SqlTokenKind.SYMBOL), 
    RPAREN                      (SqlTokenKind.SYMBOL), 
    LBRACK                      (SqlTokenKind.SYMBOL), 
    RBRACK                      (SqlTokenKind.SYMBOL), 
    SEMI                        (SqlTokenKind.SYMBOL), 
    COMMA                       (SqlTokenKind.SYMBOL), 
    DOT                         (SqlTokenKind.SYMBOL);
    
    private final SqlTokenKind kind;
    
    private SqlTokenType(SqlTokenKind kind) {
        this.kind = kind;
    }
    
    public SqlTokenKind getKind() {
        return kind;
    }
}