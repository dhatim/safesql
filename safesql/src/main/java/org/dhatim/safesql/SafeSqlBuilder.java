package org.dhatim.safesql;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

public class SafeSqlBuilder implements SafeSqlizable {
    
    static class Position {
        
        private final int sqlPosition;
        private final int paramPosition;
        
        private Position(int sqlPosition, int paramPosition) {
            this.sqlPosition = sqlPosition;
            this.paramPosition = paramPosition;
        }
        
    }
    
    private static final char[] HEX_CODE = "0123456789ABCDEF".toCharArray();
    
    private static final SafeSql DEFAULT_SEPARATOR = SafeSqlUtils.fromConstant(", ");

    private final StringBuilder sqlBuilder = new StringBuilder();
    private final List<Object> parameters = new ArrayList<>();
    
    public SafeSqlBuilder param(int num) {
        appendObject(num);
        return this;
    }
    
    public SafeSqlBuilder param(long num) {
        appendObject(num);
        return this;
    }

    public SafeSqlBuilder param(double num) {
        appendObject(num);
        return this;
    }
    
    public SafeSqlBuilder param(boolean bool) {
        appendObject(bool);
        return this;
    }
    
    public SafeSqlBuilder param(BigDecimal num) {
        appendObject(num);
        return this;
    }
    
    public SafeSqlBuilder param(Object obj) {
        appendObject(obj);
        return this;
    }
    
    public SafeSqlBuilder params(Object... parameters) {
        switch (parameters.length) {
            case 0: break; // Do nothing 
            case 1: param(parameters[0]); break;
            case 2: params(parameters[0], parameters[1]); break;
            case 3: params(parameters[0], parameters[1], parameters[2]);
            default: params(DEFAULT_SEPARATOR, Arrays.stream(parameters));
        }
        return this;
    }
    
    public SafeSqlBuilder params(Object param1, Object param2) {
        appendObject(param1);
        append(DEFAULT_SEPARATOR);
        appendObject(param2);
        return this;
    }
    
    public SafeSqlBuilder params(Object param1, Object param2, Object param3) {
        appendObject(param1);
        append(DEFAULT_SEPARATOR);
        appendObject(param2);
        append(DEFAULT_SEPARATOR);
        appendObject(param3);
        return this;
    }
    
    public SafeSqlBuilder params(Collection<?> collection) {
        return params(DEFAULT_SEPARATOR, collection.stream());
    }
    
    public SafeSqlBuilder params(Stream<?> stream) {
        return params(DEFAULT_SEPARATOR, stream);
    }
    
    public SafeSqlBuilder params(String delimiter, Collection<?> collection) {
        return params(SafeSqlUtils.fromConstant(delimiter), collection.stream());
    }
    
    public SafeSqlBuilder params(String delimiter, String prefix, String suffix, Collection<?> collection) {
        return params(SafeSqlUtils.fromConstant(delimiter), SafeSqlUtils.fromConstant(prefix), SafeSqlUtils.fromConstant(suffix), collection.stream());
    }
    
    public SafeSqlBuilder params(String delimiter, Stream<?> stream) {
        return params(SafeSqlUtils.fromConstant(delimiter), stream);
    }
    
    public SafeSqlBuilder params(String delimiter, String prefix, String suffix, Stream<?> stream) {
        return params(SafeSqlUtils.fromConstant(delimiter), SafeSqlUtils.fromConstant(prefix), SafeSqlUtils.fromConstant(suffix), stream);
    }
    
    public SafeSqlBuilder params(SafeSql delimiter, Collection<?> collection) {
        return params(delimiter, collection.stream());
    }
    
    public SafeSqlBuilder params(SafeSql delimiter, SafeSql prefix, SafeSql suffix, Collection<?> collection) {
        return params(delimiter, prefix, suffix, collection.stream());
    }
    
    public SafeSqlBuilder params(SafeSql delimiter, Stream<?> stream) {
        SafeSqlJoiner joiner = stream.collect(() -> new SafeSqlJoiner(delimiter), SafeSqlJoiner::addParameter, SafeSqlJoiner::merge);
        joiner.appendTo(this);
        return this;
    }
    
    public SafeSqlBuilder params(SafeSql delimiter, SafeSql prefix, SafeSql suffix, Stream<?> stream) {
        SafeSqlJoiner joiner = stream.collect(() -> new SafeSqlJoiner(delimiter, prefix, suffix), SafeSqlJoiner::addParameter, SafeSqlJoiner::merge);
        joiner.appendTo(this);
        return this;
    }
    
    public SafeSqlBuilder append(SafeSql sql) {
        sqlBuilder.append(sql.asSql());
        parameters.addAll(Arrays.asList(sql.getParameters()));
        return this;
    }
    
    public SafeSqlBuilder appendFormatted(String sql, Object... args) {
        SafeSqlUtils.formatTo(this, sql, args);
        return this;
    }
    
    public SafeSqlBuilder appendJoined(String delimiter, Collection<? extends SafeSqlizable> collection) {
        return appendJoined(SafeSqlUtils.fromConstant(delimiter), collection.stream());
    }
    
    public SafeSqlBuilder appendJoined(String delimiter, String prefix, String suffix, Collection<? extends SafeSqlizable> collection) {
        return appendJoined(delimiter, prefix, suffix, collection.stream());
    }
    
    public SafeSqlBuilder appendJoined(String delimiter, Stream<? extends SafeSqlizable> stream) {
        return appendJoined(SafeSqlUtils.fromConstant(delimiter), stream);
    }

    public SafeSqlBuilder appendJoined(String delimiter, String prefix, String suffix, Stream<? extends SafeSqlizable> stream) {
        return appendJoined(SafeSqlUtils.fromConstant(delimiter), SafeSqlUtils.fromConstant(prefix), SafeSqlUtils.fromConstant(suffix), stream);
    }
    
    public SafeSqlBuilder appendJoined(SafeSql delimiter, Collection<? extends SafeSqlizable> collection) {
        return appendJoined(delimiter, collection.stream());
    }
    
    public SafeSqlBuilder appendJoined(SafeSql delimiter, SafeSql prefix, SafeSql suffix, Collection<? extends SafeSqlizable> collection) {
        return appendJoined(delimiter, prefix, suffix, collection.stream());
    }
    
    public SafeSqlBuilder appendJoined(SafeSql delimiter, Stream<? extends SafeSqlizable> stream) {
        SafeSqlJoiner joiner = stream.collect(() -> new SafeSqlJoiner(delimiter), SafeSqlJoiner::add, SafeSqlJoiner::merge);
        joiner.appendTo(this);
        return this;
    }
    
    public SafeSqlBuilder appendJoined(SafeSql delimiter, SafeSql prefix, SafeSql suffix, Stream<? extends SafeSqlizable> stream) {
        SafeSqlJoiner joiner = stream.collect(() -> new SafeSqlJoiner(delimiter, prefix, suffix), SafeSqlJoiner::add, SafeSqlJoiner::merge);
        joiner.appendTo(this);
        return this;
    }
    
    public SafeSqlBuilder append(SafeSqlizable sqlizable) {
        sqlizable.appendTo(this);
        return this;
    }

    public SafeSqlBuilder append(String s) {
        sqlBuilder.append(s);
        return this;
    }
    
    public SafeSqlBuilder append(char ch) {
        sqlBuilder.append(ch);
        return this;
    }
    
    public SafeSqlBuilder append(int i) {
        sqlBuilder.append(i);
        return this;
    }
    
    /**
     * write a string literal by escaping 
     * @param s Append this string as literal string in SQL code
     * @return a reference to this object.
     */
    public SafeSqlBuilder appendStringLiteral(String s) {
        sqlBuilder.append(SafeSqlUtils.escapeString(s));
        return this;
    }
    
    /**
     * Write a byte array as literal in PostgreSQL
     * @param bytes bytes to write as literal
     * @return a reference to this object.
     */
    public SafeSqlBuilder appendBytesLiteral(byte[] bytes) {
        sqlBuilder.append("'\\x");
        for (byte b : bytes) {
            sqlBuilder.append(HEX_CODE[(b >> 4) & 0xF]);
            sqlBuilder.append(HEX_CODE[(b & 0xF)]);
        }
        sqlBuilder.append('\'');
        return this;
    }

    public SafeSqlBuilder appendIdentifier(String identifier) {
        sqlBuilder.append(SafeSqlUtils.mayEscapeIdentifier(identifier));
        return this;
    }
    
    public SafeSqlBuilder appendIdentifier(String container, String identifier) {
        sqlBuilder.append(SafeSqlUtils.mayEscapeIdentifier(container)).append('.').append(SafeSqlUtils.mayEscapeIdentifier(identifier));
        return this;
    }

    @Override
    public SafeSql toSafeSql() {
        return new SafeSqlImpl(sqlBuilder.toString(), parameters.toArray());
    }
    
    @Override
    public void appendTo(SafeSqlBuilder builder) {
        builder.parameters.addAll(parameters);
        builder.sqlBuilder.append(sqlBuilder);
    }

    private void appendObject(Object o) {
        sqlBuilder.append('?');
        parameters.add(o);
    }
    
    Position getLength() {
        return new Position(sqlBuilder.length(), parameters.size());
    }
    
    void setLength(Position position) {
        sqlBuilder.setLength(position.sqlPosition);
        int currentSize = parameters.size();
        if (position.paramPosition < currentSize) {
            parameters.subList(position.paramPosition, currentSize).clear();
        }
    }
    
    void append(SafeSqlBuilder other, Position after) {
        sqlBuilder.append(other.sqlBuilder, after.sqlPosition, other.sqlBuilder.length());
        int afterLength = after.paramPosition;
        parameters.addAll(Arrays.asList(other.parameters).subList(afterLength, other.parameters.size() - afterLength));
    }
    
    static Position getLength(SafeSql sql) {
        return new Position(sql.asSql().length(), sql.getParameters().length);
    }
    
}
