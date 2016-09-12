package org.dhatim.safesql;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.stream.Stream;

public interface SafeSqlAppendable {

    public SafeSqlAppendable param(int num);
    public SafeSqlAppendable param(long num);
    public SafeSqlAppendable param(double num);
    public SafeSqlAppendable param(boolean bool);
    public SafeSqlAppendable param(BigDecimal num);
    public SafeSqlAppendable param(Object obj);
    public SafeSqlAppendable params(Object param1, Object param2);
    public SafeSqlAppendable params(Object param1, Object param2, Object param3);
    public SafeSqlAppendable params(Object... parameters);
    public SafeSqlAppendable params(Collection<?> collection);
    public SafeSqlAppendable params(Stream<?> stream);
    public SafeSqlAppendable append(SafeSql sql);
    public SafeSqlAppendable append(SafeSqlizable sqlizable);
    public SafeSqlAppendable append(String s);
    public SafeSqlAppendable append(char ch);
    public SafeSqlAppendable append(int i);
    
    /**
     * write a string literal by escaping
     *
     * @param s this string as literal string in SQL code
     * @return a reference to this object.
     */
    public SafeSqlAppendable appendStringLiteral(String s);
    public SafeSqlAppendable appendFormatted(String sql, Object... args);
    public SafeSqlAppendable appendJoined(String delimiter, Collection<? extends SafeSqlizable> collection);
    public SafeSqlAppendable appendJoined(String delimiter, String prefix, String suffix, Collection<? extends SafeSqlizable> collection);
    public SafeSqlAppendable appendJoined(String delimiter, Stream<? extends SafeSqlizable> stream);
    public SafeSqlAppendable appendJoined(String delimiter, String prefix, String suffix, Stream<? extends SafeSqlizable> stream);
    public SafeSqlAppendable appendJoined(SafeSql delimiter, Collection<? extends SafeSqlizable> collection);
    public SafeSqlAppendable appendJoined(SafeSql delimiter, SafeSql prefix, SafeSql suffix, Collection<? extends SafeSqlizable> collection);
    public SafeSqlAppendable appendJoined(SafeSql delimiter, Stream<? extends SafeSqlizable> stream);
    public SafeSqlAppendable appendJoined(SafeSql delimiter, SafeSql prefix, SafeSql suffix, Stream<? extends SafeSqlizable> stream);
    
    /**
     * Write a byte array as literal in PostgreSQL
     *
     * @param bytes bytes to write as literal
     * @return a reference to this object.
     */
    public SafeSqlAppendable appendBytesLiteral(byte[] bytes);
    public SafeSqlAppendable appendIdentifier(String identifier);
    public SafeSqlAppendable appendIdentifier(String container, String identifier);
    
}
