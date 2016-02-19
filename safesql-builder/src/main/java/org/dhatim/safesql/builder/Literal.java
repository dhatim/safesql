package org.dhatim.safesql.builder;

import org.dhatim.safesql.SafeSql;
import org.dhatim.safesql.SafeSqlUtils;

public class Literal implements Operand {
    
    private static final Literal NULL_LITERAL = new Literal(null) {
        @Override
        public SafeSql toSafeSql() {
            return SafeSqlUtils.fromConstant("NULL");
        }
    };

    private final String value;

    protected Literal(String value) {
        this.value = value;
    }
    
    public static Literal from(String value) {
        return value == null ? NULL_LITERAL : new Literal(value);
    }
    
    @Override
    public SafeSql toSafeSql() {
        return SafeSqlUtils.literalize(SafeSqlUtils.escape(value));
    }
    
}
