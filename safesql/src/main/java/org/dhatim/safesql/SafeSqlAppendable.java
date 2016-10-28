package org.dhatim.safesql;

import java.math.BigDecimal;
import java.util.stream.Stream;

public interface SafeSqlAppendable {

    SafeSqlAppendable param(int num);
    SafeSqlAppendable param(long num);
    SafeSqlAppendable param(double num);
    SafeSqlAppendable param(boolean bool);
    SafeSqlAppendable param(BigDecimal num);
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
    SafeSqlAppendable appendStringLiteral(String s);
    SafeSqlAppendable appendFormatted(String sql, Object... args);
    
    SafeSqlAppendable appendJoined(String delimiter, Iterable<SafeSql> iterable);
    SafeSqlAppendable appendJoined(String delimiter, String prefix, String suffix, Iterable<SafeSql> iterable);
    SafeSqlAppendable appendJoined(String delimiter, Stream<SafeSql> stream);
    SafeSqlAppendable appendJoined(String delimiter, String prefix, String suffix, Stream<SafeSql> stream);
    
    SafeSqlAppendable appendJoined(SafeSql delimiter, Iterable<SafeSql> iterable);
    SafeSqlAppendable appendJoined(SafeSql delimiter, SafeSql prefix, SafeSql suffix, Iterable<SafeSql> iterable);
    SafeSqlAppendable appendJoined(SafeSql delimiter, Stream<SafeSql> stream);
    SafeSqlAppendable appendJoined(SafeSql delimiter, SafeSql prefix, SafeSql suffix, Stream<SafeSql> stream);
    
    SafeSqlAppendable appendJoinedSqlizable(String delimiter, Iterable<? extends SafeSqlizable> iterable);
    SafeSqlAppendable appendJoinedSqlizable(String delimiter, String prefix, String suffix, Iterable<? extends SafeSqlizable> iterable);
    SafeSqlAppendable appendJoinedSqlizable(String delimiter, Stream<? extends SafeSqlizable> stream);
    SafeSqlAppendable appendJoinedSqlizable(String delimiter, String prefix, String suffix, Stream<? extends SafeSqlizable> stream);
    
    SafeSqlAppendable appendJoinedSqlizable(SafeSql delimiter, Iterable<? extends SafeSqlizable> iterable);
    SafeSqlAppendable appendJoinedSqlizable(SafeSql delimiter, SafeSql prefix, SafeSql suffix, Iterable<? extends SafeSqlizable> iterable);
    SafeSqlAppendable appendJoinedSqlizable(SafeSql delimiter, Stream<? extends SafeSqlizable> stream);
    SafeSqlAppendable appendJoinedSqlizable(SafeSql delimiter, SafeSql prefix, SafeSql suffix, Stream<? extends SafeSqlizable> stream);
    
    
    /**
     * Write a byte array as literal in PostgreSQL
     *
     * @param bytes bytes to write as literal
     * @return a reference to this object.
     */
    SafeSqlAppendable appendBytesLiteral(byte[] bytes);
    SafeSqlAppendable appendIdentifier(String identifier);
    SafeSqlAppendable appendIdentifier(String container, String identifier);
    
}
