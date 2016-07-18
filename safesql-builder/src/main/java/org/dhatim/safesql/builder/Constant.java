package org.dhatim.safesql.builder;

import java.util.Objects;
import org.dhatim.safesql.SafeSql;
import org.dhatim.safesql.SafeSqlBuilder;
import org.dhatim.safesql.SafeSqlUtils;

public class Constant implements Operand {

    private final String sql;

    public Constant(Object sql) {
        this(sql.toString());
    }
    
    public Constant(String sql) {
        this.sql = Objects.requireNonNull(sql);
    }

    @Override
    public SafeSql toSafeSql() {
        return SafeSqlUtils.fromConstant(sql);
    }
    
    @Override
    public void appendTo(SafeSqlBuilder builder) {
        builder.append(sql);
    }
    
}
