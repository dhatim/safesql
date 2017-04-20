package org.dhatim.safesql;

import java.util.stream.Stream;

public interface SafeSqlAppendable {

    /**
     * include integer parameter in SQL with a placeholder <b>?</b>
     *
     * @param num integer parameter
     * @return a reference of this object
     */
    SafeSqlAppendable param(int num);

    /**
     * include long parameter in SQL with a placeholder <b>?</b>
     *
     * @param num long parameter
     * @return a reference of this object
     */
    SafeSqlAppendable param(long num);

    /**
     * include double parameter in SQL with a placeholder <b>?</b>
     *
     * @param num double parameter
     * @return a reference of this object
     */
    SafeSqlAppendable param(double num);

    /**
     * include boolean parameter in SQL with a placeholder <b>?</b>
     *
     * @param bool boolean parameter
     * @return a reference of this object
     */
    SafeSqlAppendable param(boolean bool);

    /**
     * include generic parameter in SQL with a placeholder <b>?</b>
     *
     * @param obj object parameter
     * @return a reference of this object
     */
    SafeSqlAppendable param(Object obj);

    /**
     * include multiple parameters in SQL with placeholders <b>?</b>
     *
     * @param parameters list of parameter to include
     * @return a reference of this object
     */
    SafeSqlAppendable params(Object... parameters);

    /**
     * include multiple parameters in SQL with placeholders <b>?</b>
     *
     * @param iterable {@link Iterable} of parameter to include
     * @return a reference of this object
     */
    SafeSqlAppendable params(Iterable<?> iterable);

    /**
     * include multiple parameters in SQL with placeholders <b>?</b>
     *
     * @param stream stream of parameter to include
     * @return a reference of this object
     */
    SafeSqlAppendable params(Stream<?> stream);

    /**
     * append a {@link SafeSql} to SQL
     *
     * @param sql {@link SafeSql} to append to the final SQL
     * @return a reference of this object
     */
    SafeSqlAppendable append(SafeSql sql);
    SafeSqlAppendable append(SafeSqlizable sqlizable);
    SafeSqlAppendable append(String s);
    SafeSqlAppendable append(char ch);
    SafeSqlAppendable append(int i);
    SafeSqlAppendable append(long l);

    /**
     * write a string literal by escaping
     *
     * @param s this string as literal string in SQL code
     * @return a reference to this object.
     */
    SafeSqlAppendable literal(String s);

    /**
     * Write a byte array as literal in PostgreSQL
     *
     * @param bytes bytes to write as literal
     * @return a reference to this object.
     */
    SafeSqlAppendable literal(byte[] bytes);
    SafeSqlAppendable identifier(String identifier);
    SafeSqlAppendable identifier(String container, String identifier);

    /**
     * Appends a formatted sql string using the specified arguments.
     *
     * @param sql string query with some <code>{}</code> argument place. The
     * argument can have a number inside to force a argument index (start at 1).
     * The escape sequence is <code>{{.*}}</code>.
     * @param args arguments list
     * @return a reference to this object.
     */
    SafeSqlAppendable format(String sql, Object... args);

    SafeSqlAppendable joined(String delimiter, Iterable<String> iterable);
    SafeSqlAppendable joined(String delimiter, String prefix, String suffix, Iterable<String> iterable);
    SafeSqlAppendable joined(String delimiter, Stream<String> stream);
    SafeSqlAppendable joined(String delimiter, String prefix, String suffix, Stream<String> stream);

    SafeSqlAppendable joinedSafeSqls(String delimiter, Iterable<SafeSql> iterable);
    SafeSqlAppendable joinedSafeSqls(String delimiter, String prefix, String suffix, Iterable<SafeSql> iterable);
    SafeSqlAppendable joinedSafeSqls(String delimiter, Stream<SafeSql> stream);
    SafeSqlAppendable joinedSafeSqls(String delimiter, String prefix, String suffix, Stream<SafeSql> stream);

    SafeSqlAppendable joinedSafeSqls(SafeSql delimiter, Iterable<SafeSql> iterable);
    SafeSqlAppendable joinedSafeSqls(SafeSql delimiter, SafeSql prefix, SafeSql suffix, Iterable<SafeSql> iterable);
    SafeSqlAppendable joinedSafeSqls(SafeSql delimiter, Stream<SafeSql> stream);
    SafeSqlAppendable joinedSafeSqls(SafeSql delimiter, SafeSql prefix, SafeSql suffix, Stream<SafeSql> stream);

    SafeSqlAppendable joinedSqlizables(String delimiter, Iterable<? extends SafeSqlizable> iterable);
    SafeSqlAppendable joinedSqlizables(String delimiter, String prefix, String suffix, Iterable<? extends SafeSqlizable> iterable);
    SafeSqlAppendable joinedSqlizables(String delimiter, Stream<? extends SafeSqlizable> stream);
    SafeSqlAppendable joinedSqlizables(String delimiter, String prefix, String suffix, Stream<? extends SafeSqlizable> stream);

    SafeSqlAppendable joinedSqlizables(SafeSql delimiter, Iterable<? extends SafeSqlizable> iterable);
    SafeSqlAppendable joinedSqlizables(SafeSql delimiter, SafeSql prefix, SafeSql suffix, Iterable<? extends SafeSqlizable> iterable);
    SafeSqlAppendable joinedSqlizables(SafeSql delimiter, Stream<? extends SafeSqlizable> stream);
    SafeSqlAppendable joinedSqlizables(SafeSql delimiter, SafeSql prefix, SafeSql suffix, Stream<? extends SafeSqlizable> stream);

}
