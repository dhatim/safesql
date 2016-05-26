package org.dhatim.safesql;

import static org.dhatim.safesql.SqlTokenizer.State.*;
import static org.dhatim.safesql.SqlTokenizer.CharType.*;

import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class SqlTokenizer {
    
    enum TokenClass {
        KEYWORD, IDENTIFIER, QUOTED_IDENTIFIER, LITERAL, SYMBOL, WHITESPACE
    }

    enum TokenType {
        KEYWORD                     (TokenClass.KEYWORD), 
        IDENTIFIER                  (TokenClass.IDENTIFIER), 
        QUOTED_IDENTIFIER           (TokenClass.QUOTED_IDENTIFIER), 
        UNICODE_QUOTED_IDENTIFIER   (TokenClass.QUOTED_IDENTIFIER), 
        STRING                      (TokenClass.LITERAL), 
        ESCAPED_STRING              (TokenClass.LITERAL), 
        UNICODE_STRING              (TokenClass.LITERAL), 
        DOLLAR_QUOTED_STRING        (TokenClass.LITERAL), 
        BITSTRING                   (TokenClass.LITERAL), 
        HEXSTRING                   (TokenClass.LITERAL), 
        NUMBER                      (TokenClass.LITERAL), 
        OPERATOR                    (TokenClass.SYMBOL), 
        WHITESPACE                  (TokenClass.WHITESPACE), 
        COMMENT                     (TokenClass.WHITESPACE), 
        POSITIONAL_PARAMETER        (TokenClass.SYMBOL), 
        LPAREN                      (TokenClass.SYMBOL), 
        RPAREN                      (TokenClass.SYMBOL), 
        LBRACK                      (TokenClass.SYMBOL), 
        RBRACK                      (TokenClass.SYMBOL), 
        SEMI                        (TokenClass.SYMBOL), 
        COMMA                       (TokenClass.SYMBOL), 
        DOT                         (TokenClass.SYMBOL),
        NUMERIC                     (TokenClass.LITERAL);
        
        private final TokenClass tokenClass;
        
        private TokenType(TokenClass tokenClass) {
            this.tokenClass = tokenClass;
        }
        
        public TokenClass getTokenClass() {
            return tokenClass;
        }
    }

    enum CharType {
        UNKNOWN, LETTER, UNDERSCORE, DIGIT, DOLLAR, QUOTE, DOUBLE_QUOTE, WHITESPACE, LPAREN, RPAREN, LBRACK, RBRACK, AMPERSAND, SEMI, COMMA, DOT, PLUS, MINUS, 
        ASTERISK, DIV, LT, GT, EQUAL, TILDE, BANG, AT, DIESE, HASH, PERCENT, CARET, PIPE, GRAVE, QUESTION, EOF
    }

    public static class Token {

        private final TokenType type;
        private final String value;

        public Token(TokenType type, String value) {
            this.type = type;
            this.value = value;
        }

        public TokenType type() {
            return type;
        }
        
        public TokenClass tokenClass() {
            return type.tokenClass;
        }

        public String value() {
            return value;
        }
        
        @Override
        public String toString() {
            return type + "{" + value + "}";
        }

    }
    
    private class TokenizerSpliterator implements Spliterator<Token> {

        @Override
        public boolean tryAdvance(Consumer<? super Token> action) {
            if (hasMoreTokens()) {
                action.accept(nextToken());
                return true;
            } else {
                return false;
            }
        }

        @Override
        public Spliterator<Token> trySplit() {
            return null;
        }

        @Override
        public long estimateSize() {
            return Long.MAX_VALUE;
        }

        @Override
        public int characteristics() {
            return ORDERED | NONNULL | IMMUTABLE;
        }
        
    }

    enum State {
        STATE_0, STATE_IDENT, STATE_EOT, STATE_WHITESPACE, STATE_OP, STATE_NUM, STATE_NUM_AFTER_DOT, STATE_NUM_AFTER_E, 
        STATE_NUM_AFTER_E_SIGN, STATE_QUOTED_IDENT, STATE_QUOTED_IDENT_QUOTE, STATE_MAY_UNICODE_VARIANT_OR_IDENT, STATE_UNICODE_VARIANT,
        STATE_STRING, STATE_STRING_QUOTE, STATE_MAY_ESCAPED_STRING_OR_IDENT, STATE_MAY_BITSTRING_OR_IDENT, STATE_MAY_HEXSTRING_OR_IDENT,
        STATE_BITSTRING, STATE_HEXSTRING
    }
    
    enum Modifier {
        NONE, UNICODE, ESCAPED, HEX
    }

    private static boolean isSpace(char ch) {
        return ch == ' ' || ch == '\t' || ch == '\n';
    }

    private static boolean isLetter(char ch) {
        return Character.isLetter(ch);
    }

    private static CharType toCharType(char ch) {
        CharType result;
        if (isSpace(ch)) {
            result = CharType.WHITESPACE;
        } else if (isLetter(ch)) {
            result = LETTER;
        } else if (Character.isDigit(ch)) {
            result = DIGIT;
        } else {
            switch (ch) {
            case '$':
                result = DOLLAR;
                break;
            case '_':
                result = UNDERSCORE;
                break;
            case '"':
                result = DOUBLE_QUOTE;
                break;
            case '\'':
                result = QUOTE;
                break;
            case '=':
                result = EQUAL;
                break;
            case '+':
                result = PLUS;
                break;
            case '-':
                result = MINUS;
                break;
            case '.':
                result = DOT;
                break;
            case '\0':
                result = EOF;
                break;
            case '&':
                result = AMPERSAND;
                break;
            default:
                result = UNKNOWN;
            }
        }
        return result;
    }

    //private State state = State.STATE_0;
    private int position = 0;
    
    private final String origin;
    private final char[] chars;
    
    private TokenType tokenType;
    private String token;

    public SqlTokenizer(String sql) {
        this.origin = sql;
        this.chars = sql.toCharArray();
    }

    public boolean hasMoreTokens() {
        return position < chars.length;
    }
    
    private void debug(String s) {
        System.out.println("## " + s);
    }

    public String nextTokenValue() {
        StringBuilder sb = new StringBuilder();
        Modifier modifier = Modifier.NONE;
        TokenType nextType = null;
        State state = State.STATE_0;
        boolean appendLast = false;
        while (position <= chars.length && state != STATE_EOT) {
            debug("state = " + state + " (" + position + "/" + chars.length + ")");
            char ch = position == chars.length ? '\0' : chars[position++];
            CharType type = toCharType(ch);
            debug("char = " + type);
            if (state == STATE_0) {
                switch (type) {
                    case LETTER:
                        if (ch == 'U' || ch == 'u') {
                            state = STATE_MAY_UNICODE_VARIANT_OR_IDENT;
                        } else if (ch == 'E' || ch == 'e') {
                            state = State.STATE_MAY_ESCAPED_STRING_OR_IDENT;
                        } else if (ch == 'B' || ch == 'b') {
                            state = State.STATE_MAY_BITSTRING_OR_IDENT;
                        } else if (ch == 'H' || ch == 'h') {
                            state = State.STATE_MAY_HEXSTRING_OR_IDENT;
                        } else {
                            state = STATE_IDENT;
                        }
                        break;
                    case UNDERSCORE:
                        state = STATE_IDENT;
                        break;
                    case WHITESPACE:
                        state = STATE_WHITESPACE;
                        break;
                    case EQUAL:
                        state = STATE_OP;
                        break;
                    case DIGIT:
                        state = STATE_NUM;
                        break;
                    case DOUBLE_QUOTE:
                        state = STATE_QUOTED_IDENT;
                        break;
                    case QUOTE:
                        state = STATE_STRING;
                        break;
                    default:
                        parseError("Unknown character [" + ch + "]");
                }
            } else if (state == STATE_IDENT) {
                switch (type) {
                    case LETTER:
                    case UNDERSCORE:
                    case DIGIT:
                    case DOLLAR:
                        state = STATE_IDENT;
                        break;
                    default:
                        nextType = TokenType.IDENTIFIER;
                        state = STATE_EOT;
                }
            } else if (state == STATE_WHITESPACE) {
                if (type == WHITESPACE) {
                    state = STATE_WHITESPACE;
                } else {
                    nextType = TokenType.WHITESPACE;
                    state = STATE_EOT;
                }
            } else if (state == STATE_OP) {
                switch (type) {
                    case EQUAL:
                        break;
                    default:
                        nextType = TokenType.OPERATOR;
                        state = STATE_EOT;
                }
            } else if (state == STATE_NUM) {
                switch (type) {
                    case DIGIT:
                        break;
                    case LETTER:
                        if (ch == 'e') {
                            state = STATE_NUM_AFTER_E;
                        } else {
                            parseError();
                        }
                        break;
                    case DOT:
                        state = STATE_NUM_AFTER_DOT;
                        break;
                    default:
                        nextType = TokenType.NUMERIC;
                        state = STATE_EOT;
                }
            } else if (state == STATE_NUM_AFTER_DOT) {
                switch (type) {
                    case DIGIT:
                        break;
                    case LETTER:
                        if (ch == 'e') {
                            state = STATE_NUM_AFTER_E;
                        } else {
                            parseError();
                        }
                        break;
                    default:
                        nextType = TokenType.NUMERIC;
                        state = STATE_EOT;
                }
            } else if (state == STATE_NUM_AFTER_E) {
                switch (type) {
                    case PLUS:
                    case MINUS:
                        state = STATE_NUM_AFTER_E_SIGN;
                        break;
                    case DIGIT:
                        state = STATE_NUM_AFTER_E_SIGN;
                        break;
                    default:
                        parseError();
                }
            } else if (state == STATE_NUM_AFTER_E_SIGN) {
                switch (type) {
                    case DIGIT:
                        break;
                    default:
                        state = STATE_EOT;
                        nextType = TokenType.NUMERIC;
                }
            } else if (state == STATE_QUOTED_IDENT) {
                switch (type) {
                    case DOUBLE_QUOTE:
                        state = STATE_QUOTED_IDENT_QUOTE;
                        break;
                    case EOF:
                        parseError();
                    default:
                }
            } else if (state == STATE_QUOTED_IDENT_QUOTE) {
                switch (type) {
                    case DOUBLE_QUOTE:
                        state = STATE_QUOTED_IDENT;
                        break;
                    default:
                        state = STATE_EOT;
                        nextType = TokenType.QUOTED_IDENTIFIER;
                }
            } else if (state == STATE_MAY_UNICODE_VARIANT_OR_IDENT) {
                switch (type) {
                    case AMPERSAND:
                        state = STATE_UNICODE_VARIANT;
                        break;
                    case LETTER:
                    case UNDERSCORE:
                    case DIGIT:
                    case DOLLAR:
                        state = STATE_IDENT;
                        break;
                    default:
                        nextType = TokenType.IDENTIFIER;
                        state = STATE_EOT;
                }
            } else if (state == STATE_UNICODE_VARIANT) {
                if (type == CharType.DOUBLE_QUOTE) {
                    modifier = Modifier.UNICODE;
                    state = State.STATE_QUOTED_IDENT;
                } else if (type == CharType.QUOTE) {
                    modifier = Modifier.UNICODE;
                    state = State.STATE_STRING;
                } else {
                    state = STATE_EOT;
                    position--;
                    nextType = TokenType.IDENTIFIER;
                }
            } else if (state == STATE_STRING) {
                switch (type) {
                    case QUOTE:
                        state = STATE_STRING_QUOTE;
                        break;
                    case EOF:
                        parseError();
                    default:
                }
            } else if (state == STATE_STRING_QUOTE) {
                switch (type) {
                    case QUOTE:
                        state = STATE_STRING;
                        break;
                    default:
                        state = STATE_EOT;
                        nextType = TokenType.STRING;
                }
            } else if (state == STATE_MAY_ESCAPED_STRING_OR_IDENT) {
                switch (type) {
                    case QUOTE:
                        modifier = Modifier.ESCAPED;
                        state = STATE_STRING;
                        break;
                    case LETTER:
                    case UNDERSCORE:
                    case DIGIT:
                    case DOLLAR:
                        state = STATE_IDENT;
                        break;
                    default:
                        nextType = TokenType.IDENTIFIER;
                        state = STATE_EOT;
                }
            } else if (state == State.STATE_MAY_BITSTRING_OR_IDENT) {
                switch (type) {
                    case QUOTE:
                        state = STATE_BITSTRING;
                        break;
                    case LETTER:
                    case UNDERSCORE:
                    case DIGIT:
                    case DOLLAR:
                        state = STATE_IDENT;
                        break;
                    default:
                        nextType = TokenType.IDENTIFIER;
                        state = STATE_EOT;
                }
            } else if (state == State.STATE_MAY_HEXSTRING_OR_IDENT) {
                switch (type) {
                    case QUOTE:
                        state = STATE_HEXSTRING;
                        break;
                    case LETTER:
                    case UNDERSCORE:
                    case DIGIT:
                    case DOLLAR:
                        state = STATE_IDENT;
                        break;
                    default:
                        nextType = TokenType.IDENTIFIER;
                        state = STATE_EOT;
                }
            } else if (state == State.STATE_BITSTRING) {
                switch (type) {
                    case QUOTE:
                        state = STATE_EOT;
                        nextType = TokenType.BITSTRING;
                        break;
                    default:
                        if (ch != '0' && ch != '1') {
                            parseError("Bitstring can only contains 0 or 1 characters");
                        }
                }
            } else if (state == State.STATE_HEXSTRING) {
                switch (type) {
                    case QUOTE:
                        state = STATE_EOT;
                        nextType = TokenType.HEXSTRING;
                        break;
                    default:
                        if (!((ch >= '0' && ch <= '9') || (ch >= 'A' && ch <= 'F') || (ch >= 'a' && ch <= 'f'))) {
                            parseError("Hexstring can only contains hexadecimal characters (0123456789ABCDEF)");
                        }
                }
            }
            
            if (state == STATE_EOT) {
                if (type != EOF) {
                    debug("recule");
                    position--;
                }
            } else {
                sb.append(ch);
            }
            debug(" state => " + state);
        }
        state = STATE_0;
        token = sb.toString();
        tokenType = modify(nextType, modifier);
        debug("==> return " + token + " " + nextType);
        debug("position is " + position + " on " + chars.length);
        return token;
    }
    
    private TokenType modify(TokenType type, Modifier modifier) {
        TokenType result;
        if (modifier == Modifier.NONE) {
            result = type;
        } else {
            if (type == TokenType.STRING) {
                if (modifier == Modifier.ESCAPED) {
                    result = TokenType.ESCAPED_STRING;
                } else if (modifier == Modifier.UNICODE) {
                    result = TokenType.UNICODE_STRING;
                } else {
                    result = null;
                }
            } else if (type == TokenType.QUOTED_IDENTIFIER) {
                if (modifier == Modifier.UNICODE) {
                    result = TokenType.UNICODE_QUOTED_IDENTIFIER;
                } else {
                    result = null;
                }
            } else {
                result = type;
            }
        }
        if (result == null) {
            throw new IllegalStateException("The modifier " + modifier + " is not compatible with type " + type);
        }
        return result;
    }
    
    public Token nextToken() {
        nextTokenValue();
        return new Token(tokenType, token);
    }

    public TokenType getCurrentTokenType() {
        return tokenType;
    }

    public Stream<Token> stream() {
        return StreamSupport.stream(new TokenizerSpliterator(), false);
    }
    
    private void parseError() {
        throw new SqlParseException("", position - 1, origin);
    }

    private void parseError(String msg) {
        throw new SqlParseException(msg, position - 1, origin);
    }
    
}
