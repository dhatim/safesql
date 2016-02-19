package org.dhatim.safesql;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.bind.DatatypeConverter;

public final class SafeSqlUtils {
    
    private static final DateTimeFormatter TIMESTAMP_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSX");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
    private static final int STATE_0 = 0;
    private static final int STATE_STRING = 1;
    private static final int STATE_IDENT = 2;

    private static final char STRING_QUOTE_CHAR = '\'';
    private static final String STRING_QUOTE = "'";
    private static final String ESCAPED_STRING_QUOTE = "''";
    
    private static final char IDENTIFIER_QUOTE_CHAR = '"';
    private static final String IDENTIFIER_QUOTE = "\"";
    private static final String ESCAPED_IDENTIFIER_QUOTE = "\"\"";
    
    private static final Object[] EMPTY_PARAMETERS = {};

    public static final SafeSql EMPTY = new SafeSqlImpl("", EMPTY_PARAMETERS);
    
    private static Pattern PATTERN = Pattern.compile("(?:\\{((?:\\d+|\\{(?:.*)\\})?)\\})");
    
    private SafeSqlUtils() {
    }
    
    public static SafeSql fromConstant(String s) {
        if (s.isEmpty()) {
            return EMPTY;
        }
        return new SafeSqlImpl(s, EMPTY_PARAMETERS);
    }

    public static SafeSql escape(Object o) {
        return new SafeSqlImpl("?", new Object[] { o });
    }

    public static SafeSql fromIdentifier(String identifier) {
        String sql = mustEscapeIdentifier(identifier)
                ? escapeIdentifier(identifier)
                : identifier;
        return new SafeSqlImpl(sql, EMPTY_PARAMETERS);
    }
    
    /**
     * Creates a new {@link SafeSql} that contains a literal version of the given <code>SafeSql</code>.
     * @return a literalized version of the given SafeSql
     */
    public static SafeSql literalize(SafeSql sql) {
        return fromConstant(sql.asString());
    }
    
    /**
     * Returns a formatted sql string using the specified arguments.
     * @param sql string query with some <code>{}</code> argument place. The argument can have a number inside to force a argument index (start at 1). The escape sequence is <code>{{.*}}</code>.
     * @param arguments arguments list
     * @return <code>SafeSql</code> with parameters
     */
    public static SafeSql format(String sql, Object... arguments) {
        SafeSqlBuilder sb = new SafeSqlBuilder();
        Matcher matcher = PATTERN.matcher(sql);
        int lastIndex = 0;
        int argIndex = 0;
        while (matcher.find()) {
            String before = sql.substring(lastIndex, matcher.start());
            String parameter = matcher.group(1);
            lastIndex = matcher.end();
            sb.appendConstant(before);
            if (parameter.isEmpty()) {
                sb.append(arguments[argIndex++]);
            } else if (parameter.startsWith("{")) {
                sb.appendConstant(parameter);
            } else {
                int customArgIndex = Integer.parseInt(parameter);
                sb.append(arguments[customArgIndex - 1]);
            }
        }
        String lastPart = sql.substring(lastIndex);
        if (!lastPart.isEmpty()) {
            sb.appendConstant(lastPart);
        }
        return sb.toSafeSql();
    }
    
    static String escapeIdentifier(String identifier) {
        return IDENTIFIER_QUOTE_CHAR + identifier.replace(IDENTIFIER_QUOTE, ESCAPED_IDENTIFIER_QUOTE) + IDENTIFIER_QUOTE_CHAR;
    }
    
    static String escapeString(String string) {
        return STRING_QUOTE_CHAR + string.replace(STRING_QUOTE, ESCAPED_STRING_QUOTE) + STRING_QUOTE_CHAR;
    }
    
    static String escapeByteArray(byte[] array) {
        return "'\\x" + DatatypeConverter.printHexBinary(array) + "'";
    }

    static boolean mustEscapeIdentifier(String identifier) {
        Objects.requireNonNull(identifier, "null identifier");
        return !identifier.equals(identifier.toLowerCase()) || identifier.contains(IDENTIFIER_QUOTE);
    }

    static String toString(SafeSql sql) {
        Object[] parameters = sql.getParameters();
        StringBuilder sb = new StringBuilder();
        int index = 0;
        StringTokenizer tokenizer = new StringTokenizer(sql.asSql(), "\"'?", true);
        int state = 0;
        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();
            switch (token) {
            case "\"":
                if (state == STATE_0) {
                    state = STATE_IDENT;
                } else if (state == STATE_IDENT) {
                    state = STATE_0;
                }
                sb.append(token);
                break;
            case "'":
                if (state == STATE_0) {
                    state = STATE_STRING;
                } else if (state == STATE_STRING) {
                    state = STATE_0;
                }
                sb.append(token);
                break;
            case "?":
                sb.append(state != STATE_0 ? token : escapeParam(parameters[index++]));
                break;
            default:
                sb.append(token);
            }
        }
        return sb.toString();
    }

    private static String escapeParam(Object obj) {
        if (obj == null) {
            return "NULL";
        } else if (obj instanceof Boolean) {
            return (Boolean) obj ? "TRUE" : "FALSE";
        } else if (obj instanceof BigDecimal) {
            return obj.toString() + "::numeric"; 
        } else if (obj instanceof Number) {
            return ((Number) obj).toString();
        } else if (obj instanceof Timestamp) {
            return STRING_QUOTE + TIMESTAMP_FORMATTER.format(((Timestamp) obj).toLocalDateTime()) + STRING_QUOTE;
        } else if (obj instanceof Date) {
            return STRING_QUOTE + DATE_FORMATTER.format(((Date) obj).toLocalDate()) + STRING_QUOTE;
        } else if (obj instanceof byte[]) {
            return escapeByteArray((byte[]) obj);
        } else {
            return escapeString(obj.toString());
        }
    }

}
