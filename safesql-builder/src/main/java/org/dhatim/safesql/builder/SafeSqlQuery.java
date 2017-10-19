package org.dhatim.safesql.builder;

import org.dhatim.safesql.SafeSql;
import org.dhatim.safesql.SafeSqlBuilder;

public class SafeSqlQuery implements SqlQuery {

    private final SafeSql sql;

    public SafeSqlQuery(SafeSql sql) {
        this.sql = sql;
    }

    @Override
    public void appendTo(SafeSqlBuilder builder) {
        builder.append(sql);
    }

}
