package org.dhatim.safesql;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

public class SafeSqlBuilder implements SafeSqlizable {

    private final StringBuilder sqlBuilder = new StringBuilder();
    private final List<Object> parameters = new ArrayList<>();
    
    // TODO rename to param
    public SafeSqlBuilder append(int num) {
        appendObject(num);
        return this;
    }

    // TODO rename to param
    public SafeSqlBuilder append(double num) {
        appendObject(num);
        return this;
    }
    
    // TODO rename to param
    public SafeSqlBuilder append(Object obj) {
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
        return appendJoined(SafeSqlUtils.fromConstant(delimiter), SafeSql.EMPTY, SafeSql.EMPTY, collection.stream());
    }
    
    public SafeSqlBuilder appendJoined(String delimiter, String prefix, String suffix, Collection<? extends SafeSqlizable> collection) {
        return appendJoined(delimiter, prefix, suffix, collection.stream());
    }
    
    public SafeSqlBuilder appendJoined(String delimiter, Stream<? extends SafeSqlizable> stream) {
        return appendJoined(SafeSqlUtils.fromConstant(delimiter), SafeSql.EMPTY, SafeSql.EMPTY, stream);
    }

    public SafeSqlBuilder appendJoined(String delimiter, String prefix, String suffix, Stream<? extends SafeSqlizable> stream) {
        return appendJoined(SafeSqlUtils.fromConstant(delimiter), SafeSqlUtils.fromConstant(prefix), SafeSqlUtils.fromConstant(suffix), stream);
    }
    
    public SafeSqlBuilder appendJoined(SafeSql delimiter, Collection<? extends SafeSqlizable> collection) {
        return appendJoined(delimiter, SafeSql.EMPTY, SafeSql.EMPTY, collection.stream());
    }
    
    public SafeSqlBuilder appendJoined(SafeSql delimiter, SafeSql prefix, SafeSql suffix, Collection<? extends SafeSqlizable> collection) {
        return appendJoined(delimiter, prefix, suffix, collection.stream());
    }
    
    public SafeSqlBuilder appendJoined(SafeSql delimiter, Stream<? extends SafeSqlizable> stream) {
        return appendJoined(delimiter, SafeSql.EMPTY, SafeSql.EMPTY, stream);
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

    // TODO rename to param
    public SafeSqlBuilder appendEscaped(String s) {
        appendObject(s);
        return this;
    }

    // TODO rename to append
    public SafeSqlBuilder appendConstant(String s) {
        sqlBuilder.append(s);
        return this;
    }
    
    // TODO rename to append
    public SafeSqlBuilder appendConstant(char ch) {
        sqlBuilder.append(ch);
        return this;
    }
    
    /**
     * write a string literal by escaping 
     * @param s
     * @return
     */
    public SafeSqlBuilder appendStringLiteral(String s) {
        sqlBuilder.append(SafeSqlUtils.escapeString(s));
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
        builder.append(toSafeSql());
    }

    private void appendObject(Object o) {
        sqlBuilder.append('?');
        parameters.add(o);
    }
    
}
