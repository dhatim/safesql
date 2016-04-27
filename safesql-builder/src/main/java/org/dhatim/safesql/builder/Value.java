package org.dhatim.safesql.builder;

import org.dhatim.safesql.SafeSql;
import org.dhatim.safesql.SafeSqlBuilder;
import org.dhatim.safesql.SafeSqlUtils;

public class Value implements Operand {

    private final Object data;

    public Value(Object data) {
        this.data = data;
    }

    @Override
    public SafeSql toSafeSql() {
        return SafeSqlUtils.escape(data);
    }
    
    @Override
    public void appendTo(SafeSqlBuilder builder) {
        builder.param(data);
    }

    public Object value() {
        return data;
    }
    
    @Override
    public String toString() {
        return "Value{" + data.getClass().getSimpleName() + ":" + data + "}";
    }
    
    public static Value of(Object data) {
        if (data instanceof Boolean) {
            return new BooleanValue((Boolean) data);
        } else {
            return new Value(data);
        }
    }

}
