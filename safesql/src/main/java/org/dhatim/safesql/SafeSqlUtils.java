package org.dhatim.safesql;

import java.util.MissingFormatArgumentException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class SafeSqlUtils {

    public static final SafeSql EMPTY = Dialect.getDefault().empty();

    private static final Pattern PATTERN = Pattern.compile("(?:\\{((?:\\d+|\\{(?:.*)\\})?)\\})");

    private SafeSqlUtils() {
    }

    public static SafeSql fromConstant(String s) {
        return Dialect.getDefault().fromConstant(s);
    }

    public static SafeSql escape(Object o) {
        return Dialect.getDefault().fromParameter(o);
    }

    public static SafeSql fromIdentifier(String identifier) {
        return Dialect.getDefault().fromIdentifier(identifier);
    }

    /**
     * Creates a new {@link SafeSql} that contains a literal version of the
     * given <code>SafeSql</code>.
     *
     * @param sql {@code SafeSql} that will be converted with no parameters
     * @return a literalized version of the given SafeSql
     */
    public static SafeSql literalize(SafeSql sql) {
        return Dialect.getDefault().literalize(sql);
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
        return new ConcatenatedSafeSql(s1, s2);
    }

    public static boolean isEmpty(SafeSql s) {
        return s.asSql().isEmpty();
    }

    public static String escapeLikeValue(String s, char escapeChar) {
        return Dialect.getDefault().escapeLikeValue(s, escapeChar);
    }

    public static String escapeLikeValue(String s) {
        return Dialect.getDefault().escapeLikeValue(s);
    }

}
