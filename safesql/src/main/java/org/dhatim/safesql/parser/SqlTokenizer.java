package org.dhatim.safesql.parser;

import static org.dhatim.safesql.parser.SqlTokenizer.CharType.AMPERSAND;
import static org.dhatim.safesql.parser.SqlTokenizer.CharType.ASTERISK;
import static org.dhatim.safesql.parser.SqlTokenizer.CharType.AT;
import static org.dhatim.safesql.parser.SqlTokenizer.CharType.BANG;
import static org.dhatim.safesql.parser.SqlTokenizer.CharType.CARET;
import static org.dhatim.safesql.parser.SqlTokenizer.CharType.COLON;
import static org.dhatim.safesql.parser.SqlTokenizer.CharType.DIGIT;
import static org.dhatim.safesql.parser.SqlTokenizer.CharType.DIV;
import static org.dhatim.safesql.parser.SqlTokenizer.CharType.DOLLAR;
import static org.dhatim.safesql.parser.SqlTokenizer.CharType.DOT;
import static org.dhatim.safesql.parser.SqlTokenizer.CharType.DOUBLE_QUOTE;
import static org.dhatim.safesql.parser.SqlTokenizer.CharType.EOF;
import static org.dhatim.safesql.parser.SqlTokenizer.CharType.EOL;
import static org.dhatim.safesql.parser.SqlTokenizer.CharType.EQUAL;
import static org.dhatim.safesql.parser.SqlTokenizer.CharType.GRAVE;
import static org.dhatim.safesql.parser.SqlTokenizer.CharType.GT;
import static org.dhatim.safesql.parser.SqlTokenizer.CharType.HASH;
import static org.dhatim.safesql.parser.SqlTokenizer.CharType.LETTER;
import static org.dhatim.safesql.parser.SqlTokenizer.CharType.LT;
import static org.dhatim.safesql.parser.SqlTokenizer.CharType.MINUS;
import static org.dhatim.safesql.parser.SqlTokenizer.CharType.PERCENT;
import static org.dhatim.safesql.parser.SqlTokenizer.CharType.PIPE;
import static org.dhatim.safesql.parser.SqlTokenizer.CharType.PLUS;
import static org.dhatim.safesql.parser.SqlTokenizer.CharType.QUESTION;
import static org.dhatim.safesql.parser.SqlTokenizer.CharType.QUOTE;
import static org.dhatim.safesql.parser.SqlTokenizer.CharType.TILDE;
import static org.dhatim.safesql.parser.SqlTokenizer.CharType.UNDERSCORE;
import static org.dhatim.safesql.parser.SqlTokenizer.CharType.UNKNOWN;
import static org.dhatim.safesql.parser.SqlTokenizer.CharType.WHITESPACE;
import static org.dhatim.safesql.parser.SqlTokenizer.State.STATE_BITSTRING;
import static org.dhatim.safesql.parser.SqlTokenizer.State.STATE_BLOCK_COMMENT;
import static org.dhatim.safesql.parser.SqlTokenizer.State.STATE_DOLLAR_QUOTED_STRING;
import static org.dhatim.safesql.parser.SqlTokenizer.State.STATE_EOT;
import static org.dhatim.safesql.parser.SqlTokenizer.State.STATE_HEXSTRING;
import static org.dhatim.safesql.parser.SqlTokenizer.State.STATE_IDENT;
import static org.dhatim.safesql.parser.SqlTokenizer.State.STATE_LINE_COMMENT;
import static org.dhatim.safesql.parser.SqlTokenizer.State.STATE_MAY_BLOCK_COMMENT_LEVEL;
import static org.dhatim.safesql.parser.SqlTokenizer.State.STATE_MAY_CAST_OR_SLICE;
import static org.dhatim.safesql.parser.SqlTokenizer.State.STATE_MAY_DOLLAR_QUOTED_STRING;
import static org.dhatim.safesql.parser.SqlTokenizer.State.STATE_MAY_END_BLOCK_LEVEL;
import static org.dhatim.safesql.parser.SqlTokenizer.State.STATE_MAY_END_DOLLAR_QUOTED_STRING;
import static org.dhatim.safesql.parser.SqlTokenizer.State.STATE_MAY_NUM_OR_OP;
import static org.dhatim.safesql.parser.SqlTokenizer.State.STATE_MAY_OPX_OR_FUTURE_BLOCK_COMMENT;
import static org.dhatim.safesql.parser.SqlTokenizer.State.STATE_MAY_OPX_OR_FUTURE_LINE_COMMENT;
import static org.dhatim.safesql.parser.SqlTokenizer.State.STATE_MAY_OP_OR_BLOCK_COMMENT;
import static org.dhatim.safesql.parser.SqlTokenizer.State.STATE_MAY_OP_OR_FUTURE_BLOCK_COMMENT;
import static org.dhatim.safesql.parser.SqlTokenizer.State.STATE_MAY_OP_OR_FUTURE_LINE_COMMENT;
import static org.dhatim.safesql.parser.SqlTokenizer.State.STATE_MAY_OP_OR_LINE_COMMENT;
import static org.dhatim.safesql.parser.SqlTokenizer.State.STATE_MAY_PARAMETER_OR_DOLLAR_QUOTED_STRING;
import static org.dhatim.safesql.parser.SqlTokenizer.State.STATE_MAY_UNICODE_VARIANT_OR_IDENT;
import static org.dhatim.safesql.parser.SqlTokenizer.State.STATE_NUM;
import static org.dhatim.safesql.parser.SqlTokenizer.State.STATE_NUM_AFTER_DOT;
import static org.dhatim.safesql.parser.SqlTokenizer.State.STATE_NUM_AFTER_E;
import static org.dhatim.safesql.parser.SqlTokenizer.State.STATE_NUM_AFTER_E_SIGN;
import static org.dhatim.safesql.parser.SqlTokenizer.State.STATE_OP;
import static org.dhatim.safesql.parser.SqlTokenizer.State.STATE_OPX;
import static org.dhatim.safesql.parser.SqlTokenizer.State.STATE_OP_START;
import static org.dhatim.safesql.parser.SqlTokenizer.State.STATE_OP_WITHOUT_FINAL_PLUS;
import static org.dhatim.safesql.parser.SqlTokenizer.State.STATE_POSITIONAL_PARAMETER;
import static org.dhatim.safesql.parser.SqlTokenizer.State.STATE_QUOTED_IDENT;
import static org.dhatim.safesql.parser.SqlTokenizer.State.STATE_QUOTED_IDENT_QUOTE;
import static org.dhatim.safesql.parser.SqlTokenizer.State.STATE_STRING;
import static org.dhatim.safesql.parser.SqlTokenizer.State.STATE_STRING_QUOTE;
import static org.dhatim.safesql.parser.SqlTokenizer.State.STATE_UNICODE_VARIANT;
import static org.dhatim.safesql.parser.SqlTokenizer.State.STATE_WHITESPACE;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class SqlTokenizer {
    
    enum CharType {
        UNKNOWN, LETTER, UNDERSCORE, DIGIT, DOLLAR, QUOTE, DOUBLE_QUOTE, WHITESPACE, LPAREN, RPAREN, LBRACK, RBRACK, AMPERSAND, SEMI, COMMA, DOT, PLUS, MINUS, 
        ASTERISK, DIV, LT, GT, EQUAL, TILDE, BANG, AT, DIESE, HASH, PERCENT, CARET, PIPE, GRAVE, QUESTION, EOF, EOL, COLON
    }
    
    public static class Reserved {
    	
		private static final String[] RESERVED = { 
				"all", "analyse", "analyze", "and", "any", "array", "as", "asc", "asymmetric", "authorization", "between", 
				"binary", "both", "case", "cast", "check", "collate", "column", "constraint", "create", "cross", "current_date", 
				"current_role", "current_time", "current_timestamp", "current_user", "default", "deferrable", "desc", "distinct", 
				"do", "else", "end", "except", "false", "for", "foreign", "freeze", "from", "full", "grant", "group", "having", 
				"ilike", "in", "initially", "inner", "intersect", "into", "is", "isnull", "join", "leading", "left", "like", 
				"limit", "localtime", "localtimestamp", "natural", "new", "not", "notnull", "null", "off", "offset", "old", 
				"on", "only", "or", "order", "outer", "overlaps", "placing", "primary", "references", "right", "select", 
				"session_user", "similar", "some", "symmetric", "table", "then", "to", "trailing", "true", "union", "unique", 
				"user", "using", "verbose", "when", "where" 
		};
		
		private static final String[] NON_RESERVED = { "abort", "absolute", "access", "action", "add", "admin", "after", "aggregate", 
				"also", "alter", "assertion", "assignment", "at", "backward", "before", "begin", "bigint", "bit", "boolean", "by", 
				"cache", "called", "cascade", "chain", "char", "character", "characteristics", "checkpoint", "class", "close", 
				"cluster", "coalesce", "comment", "commit", "committed", "connection", "constraints", "conversion", "convert", 
				"copy", "createdb", "createrole", "createuser", "csv", "cursor", "cycle", "database", "day", "deallocate", "dec", 
				"decimal", "declare", "defaults", "deferred", "definer", "delete", "delimiter", "delimiters", "disable", "domain", 
				"double", "drop", "each", "enable", "encoding", "encrypted", "escape", "excluding", "exclusive", "execute", "exists", 
				"explain", "external", "extract", "fetch", "first", "float", "force", "forward", "function", "global", "granted", 
				"greatest", "handler", "header", "hold", "hour", "immediate", "immutable", "implicit", "including", "increment", 
				"index", "inherit", "inherits", "inout", "input", "insensitive", "insert", "instead", "int", "integer", "interval", 
				"invoker", "isolation", "key", "lancompiler", "language", "large", "last", "least", "level", "listen", "load", "local", 
				"location", "lock", "login", "match", "maxvalue", "minute", "minvalue", "mode", "month", "move", "names", "national", 
				"nchar", "next", "no", "nocreatedb", "nocreaterole", "nocreateuser", "noinherit", "nologin", "none", "nosuperuser", 
				"nothing", "notify", "nowait", "nullif", "numeric", "object", "of", "oids", "operator", "option", "out", "overlay", 
				"owner", "partial", "password", "position", "precision", "prepare", "prepared", "preserve", "prior", "privileges", 
				"procedural", "procedure", "quote", "read", "real", "recheck", "reindex", "relative", "release", "rename", 
				"repeatable", "replace", "reset", "restart", "restrict", "returns", "revoke", "role", "rollback", "row", "rows", 
				"rule", "savepoint", "schema", "scroll", "second", "security", "sequence", "serializable", "session", "set", "setof", 
				"share", "show", "simple", "smallint", "stable", "start", "statement", "statistics", "stdin", "stdout", "storage", 
				"strict", "substring", "superuser", "sysid", "system", "tablespace", "temp", "template", "temporary", "time", 
				"timestamp", "toast", "transaction", "treat", "trigger", "trim", "truncate", "trusted", "type", "uncommitted", 
				"unencrypted", "unknown", "unlisten", "until", "update", "vacuum", "valid", "validator", "values", "varchar", 
				"varying", "view", "volatile", "with", "without", "work", "write", "year", "zone"
		};
    	
    	private static final HashSet<String> RESERVED_KEYWORDS = new HashSet<>(Arrays.asList(RESERVED));
    	private static final HashSet<String> NON_RESERVED_KEYWORDS = new HashSet<>(Arrays.asList(NON_RESERVED));
    	
    	public static boolean isReserved(String keyword) {
    		return RESERVED_KEYWORDS.contains(keyword.toLowerCase(Locale.ROOT));
    	}
    	
    	public static boolean isNonReserved(String keyword) {
    		return NON_RESERVED_KEYWORDS.contains(keyword.toLowerCase(Locale.ROOT));
    	}
    	
    	public static boolean isKeyword(String keyword) {
    		final String lowerKeyword = keyword.toLowerCase(Locale.ROOT);
			return RESERVED_KEYWORDS.contains(lowerKeyword) ||  NON_RESERVED_KEYWORDS.contains(lowerKeyword);
    	}
    	
    }

    private class TokenizerSpliterator<T> implements Spliterator<T> {

    	private final Supplier<T> nextSupplier;

		public TokenizerSpliterator(Supplier<T> nextSupplier) {
			this.nextSupplier = nextSupplier;
		}
    	
        @Override
        public boolean tryAdvance(Consumer<? super T> action) {
            if (hasMoreTokens()) {
            	action.accept(nextSupplier.get());
                return true;
            } else {
                return false;
            }
        }

        @Override
        public Spliterator<T> trySplit() {
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
		STATE_NUM_AFTER_E_SIGN, STATE_QUOTED_IDENT, STATE_QUOTED_IDENT_QUOTE, STATE_MAY_UNICODE_VARIANT_OR_IDENT, 
		STATE_UNICODE_VARIANT, STATE_STRING, STATE_STRING_QUOTE, STATE_MAY_ESCAPED_STRING_OR_IDENT, STATE_MAY_BITSTRING_OR_IDENT, 
		STATE_MAY_HEXSTRING_OR_IDENT, STATE_BITSTRING, STATE_HEXSTRING, STATE_MAY_PARAMETER_OR_DOLLAR_QUOTED_STRING, 
		STATE_MAY_DOLLAR_QUOTED_STRING, STATE_DOLLAR_QUOTED_STRING, STATE_MAY_END_DOLLAR_QUOTED_STRING, 
		STATE_POSITIONAL_PARAMETER, STATE_OP_START, STATE_OPX, STATE_MAY_OP_OR_LINE_COMMENT, STATE_MAY_OP_OR_BLOCK_COMMENT, 
		STATE_OP_WITHOUT_FINAL_PLUS, STATE_MAY_OP_OR_FUTURE_LINE_COMMENT, STATE_MAY_OP_OR_FUTURE_BLOCK_COMMENT, 
		STATE_MAY_OPX_OR_FUTURE_LINE_COMMENT, STATE_MAY_OPX_OR_FUTURE_BLOCK_COMMENT, STATE_LINE_COMMENT, STATE_BLOCK_COMMENT, 
		STATE_MAY_BLOCK_COMMENT_LEVEL, STATE_MAY_END_BLOCK_LEVEL, STATE_MAY_CAST_OR_SLICE, STATE_MAY_NUM_OR_OP
	}
    
    enum Modifier {
        NONE, UNICODE, ESCAPED, HEX
    }
    
    //private static final int NAMEDATALEN = 64-1;

    private int position = -1;
    
    private final String origin;
    private final char[] chars;
    
    private SqlTokenType tokenType;
    private String token;

    public SqlTokenizer(String sql) {
        this.origin = sql;
        this.chars = sql.toCharArray();
    }

    public boolean hasMoreTokens() {
    	boolean has = position+1 < chars.length;
    	debug(() -> "hasMoreTokens = " + has + " because position " + position + "/" + chars.length);
        return has;
    }
    
    @Deprecated
    private void debug(Supplier<String> s) {
        //System.out.println("[DEBUG] " + s);
    }
    
    public String nextValue() {
        StringBuilder sb = new StringBuilder();
        Modifier modifier = Modifier.NONE;
        SqlTokenType nextType = null;
        State state = State.STATE_0;
        int exitPatternPosition = 0;
        String exitPattern = "";
        int commentLevel = 0;
        int tooManyChars = 0;
        while (position <= chars.length && state != STATE_EOT) {
        	position++;
        	final State debugState = state;
        	debug(() -> "#### state = " + debugState + " (" + position + "/" + chars.length + ")");
        	final char ch;
        	final CharType type;
        	if (position == chars.length) {
        		ch = '\0';
        		type = EOF;
        	} else {
        		ch = chars[position];
        		type = toCharType(ch);
        	}
            debug(() -> "char = " + type);
            
			switch (state) {
				case STATE_0:
					switch (type) {
						case LETTER:
							if (ch == 'U' || ch == 'u') {
								state = STATE_MAY_UNICODE_VARIANT_OR_IDENT;
							} else if (ch == 'E' || ch == 'e') {
								state = State.STATE_MAY_ESCAPED_STRING_OR_IDENT;
							} else if (ch == 'B' || ch == 'b') {
								state = State.STATE_MAY_BITSTRING_OR_IDENT;
							} else if (ch == 'X' || ch == 'x') {
								state = State.STATE_MAY_HEXSTRING_OR_IDENT;
							} else {
								state = STATE_IDENT;
							}
							break;
						case UNDERSCORE:
							state = STATE_IDENT;
							break;
						case WHITESPACE:
						case EOL:
							state = STATE_WHITESPACE;
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
						case DOLLAR:
							state = STATE_MAY_PARAMETER_OR_DOLLAR_QUOTED_STRING;
							break;
						case PLUS:
						case ASTERISK:
						case LT:
						case GT:
						case EQUAL:
							state = STATE_OP_START;
							break;
						case TILDE:
						case BANG:
						case AT:
						case HASH:
						case PERCENT:
						case CARET:
						case AMPERSAND:
						case PIPE:
						case GRAVE:
						case QUESTION:
							state = STATE_OPX;
							break;
						case MINUS:
							state = STATE_MAY_OP_OR_LINE_COMMENT;
							break;
						case DIV:
							state = STATE_MAY_OP_OR_BLOCK_COMMENT;
							break;
						case LPAREN:
							nextType = SqlTokenType.LPAREN;
							state = STATE_EOT;
							break;
						case RPAREN:
							nextType = SqlTokenType.RPAREN;
							state = STATE_EOT;
							break;
						case LBRACK:
							nextType = SqlTokenType.LBRACK;
							state = STATE_EOT;
							break;
						case RBRACK:
							nextType = SqlTokenType.RBRACK;
							state = STATE_EOT;
							break;
						case COMMA:
							nextType = SqlTokenType.COMMA;
							state = STATE_EOT;
							break;
						case SEMI:
							nextType = SqlTokenType.SEMI;
							state = STATE_EOT;
							break;
						case DOT:
							state = STATE_MAY_NUM_OR_OP;
							break;
						case COLON:
							state = STATE_MAY_CAST_OR_SLICE;
							break;
						case UNKNOWN:
						default:
							parseError("Unknown character [" + ch + "] of type " + type);
					}
					break;
				case STATE_IDENT:
					switch (type) {
						case LETTER:
						case UNDERSCORE:
						case DIGIT:
						case DOLLAR:
							state = STATE_IDENT;
							break;
						default:
							nextType = SqlTokenType.IDENTIFIER;
							state = STATE_EOT;
							tooManyChars = 1;
					}
					break;
				case STATE_WHITESPACE:
					if (type == WHITESPACE || type == EOL) {
						state = STATE_WHITESPACE;
					} else {
						nextType = SqlTokenType.WHITESPACE;
						state = STATE_EOT;
						tooManyChars = 1;
					}
					break;
				case STATE_NUM:
					switch (type) {
						case DIGIT:
							break;
						case LETTER:
							if (ch == 'e') {
								state = STATE_NUM_AFTER_E;
							} else {
								nextType = SqlTokenType.NUMERIC;
								state = STATE_EOT;
								tooManyChars = 1;
							}
							break;
						case DOT:
							state = STATE_NUM_AFTER_DOT;
							break;
						default:
							nextType = SqlTokenType.NUMERIC;
							state = STATE_EOT;
							tooManyChars = 1;
					}
					break;
				case STATE_NUM_AFTER_DOT:
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
							nextType = SqlTokenType.NUMERIC;
							state = STATE_EOT;
							tooManyChars = 1;
					}
					break;
				case STATE_NUM_AFTER_E:
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
					break;
				case STATE_NUM_AFTER_E_SIGN:
					switch (type) {
						case DIGIT:
							break;
						default:
							nextType = SqlTokenType.NUMERIC;
							state = STATE_EOT;
							tooManyChars = 1;
					}
					break;
				case STATE_MAY_NUM_OR_OP:
					switch (type) {
						case DIGIT:
							state = STATE_NUM_AFTER_DOT;
							break;
						default:
							nextType = SqlTokenType.DOT;
							state = STATE_EOT;
							tooManyChars = 1;
					}
					break;
				case STATE_QUOTED_IDENT:
					switch (type) {
						case DOUBLE_QUOTE:
							state = STATE_QUOTED_IDENT_QUOTE;
							break;
						case EOF:
							parseError();
						default:
					}
					break;
				case STATE_QUOTED_IDENT_QUOTE:
					switch (type) {
						case DOUBLE_QUOTE:
							state = STATE_QUOTED_IDENT;
							break;
						default:
							nextType = SqlTokenType.QUOTED_IDENTIFIER;
							state = STATE_EOT;
							tooManyChars = 1;
					}
					break;
				case STATE_MAY_UNICODE_VARIANT_OR_IDENT:
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
							nextType = SqlTokenType.IDENTIFIER;
							state = STATE_EOT;
							tooManyChars = 1;
					}
					break;
				case STATE_UNICODE_VARIANT:
					if (type == CharType.DOUBLE_QUOTE) {
						modifier = Modifier.UNICODE;
						state = State.STATE_QUOTED_IDENT;
					} else if (type == CharType.QUOTE) {
						modifier = Modifier.UNICODE;
						state = State.STATE_STRING;
					} else {
						nextType = SqlTokenType.IDENTIFIER;
						state = STATE_EOT;
						tooManyChars = 2;
					}
					break;
				case STATE_STRING:
					switch (type) {
						case QUOTE:
							state = STATE_STRING_QUOTE;
							break;
						case EOF:
							parseError();
						default:
					}
					break;
				case STATE_STRING_QUOTE:
					switch (type) {
						case QUOTE:
							state = STATE_STRING;
							break;
						default:
							nextType = SqlTokenType.STRING;
							state = STATE_EOT;
							tooManyChars = 1;
					}
					break;
				case STATE_MAY_ESCAPED_STRING_OR_IDENT:
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
							nextType = SqlTokenType.IDENTIFIER;
							state = STATE_EOT;
							tooManyChars = 1;
					}
					break;
				case STATE_MAY_BITSTRING_OR_IDENT:
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
							nextType = SqlTokenType.IDENTIFIER;
							state = STATE_EOT;
							tooManyChars = 1;
					}
					break;
				case STATE_MAY_HEXSTRING_OR_IDENT:
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
							nextType = SqlTokenType.IDENTIFIER;
							state = STATE_EOT;
							tooManyChars = 1;
					}
					break;
				case STATE_BITSTRING:
					switch (type) {
						case QUOTE:
							state = STATE_EOT;
							nextType = SqlTokenType.BITSTRING;
							break;
						default:
							if (ch != '0' && ch != '1') {
								parseError("Bitstring can only contains 0 or 1 characters");
							}
					}
					break;
				case STATE_HEXSTRING:
					switch (type) {
						case QUOTE:
							state = STATE_EOT;
							nextType = SqlTokenType.HEXSTRING;
							break;
						default:
							if (!((ch >= '0' && ch <= '9') || (ch >= 'A' && ch <= 'F') || (ch >= 'a' && ch <= 'f'))) {
								parseError("Hexstring can only contains hexadecimal characters (0123456789ABCDEF)");
							}
					}
					break;
				case STATE_MAY_PARAMETER_OR_DOLLAR_QUOTED_STRING:
					switch (type) {
						case DOLLAR:
							state = STATE_DOLLAR_QUOTED_STRING;
							exitPattern = "";
							debug(() -> "exitPattern empty");
							break;
						case DIGIT:
							state = STATE_POSITIONAL_PARAMETER;
							break;
						case UNDERSCORE:
						case LETTER:
							state = STATE_MAY_DOLLAR_QUOTED_STRING;
							break;
						default:
							parseError("syntax error at or near [$]");
					}
					break;
				case STATE_DOLLAR_QUOTED_STRING:
					if (type == DOLLAR) {
						state = STATE_MAY_END_DOLLAR_QUOTED_STRING;
						exitPatternPosition = 0;
					} else if (type == EOF) {
						parseError("unterminated dollar-quoted string at or near [" + sb.toString() + "]");
					}
					break;
				case STATE_MAY_END_DOLLAR_QUOTED_STRING:
					if (type == DOLLAR) {
						if (exitPatternPosition == exitPattern.length()) {
							state = STATE_EOT;
							nextType = SqlTokenType.DOLLAR_QUOTED_STRING;
						} else {
							state = STATE_DOLLAR_QUOTED_STRING;
						}
					} else if (type == EOF) {
						parseError("unterminated dollar-quoted string at or near [" + sb.toString() + "]");
					} else {
						if (exitPattern.charAt(exitPatternPosition++) != ch) {
							state = STATE_DOLLAR_QUOTED_STRING;
						}
					}
					break;
				case STATE_MAY_DOLLAR_QUOTED_STRING:
					switch (type) {
						case LETTER:
						case DIGIT:
						case UNDERSCORE:
							break;
						case DOLLAR:
							state = STATE_DOLLAR_QUOTED_STRING;
							exitPattern = sb.toString().substring(1);
							final String debugExitPattern = exitPattern;
							debug(() -> "exitPattern " + debugExitPattern);
							break;
						default:
							parseError("syntax error at or near [$]");
					}
					break;
				case STATE_POSITIONAL_PARAMETER:
					if (type != DIGIT) {
						nextType = SqlTokenType.POSITIONAL_PARAMETER;
						state = STATE_EOT;
						tooManyChars = 1;
					}
					break;
				case STATE_OP_START:
					switch (type) {
						case PLUS:
							state = STATE_OP_WITHOUT_FINAL_PLUS;
							break;
						case ASTERISK:
						case LT:
						case GT:
						case EQUAL:
							state = STATE_OP;
							break;
						case TILDE:
						case BANG:
						case AT:
						case HASH:
						case PERCENT:
						case CARET:
						case AMPERSAND:
						case PIPE:
						case GRAVE:
						case QUESTION:
							state = STATE_OPX;
							break;
						case MINUS:
							state = STATE_MAY_OP_OR_FUTURE_LINE_COMMENT;
							break;
						case DIV:
							state = STATE_MAY_OP_OR_FUTURE_BLOCK_COMMENT;
							break;
						case EOF:
						default:
							nextType = SqlTokenType.OPERATOR;
							state = STATE_EOT;
							tooManyChars = 1;
					}
					break;
				case STATE_OP:
					switch (type) {
						case PLUS:
							state = STATE_OP_WITHOUT_FINAL_PLUS;
							break;
						case ASTERISK:
						case LT:
						case GT:
						case EQUAL:
							state = STATE_OP;
							break;
						case TILDE:
						case BANG:
						case AT:
						case HASH:
						case PERCENT:
						case CARET:
						case AMPERSAND:
						case PIPE:
						case GRAVE:
						case QUESTION:
							state = STATE_OPX;
							break;
						case MINUS:
							state = STATE_MAY_OP_OR_FUTURE_LINE_COMMENT;
							break;
						case DIV:
							state = STATE_MAY_OP_OR_FUTURE_BLOCK_COMMENT;
							break;
						case EOF:
						default:
							nextType = SqlTokenType.OPERATOR;
							state = STATE_EOT;
							tooManyChars = 1;
					}
					break;
				case STATE_OPX:
					switch (type) {
						case PLUS:
						case ASTERISK:
						case LT:
						case GT:
						case EQUAL:
						case TILDE:
						case BANG:
						case AT:
						case HASH:
						case PERCENT:
						case CARET:
						case AMPERSAND:
						case PIPE:
						case GRAVE:
						case QUESTION:
							state = STATE_OPX;
							break;
						case MINUS:
							state = STATE_MAY_OPX_OR_FUTURE_LINE_COMMENT;
							break;
						case DIV:
							state = STATE_MAY_OPX_OR_FUTURE_BLOCK_COMMENT;
							break;
						case EOF:
						default:
							nextType = SqlTokenType.OPERATOR;
							state = STATE_EOT;
							tooManyChars = 1;
					}
					break;
				case STATE_OP_WITHOUT_FINAL_PLUS:
					switch (type) {
						case PLUS:
							state = STATE_OP_WITHOUT_FINAL_PLUS;
							break;
						case ASTERISK:
						case LT:
						case GT:
						case EQUAL:
							state = STATE_OP;
							break;
						case TILDE:
						case BANG:
						case AT:
						case HASH:
						case PERCENT:
						case CARET:
						case AMPERSAND:
						case PIPE:
						case GRAVE:
						case QUESTION:
							state = STATE_OPX;
							break;
						case MINUS:
							state = STATE_MAY_OP_OR_FUTURE_LINE_COMMENT;
							break;
						case DIV:
							state = STATE_MAY_OP_OR_FUTURE_BLOCK_COMMENT;
							break;
						case EOF:
						default:
							parseError("Cannot end and operator with [+] when there is no [~ ! @ # % ^ & | ` ?] before");
					}
					break;
				case STATE_MAY_OP_OR_FUTURE_LINE_COMMENT:
					switch (type) {
						case PLUS:
							state = STATE_OP_WITHOUT_FINAL_PLUS;
							break;
						case ASTERISK:
						case LT:
						case GT:
						case EQUAL:
							state = STATE_OP;
							break;
						case TILDE:
						case BANG:
						case AT:
						case HASH:
						case PERCENT:
						case CARET:
						case AMPERSAND:
						case PIPE:
						case GRAVE:
						case QUESTION:
							state = STATE_OPX;
							break;
						case MINUS:
							nextType = SqlTokenType.OPERATOR;
							state = STATE_EOT;
							tooManyChars = 2;
							break;
						case DIV:
							state = STATE_MAY_OP_OR_FUTURE_BLOCK_COMMENT;
							break;
						case EOF:
						default:
							parseError("Cannot end and operator with [+] when there is no [~ ! @ # % ^ & | ` ?] before");
					}
					break;
				case STATE_MAY_OP_OR_FUTURE_BLOCK_COMMENT:
					switch (type) {
						case PLUS:
							state = STATE_OP_WITHOUT_FINAL_PLUS;
							break;
						case ASTERISK:
							nextType = SqlTokenType.OPERATOR;
							state = STATE_EOT;
							tooManyChars = 2;
							break;
						case LT:
						case GT:
						case EQUAL:
							state = STATE_OP;
							break;
						case TILDE:
						case BANG:
						case AT:
						case HASH:
						case PERCENT:
						case CARET:
						case AMPERSAND:
						case PIPE:
						case GRAVE:
						case QUESTION:
							state = STATE_OPX;
							break;
						case MINUS:
							state = STATE_MAY_OP_OR_FUTURE_LINE_COMMENT;
							break;
						case DIV:
							state = STATE_MAY_OP_OR_FUTURE_BLOCK_COMMENT;
							break;
						case EOF:
							nextType = SqlTokenType.OPERATOR;
							state = STATE_EOT;
							tooManyChars = 1;
							break;
						default:
							parseError("Cannot end and operator with [+] when there is no [~ ! @ # % ^ & | ` ?] before");
					}
					break;
				case STATE_MAY_OPX_OR_FUTURE_LINE_COMMENT:
					switch (type) {
						case PLUS:
						case ASTERISK:
						case LT:
						case GT:
						case EQUAL:
						case TILDE:
						case BANG:
						case AT:
						case HASH:
						case PERCENT:
						case CARET:
						case AMPERSAND:
						case PIPE:
						case GRAVE:
						case QUESTION:
							state = STATE_OPX;
							break;
						case MINUS:
							nextType = SqlTokenType.OPERATOR;
							state = STATE_EOT;
							tooManyChars = 2;
							break;
						case DIV:
							state = STATE_MAY_OPX_OR_FUTURE_BLOCK_COMMENT;
							break;
						case EOF:
						default:
							nextType = SqlTokenType.OPERATOR;
							state = STATE_EOT;
							tooManyChars = 1;
					}
					break;
				case STATE_MAY_OPX_OR_FUTURE_BLOCK_COMMENT:
					switch (type) {
						case PLUS:
						case ASTERISK:
							nextType = SqlTokenType.OPERATOR;
							state = STATE_EOT;
							tooManyChars = 2;
							break;
						case LT:
						case GT:
						case EQUAL:
						case TILDE:
						case BANG:
						case AT:
						case HASH:
						case PERCENT:
						case CARET:
						case AMPERSAND:
						case PIPE:
						case GRAVE:
						case QUESTION:
							state = STATE_OPX;
							break;
						case MINUS:
							state = STATE_MAY_OPX_OR_FUTURE_LINE_COMMENT;
							break;
						case DIV:
							state = STATE_OPX;
							break;
						case EOF:
							nextType = SqlTokenType.OPERATOR;
							state = STATE_EOT;
							tooManyChars = 1;
							break;
						default:
							parseError("Cannot end and operator with [+] when there is no [~ ! @ # % ^ & | ` ?] before");
					}
					break;
				case STATE_MAY_OP_OR_LINE_COMMENT:
					switch (type) {
						case PLUS:
							state = STATE_OP_WITHOUT_FINAL_PLUS;
							break;
						case ASTERISK:
						case LT:
						case GT:
						case EQUAL:
							state = STATE_OP;
							break;
						case TILDE:
						case BANG:
						case AT:
						case HASH:
						case PERCENT:
						case CARET:
						case AMPERSAND:
						case PIPE:
						case GRAVE:
						case QUESTION:
							state = STATE_OPX;
							break;
						case MINUS:
							state = STATE_LINE_COMMENT;
							break;
						case DIV:
							state = STATE_MAY_OP_OR_FUTURE_BLOCK_COMMENT;
							break;
						case EOF:
						default:
							nextType = SqlTokenType.OPERATOR;
							state = STATE_EOT;
							tooManyChars = 1;
					}
					break;
				case STATE_MAY_OP_OR_BLOCK_COMMENT:
					if (type == ASTERISK) {
						state = STATE_BLOCK_COMMENT;
					} else {
						state = STATE_OP;
					}
					break;
				case STATE_LINE_COMMENT:

					if (type == EOL || type == EOF) {
						nextType = SqlTokenType.LINE_COMMENT;
						state = STATE_EOT;
						tooManyChars = 1;
					}
					break;
				case STATE_BLOCK_COMMENT:
					if (type == DIV) {
						state = STATE_MAY_BLOCK_COMMENT_LEVEL;
					} else if (type == ASTERISK) {
						state = STATE_MAY_END_BLOCK_LEVEL;
					} else if (type == EOF) {
						parseError("unterminated comment at or near");
					}
					break;
				case STATE_MAY_END_BLOCK_LEVEL:
					if (type == DIV) {
						if (commentLevel > 0) {
							commentLevel--;
						} else {
							nextType = SqlTokenType.BLOCK_COMMENT;
							state = STATE_EOT;
						}
					} else if (type == EOF) {
						parseError("unterminated comment at or near");
					} else {
						state = STATE_BLOCK_COMMENT;
					}
					break;
				case STATE_MAY_BLOCK_COMMENT_LEVEL:
					if (type == ASTERISK) {
						commentLevel++;
						state = STATE_BLOCK_COMMENT;
					} else if (type == EOF) {
						parseError("unterminated comment at or near");
					} else {
						state = STATE_BLOCK_COMMENT;
					}
					break;
				case STATE_MAY_CAST_OR_SLICE:
					if (type == COLON) {
						nextType = SqlTokenType.OPERATOR;
						state = STATE_EOT;
					} else {
						nextType = SqlTokenType.OPERATOR;
						state = STATE_EOT;
						tooManyChars = 1;
					}
					break;
				default:
					parseError("Unknown state " + state);
			}
			
			sb.append(ch);
			final State debugNextState = state;
            debug(() -> "state => " + debugNextState);
            debug(() -> "text => " + sb.toString());
        }
        if (tooManyChars > 0) {
            final int debugTooManyChars = tooManyChars;
        	debug(() -> "tooManyChars = " + debugTooManyChars);
            sb.delete(sb.length() - tooManyChars, sb.length());
            position -= tooManyChars;
        }
        token = sb.toString();
        tokenType = modify(nextType, modifier, token);
        final SqlTokenType debugNextType = nextType;
        debug(() -> "==> return [" + token + "] of type " + debugNextType + " position is " + position + "/" + chars.length);
        return token;
    }
    
    private SqlTokenType modify(SqlTokenType type, Modifier modifier, String value) {
        SqlTokenType result;
        if (modifier == Modifier.NONE) {
        	if (type == SqlTokenType.IDENTIFIER && Reserved.isKeyword(value)) {
        		result = SqlTokenType.KEYWORD;
        	} else {
        		result = type;
        	}
        } else {
            if (type == SqlTokenType.STRING) {
                if (modifier == Modifier.ESCAPED) {
                    result = SqlTokenType.ESCAPED_STRING;
                } else if (modifier == Modifier.UNICODE) {
                    result = SqlTokenType.UNICODE_STRING;
                } else {
                    result = null;
                }
            } else if (type == SqlTokenType.QUOTED_IDENTIFIER) {
                if (modifier == Modifier.UNICODE) {
                    result = SqlTokenType.UNICODE_QUOTED_IDENTIFIER;
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
    
    public SqlToken nextToken() {
        nextValue();
        return new SqlToken(tokenType, token);
    }

    public SqlTokenType getCurrentType() {
        return tokenType;
    }

    public Stream<SqlToken> stream() {
    	return stream(this::nextToken);
    }
    
    public Stream<String> valueStream() {
    	return stream(this::nextValue);
    }
    
    public Stream<SqlTokenType> typeStream() {
    	return stream(() -> {
    		nextValue();
    		return getCurrentType();
    	});
    }
    
    public void forEachRemaining(Consumer<SqlToken> consumer) {
        while (hasMoreTokens()) {
            consumer.accept(nextToken());
        }
    }
    
    private <T> Stream<T> stream(Supplier<T> nextSupplier) {
    	return StreamSupport.stream(new TokenizerSpliterator<>(nextSupplier), false);
    }
    
    private void parseError() {
        throw new SqlParseException("", position, origin);
    }

    private void parseError(String msg) {
        throw new SqlParseException(msg, position, origin);
    }
    
    private static CharType toCharType(char ch) {
        CharType result;
        if (Character.isLetter(ch)) {
            result = LETTER;
        } else if (Character.isDigit(ch)) {
            result = DIGIT;
        } else {
            switch (ch) {
            	case ' ':
            	case '\t':
            		result = WHITESPACE;
            		break;
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
	            case '/':
	                result = DIV;
	                break;
	            case '*':
	                result = ASTERISK;
	                break;
	            case '<':
	                result = LT;
	                break;
	            case '>':
	                result = GT;
	                break;
	            case '~':
	                result = TILDE;
	                break;
	            case '!':
	                result = BANG;
	                break;
	            case '@':
	                result = AT;
	                break;
	            case '#':
	                result = HASH;
	                break;
	            case '%':
	                result = PERCENT;
	                break;
	            case '^':
	                result = CARET;
	                break;
	            case '|':
	                result = PIPE;
	                break;
	            case '`':
	                result = GRAVE;
	                break;
	            case '?':
	                result = QUESTION;
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
	            case '\n':
	                result = EOL;
	                break;
	            case '(':
	                result = CharType.LPAREN;
	                break;
	            case ')':
	                result = CharType.RPAREN;
	                break;
	            case '[':
	                result = CharType.LBRACK;
	                break;
	            case ']':
	                result = CharType.RBRACK;
	                break;
	            case ',':
	                result = CharType.COMMA;
	                break;
	            case ';':
	                result = CharType.SEMI;
	                break;
	            case ':':
	                result = CharType.COLON;
	                break;
	            default:
	                result = UNKNOWN;
            }
        }
        return result;
    }
    
}
