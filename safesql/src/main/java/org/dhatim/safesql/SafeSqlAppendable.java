package org.dhatim.safesql;

import java.util.stream.Stream;

public interface SafeSqlAppendable {

    SafeSqlAppendable param(int num);
    SafeSqlAppendable param(long num);
    SafeSqlAppendable param(double num);
    SafeSqlAppendable param(boolean bool);
    SafeSqlAppendable param(Object obj);
    SafeSqlAppendable params(Object param1, Object param2);
    SafeSqlAppendable params(Object param1, Object param2, Object param3);
    SafeSqlAppendable params(Object param1, Object param2, Object param3, Object... others);
    SafeSqlAppendable params(Object... parameters);
    SafeSqlAppendable params(Iterable<?> iterable);
    SafeSqlAppendable params(Stream<?> stream);

    SafeSqlAppendable append(SafeSql sql);
    SafeSqlAppendable append(SafeSqlizable sqlizable);
    SafeSqlAppendable append(String s);
    SafeSqlAppendable append(char ch);
    SafeSqlAppendable append(int i);

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

    SafeSqlAppendable format(String sql, Object... args);

    SafeSqlAppendable join(String delimiter, Iterable<String> iterable);
    SafeSqlAppendable join(String delimiter, String prefix, String suffix, Iterable<String> iterable);
    SafeSqlAppendable join(String delimiter, Stream<String> stream);
    SafeSqlAppendable join(String delimiter, String prefix, String suffix, Stream<String> stream);

    SafeSqlAppendable joinSafeSqls(String delimiter, Iterable<SafeSql> iterable);
    SafeSqlAppendable joinSafeSqls(String delimiter, String prefix, String suffix, Iterable<SafeSql> iterable);
    SafeSqlAppendable joinSafeSqls(String delimiter, Stream<SafeSql> stream);
    SafeSqlAppendable joinSafeSqls(String delimiter, String prefix, String suffix, Stream<SafeSql> stream);

    SafeSqlAppendable joinSafeSqls(SafeSql delimiter, Iterable<SafeSql> iterable);
    SafeSqlAppendable joinSafeSqls(SafeSql delimiter, SafeSql prefix, SafeSql suffix, Iterable<SafeSql> iterable);
    SafeSqlAppendable joinSafeSqls(SafeSql delimiter, Stream<SafeSql> stream);
    SafeSqlAppendable joinSafeSqls(SafeSql delimiter, SafeSql prefix, SafeSql suffix, Stream<SafeSql> stream);

    SafeSqlAppendable joinSqlizables(String delimiter, Iterable<? extends SafeSqlizable> iterable);
    SafeSqlAppendable joinSqlizables(String delimiter, String prefix, String suffix, Iterable<? extends SafeSqlizable> iterable);
    SafeSqlAppendable joinSqlizables(String delimiter, Stream<? extends SafeSqlizable> stream);
    SafeSqlAppendable joinSqlizables(String delimiter, String prefix, String suffix, Stream<? extends SafeSqlizable> stream);

    SafeSqlAppendable joinSqlizables(SafeSql delimiter, Iterable<? extends SafeSqlizable> iterable);
    SafeSqlAppendable joinSqlizables(SafeSql delimiter, SafeSql prefix, SafeSql suffix, Iterable<? extends SafeSqlizable> iterable);
    SafeSqlAppendable joinSqlizables(SafeSql delimiter, Stream<? extends SafeSqlizable> stream);
    SafeSqlAppendable joinSqlizables(SafeSql delimiter, SafeSql prefix, SafeSql suffix, Stream<? extends SafeSqlizable> stream);

}
