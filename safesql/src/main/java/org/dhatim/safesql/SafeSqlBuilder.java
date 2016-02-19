package org.dhatim.safesql;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class SafeSqlBuilder implements SafeSqlizable {

    private final StringBuilder builder = new StringBuilder();
    private final List<Object> parameters = new ArrayList<>();

    public SafeSqlBuilder append(int num) {
        appendObject(num);
        return this;
    }

    public SafeSqlBuilder append(double num) {
        appendObject(num);
        return this;
    }
    
    public SafeSqlBuilder append(Object obj) {
        appendObject(obj);
        return this;
    }

    public SafeSqlBuilder append(SafeSql sql) {
        builder.append(sql.asSql());
        parameters.addAll(Arrays.asList(sql.getParameters()));
        return this;
    }

    public SafeSqlBuilder appendJoined(String sep, Collection<? extends SafeSqlizable> c) {
        boolean first = true;
        for (SafeSqlizable sql : c) {
            if (first) {
                first = false;
            } else {
                builder.append(sep);
            }
            append(sql);
        }
        return this;
    }
    
    public SafeSqlBuilder appendJoined(SafeSqlizable sep, Collection<? extends SafeSqlizable> c) {
        boolean first = true;
        for (SafeSqlizable sql : c) {
            if (first) {
                first = false;
            } else {
                builder.append(' ').append(sep).append(' ');
            }
            append(sql);
        }
        return this;
    }

    public SafeSqlBuilder append(SafeSqlizable sqlizable) {
        return append(sqlizable.toSafeSql());
    }

    public SafeSqlBuilder appendEscaped(String s) {
        appendObject(s);
        return this;
    }

    public SafeSqlBuilder appendConstant(String s) {
        builder.append(s);
        return this;
    }
    
    public SafeSqlBuilder appendConstant(char ch) {
        builder.append(ch);
        return this;
    }
    
    /**
     * write a string literal by escaping 
     * @param s
     * @return
     */
    public SafeSqlBuilder appendStringLiteral(String s) {
        builder.append(SafeSqlUtils.escapeString(s));
        return this;
    }

    public SafeSqlBuilder appendIdentifier(String identifier) {
        builder.append(SafeSqlUtils.mustEscapeIdentifier(identifier) ? SafeSqlUtils.escapeIdentifier(identifier) : identifier);
        return this;
    }

    @Override
    public SafeSql toSafeSql() {
        return new SafeSqlImpl(builder.toString(), parameters.toArray());
    }

    private void appendObject(Object o) {
        builder.append('?');
        parameters.add(o);
    }

}
