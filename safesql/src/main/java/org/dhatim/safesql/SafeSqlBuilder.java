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
    
    public SafeSqlBuilder param(String s) {
        appendObject(s);
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
    
    public SafeSqlBuilder append(SafeSql sql) {
        sqlBuilder.append(sql.asSql());
        parameters.addAll(Arrays.asList(sql.getParameters()));
        return this;
    }
    
    public SafeSqlBuilder appendFormatted(String sql, Object... args) {
        SafeSqlUtils.format(this, sql, args);
        return this;
    }
    
    public SafeSqlBuilder appendJoined(String delimiter, Collection<? extends SafeSqlizable> collection) {
        return appendJoined(SafeSqlUtils.fromConstant(delimiter), SafeSqlUtils.EMPTY, SafeSqlUtils.EMPTY, collection.stream());
    }
    
    public SafeSqlBuilder appendJoined(String delimiter, String prefix, String suffix, Collection<? extends SafeSqlizable> collection) {
        return appendJoined(delimiter, prefix, suffix, collection.stream());
    }
    
    public SafeSqlBuilder appendJoined(String delimiter, Stream<? extends SafeSqlizable> stream) {
        return appendJoined(SafeSqlUtils.fromConstant(delimiter), SafeSqlUtils.EMPTY, SafeSqlUtils.EMPTY, stream);
    }

    public SafeSqlBuilder appendJoined(String delimiter, String prefix, String suffix, Stream<? extends SafeSqlizable> stream) {
        return appendJoined(SafeSqlUtils.fromConstant(delimiter), SafeSqlUtils.fromConstant(prefix), SafeSqlUtils.fromConstant(suffix), stream);
    }
    
    public SafeSqlBuilder appendJoined(SafeSql delimiter, Collection<? extends SafeSqlizable> collection) {
        return appendJoined(delimiter, SafeSqlUtils.EMPTY, SafeSqlUtils.EMPTY, collection.stream());
    }
    
    public SafeSqlBuilder appendJoined(SafeSql delimiter, SafeSql prefix, SafeSql suffix, Collection<? extends SafeSqlizable> collection) {
        return appendJoined(delimiter, prefix, suffix, collection.stream());
    }
    
    public SafeSqlBuilder appendJoined(SafeSql delimiter, Stream<? extends SafeSqlizable> stream) {
        return appendJoined(delimiter, SafeSqlUtils.EMPTY, SafeSqlUtils.EMPTY, stream);
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
    
    /**
     * write a string literal by escaping 
     * @param s Append this string as literal string in SQL code
     * @return a reference to this object.
     */
    public SafeSqlBuilder appendStringLiteral(String s) {
        SafeSqlUtils.appendEscapedString(sqlBuilder, s);
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
