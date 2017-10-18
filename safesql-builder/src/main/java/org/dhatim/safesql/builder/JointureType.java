package org.dhatim.safesql.builder;

import org.dhatim.safesql.SafeSql;
import org.dhatim.safesql.SafeSqlBuilder;
import org.dhatim.safesql.SafeSqlUtils;
import org.dhatim.safesql.SafeSqlizable;

public enum JointureType implements SafeSqlizable {
    INNER           ("INNER JOIN"),
    LEFT_OUTER      ("LEFT JOIN"),
    RIGHT_OUTER     ("RIGHT JOIN"),
    FULL_OUTER      ("FULL JOIN"),
    CROSS           ("CROSS JOIN");

    private final String sql;

    private JointureType(String sql) {
        this.sql = sql;
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
