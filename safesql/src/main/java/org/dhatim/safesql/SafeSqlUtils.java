package org.dhatim.safesql;

import java.math.BigDecimal;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.MissingFormatArgumentException;
import java.util.Objects;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class SafeSqlUtils {

    private static final DateTimeFormatter TIMESTAMP_FORMATTER_WITH_TZ = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSX");

    private static final DateFormat TIME_FORMAT_WITH_TZ = new SimpleDateFormat("hh:mm:ss:SSSXXX");
    private static final DateFormat TIMESTAMP_FORMAT_WITH_TZ = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss:SSSXXX");

    private static final char STRING_QUOTE_CHAR = '\'';
    private static final String STRING_QUOTE = "'";
    private static final String ESCAPED_STRING_QUOTE = "''";

    private static final char IDENTIFIER_QUOTE_CHAR = '"';
    private static final String IDENTIFIER_QUOTE = "\"";
    private static final String ESCAPED_IDENTIFIER_QUOTE = "\"\"";

    public static final SafeSql EMPTY = new StringSafeSqlImpl("");

    private static final Pattern PATTERN = Pattern.compile("(?:\\{((?:\\d+|\\{(?:.*)\\})?)\\})");

    private static final Pattern DEFAULT_LIKE_VALUE_ESCAPE = Pattern.compile("([\\_%])");

    private SafeSqlUtils() {
    }

    public static SafeSql fromConstant(String s) {
        Objects.requireNonNull(s);
        if (s.isEmpty()) {
            return EMPTY;
        }
        return new StringSafeSqlImpl(s);
    }

    public static SafeSql escape(Object o) {
        return new SafeSqlImpl("?", new Object[]{o});
    }

    public static SafeSql fromIdentifier(String identifier) {
        String sql = mustEscapeIdentifier(identifier)
                ? escapeIdentifier(identifier)
                : identifier;
        return new StringSafeSqlImpl(sql);
    }

    /**
     * Creates a new {@link SafeSql} that contains a literal version of the
     * given <code>SafeSql</code>.
     *
     * @param sql {@code SafeSql} that will be converted with no parameters
     * @return a literalized version of the given SafeSql
     */
    public static SafeSql literalize(SafeSql sql) {
        return SafeSqlRewriter.create(SafeSqlUtils::appendEscapedParam).write(sql);
    }

    /**
     * Returns a formatted sql string using the specified arguments.
     *
     * @param sql string query with some <code>{}</code> argument place. The
     * argument can have a number inside to force a argument index (start at 1).
     * The escape sequence is <code>{{.*}}</code>.
     * @param arguments arguments list
     * @return <code>SafeSql</code> with parameters
     */
    public static SafeSql format(String sql, Object... arguments) {
        SafeSqlBuilder sb = new SafeSqlBuilder();
        formatTo(sb, sql, arguments);
        return sb.toSafeSql();
    }

    /**
     * Appends to a {@code SafeSqlBuilder} a formatted sql string using the
     * specified arguments.
     *
     * @param builder {@code SafeSqlBuilder} where is appened the formatted sql
     * @param sql string query with some <code>{}</code> argument place. The
     * argument can have a number inside to force a argument index (start at 1).
     * The escape sequence is <code>{{.*}}</code>.
     * @param arguments arguments list
     */
    public static void formatTo(SafeSqlBuilder builder, String sql, Object... arguments) {
        Matcher matcher = PATTERN.matcher(sql);
        int lastIndex = 0;
        int argIndex = 0;
        while (matcher.find()) {
            String before = sql.substring(lastIndex, matcher.start());
            String parameter = matcher.group(1);
            lastIndex = matcher.end();
            builder.append(before);
            if (parameter.isEmpty()) {
                if (argIndex >= arguments.length) {
                    throw new MissingFormatArgumentException("Argument " + argIndex);
                }
                builder.param(arguments[argIndex++]);
            } else if (parameter.startsWith("{")) {
                builder.append(parameter);
            } else {
                int customArgIndex = Integer.parseInt(parameter) - 1;
                if (customArgIndex < 0 || customArgIndex >= arguments.length) {
                    throw new MissingFormatArgumentException("Argument " + customArgIndex);
                }
                builder.param(arguments[customArgIndex]);
            }
        }
        String lastPart = sql.substring(lastIndex);
        if (!lastPart.isEmpty()) {
            builder.append(lastPart);
        }
    }

    public static SafeSql concat(SafeSql s1, SafeSql s2) {
        String sql = s1.asSql() + s2.asSql();
        Object[] p1 = s1.getParameters();
        Object[] p2 = s2.getParameters();
        Object[] params = Arrays.copyOf(p1, p1.length + p2.length);
        System.arraycopy(p2, 0, params, p1.length, p2.length);
        return new SafeSqlImpl(sql, params);
    }

    public static boolean isEmpty(SafeSql s) {
        return s.asSql().isEmpty();
    }

    public static String escapeLikeValue(String s, char escapeChar) {
        return Pattern.compile("([" + escapeChar + "_%])").matcher(s).replaceAll(escapeChar + "$1");
    }

    public static String escapeLikeValue(String s) {
        return DEFAULT_LIKE_VALUE_ESCAPE.matcher(s).replaceAll("\\\\$1");
    }

    static String escapeIdentifier(String identifier) {
        return IDENTIFIER_QUOTE_CHAR + identifier.replace(IDENTIFIER_QUOTE, ESCAPED_IDENTIFIER_QUOTE) + IDENTIFIER_QUOTE_CHAR;
    }

    static String escapeString(String string) {
        return STRING_QUOTE_CHAR + string.replace(STRING_QUOTE, ESCAPED_STRING_QUOTE) + STRING_QUOTE_CHAR;
    }

    static boolean mustEscapeIdentifier(String identifier) {
        Objects.requireNonNull(identifier, "null identifier");
        for (int i=0; i<identifier.length(); i++) {
            char ch = identifier.charAt(i);
            if (i == 0) {
                if (!(Character.isLetter(ch) || ch == '_')) {
                    return true;
                }
            } else {
                if (!(Character.isLetterOrDigit(ch) || ch == '_' || ch == '$')) {
                    return true;
                }
            }
            if (Character.isLetter(ch) && !Character.isLowerCase(ch)) {
                return true;
            }
        }
        return false;
    }

    static String mayEscapeIdentifier(String identifier) {
        return mustEscapeIdentifier(identifier) ? escapeIdentifier(identifier) : identifier;
    }

    static String toString(SafeSql sql) {
        return literalize(sql).asSql();
    }

    private static void appendEscapedParam(SafeSqlBuilder sb, Object obj) {
        if (obj == null) {
            sb.append("NULL");
        } else if (obj instanceof Boolean) {
            sb.append((Boolean) obj ? "TRUE" : "FALSE");
        } else if (obj instanceof BigDecimal) {
            sb.append(obj.toString()).append("::numeric");
        } else if (obj instanceof Number) {
            sb.append(((Number) obj).toString());
        } else if (obj instanceof Timestamp) {
            sb.append("TIMESTAMP WITH TIME ZONE ").append(STRING_QUOTE);
            sb.append(TIMESTAMP_FORMAT_WITH_TZ.format((Timestamp) obj));
            sb.append(STRING_QUOTE);
        } else if (obj instanceof Time) {
            sb.append("TIME WITH TIME ZONE ").append(STRING_QUOTE);
            sb.append(TIME_FORMAT_WITH_TZ.format((Time) obj));
            sb.append(STRING_QUOTE);
        } else if (obj instanceof java.sql.Date) {
            sb.append("DATE ").append(STRING_QUOTE);
            sb.append(((java.sql.Date) obj).toLocalDate().toString());
            sb.append(STRING_QUOTE);
        } else if (obj instanceof LocalDate) {
            sb.append("DATE ").append(STRING_QUOTE);
            sb.append(((LocalDate) obj).toString());
            sb.append(STRING_QUOTE);
        } else if (obj instanceof LocalTime) {
            sb.append("TIME ").append(STRING_QUOTE);
            sb.append(((LocalTime) obj).toString());
            sb.append(STRING_QUOTE);
        } else if (obj instanceof LocalDateTime) {
            sb.append("TIMESTAMP ").append(STRING_QUOTE);
            LocalDateTime other = (LocalDateTime) obj;
            sb.append(other.toLocalDate().toString()).append(' ').append(other.toLocalTime().toString());
            sb.append(STRING_QUOTE);
        } else if (obj instanceof OffsetDateTime) {
            sb.append("TIMESTAMP WITH TIME ZONE ").append(STRING_QUOTE);
            sb.append(TIMESTAMP_FORMATTER_WITH_TZ.format((OffsetDateTime) obj));
            sb.append(STRING_QUOTE);
        } else if (obj instanceof UUID) {
            sb.append("UUID ").literal(obj.toString());
        } else if (obj instanceof SafeSqlLiteralizable) {
            ((SafeSqlLiteralizable) obj).appendLiteralized(sb);
        } else if (obj instanceof byte[]) {
            sb.append("BYTEA ").literal((byte[]) obj);
        } else {
            sb.literal(obj.toString());
        }
    }

}
