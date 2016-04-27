package org.dhatim.safesql;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Objects;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class SafeSqlUtils {
    
    private static final DateTimeFormatter TIMESTAMP_FORMATTER_WITH_TZ = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSX");
    private static final DateTimeFormatter TIMESTAMP_FORMATTER_WITHOUT_TZ = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
    //private static final DateTimeFormatter TIME_FORMATTER_WITH_TZ = DateTimeFormatter.ofPattern("HH:mm:ss.SSSX");
    private static final DateTimeFormatter TIME_FORMATTER_WITHOUT_TZ = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");
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
    
    private static final char[] HEX_CODE = "0123456789ABCDEF".toCharArray();
    
    private SafeSqlUtils() {
    }
    
    public static SafeSql fromConstant(String s) {
        Objects.requireNonNull(s);
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
     * @param sql {@code SafeSql} that will be converted with no parameters
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
        format(sb, sql, arguments);
        return sb.toSafeSql();
    }
    
    /**
     * Appends to a {@code SafeSqlBuilder} a formatted sql string using the specified arguments.
     * @param builder {@code SafeSqlBuilder} where is appened the formatted sql
     * @param sql string query with some <code>{}</code> argument place. The argument can have a number inside to force a argument index (start at 1). The escape sequence is <code>{{.*}}</code>.
     * @param arguments arguments list
     */
    public static void format(SafeSqlBuilder builder, String sql, Object... arguments) {
        Matcher matcher = PATTERN.matcher(sql);
        int lastIndex = 0;
        int argIndex = 0;
        while (matcher.find()) {
            String before = sql.substring(lastIndex, matcher.start());
            String parameter = matcher.group(1);
            lastIndex = matcher.end();
            builder.append(before);
            if (parameter.isEmpty()) {
                builder.param(arguments[argIndex++]);
            } else if (parameter.startsWith("{")) {
                builder.append(parameter);
            } else {
                int customArgIndex = Integer.parseInt(parameter);
                builder.param(arguments[customArgIndex - 1]);
            }
        }
        String lastPart = sql.substring(lastIndex);
        if (!lastPart.isEmpty()) {
            builder.append(lastPart);
        }
    }
    
    public static SafeSql concat(SafeSql s1, SafeSql s2) {
        String sql = s1.asSql() + s2.asSql();
        Object[] p1 = s1.getParameters(), p2 = s2.getParameters();
        Object[] params = Arrays.copyOf(p1, p1.length + p2.length);
        System.arraycopy(p2, 0, params, p1.length, p2.length);
        return new SafeSqlImpl(sql, params);
    }
    
    public static boolean isEmpty(SafeSql s) {
        return s.asSql().isEmpty();
    }
    
    static String escapeIdentifier(String identifier) {
        return IDENTIFIER_QUOTE_CHAR + identifier.replace(IDENTIFIER_QUOTE, ESCAPED_IDENTIFIER_QUOTE) + IDENTIFIER_QUOTE_CHAR;
    }
    
    static void appendEscapedString(StringBuilder sb, String string) {
        sb.append(STRING_QUOTE_CHAR).append(string.replace(STRING_QUOTE, ESCAPED_STRING_QUOTE)).append(STRING_QUOTE_CHAR);
    }
    
    static void appendEscapedByteArray(StringBuilder sb, byte[] array) {
        sb.append("'\\x");
        for (byte b : array) {
            sb.append(HEX_CODE[(b >> 4) & 0xF]);
            sb.append(HEX_CODE[(b & 0xF)]);
        }
        sb.append('\'');
    }

    static boolean mustEscapeIdentifier(String identifier) {
        Objects.requireNonNull(identifier, "null identifier");
        return !identifier.equals(identifier.toLowerCase()) || identifier.contains(IDENTIFIER_QUOTE);
    }
    
    static String mayEscapeIdentifier(String identifier) {
        return mustEscapeIdentifier(identifier) ? escapeIdentifier(identifier) : identifier;
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
                if (state != STATE_0) {
                    sb.append(token);
                } else {
                    appendEscapedParam(sb, parameters[index++]);
                }
                break;
            default:
                sb.append(token);
                break;
            }
        }
        return sb.toString();
    }
    
    private static void appendEscapedParam(StringBuilder sb, Object obj) {
        if (obj == null) {
            sb.append("NULL");
        } else if (obj instanceof Boolean) {
            sb.append((Boolean) obj ? "TRUE" : "FALSE");
        } else if (obj instanceof BigDecimal) {
            sb.append(obj.toString()).append("::numeric");
        } else if (obj instanceof Number) {
            sb.append(((Number) obj).toString());
        } else if (obj instanceof Timestamp) {
            ZoneId zone = ZoneId.of("UTC");
            sb.append("TIMESTAMP WITH TIME ZONE ").append(STRING_QUOTE);
            TIMESTAMP_FORMATTER_WITH_TZ.formatTo(((Timestamp) obj).toLocalDateTime().atZone(zone), sb);
            sb.append(STRING_QUOTE);
        } else if (obj instanceof Time) {
            sb.append("TIME ").append(STRING_QUOTE);
            TIME_FORMATTER_WITHOUT_TZ.formatTo(((Time) obj).toLocalTime(), sb);
            sb.append(STRING_QUOTE);
        } else if (obj instanceof Date) {
            sb.append("DATE ").append(STRING_QUOTE);
            DATE_FORMATTER.formatTo(((Date) obj).toLocalDate(), sb);
            sb.append(STRING_QUOTE);
        } else if (obj instanceof LocalDate) {
            sb.append("DATE ").append(STRING_QUOTE);
            DATE_FORMATTER.formatTo((LocalDate) obj, sb);
            sb.append(STRING_QUOTE);
        } else if (obj instanceof LocalTime) {
            sb.append("TIME ").append(STRING_QUOTE);
            TIME_FORMATTER_WITHOUT_TZ.formatTo((LocalTime) obj, sb);
            sb.append(STRING_QUOTE);
        } else if (obj instanceof LocalDateTime) {
            sb.append("TIMESTAMP ").append(STRING_QUOTE);
            TIMESTAMP_FORMATTER_WITHOUT_TZ.formatTo((LocalDateTime) obj, sb);
            sb.append(STRING_QUOTE);
        } else if (obj instanceof OffsetDateTime) {
            sb.append("TIMESTAMP WITH TIME ZONE ").append(STRING_QUOTE);
            TIMESTAMP_FORMATTER_WITH_TZ.formatTo((OffsetDateTime) obj, sb);
            sb.append(STRING_QUOTE);
        } else if (obj instanceof byte[]) {
            appendEscapedByteArray(sb, (byte[]) obj);
        } else {
            appendEscapedString(sb, obj.toString());
        }
    }

}
