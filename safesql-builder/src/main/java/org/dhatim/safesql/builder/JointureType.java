package org.dhatim.safesql.builder;

import org.dhatim.safesql.SafeSql;
import org.dhatim.safesql.SafeSqlUtils;
import org.dhatim.safesql.SafeSqlizable;

public enum JointureType implements SafeSqlizable {
    INNER           ("INNER JOIN"),
    LEFT_OUTER      ("LEFT JOIN"),
    RIGHT_OUTER     ("RIGHT JOIN"),
    FULL_OUTER      ("FULL JOIN"),
    CROSS           ("CROSS JOIN");
    
    private final SafeSql sql;
    
    private JointureType(String sql) {
        this.sql = SafeSqlUtils.fromConstant(sql);
    }

    @Override
    public SafeSql toSafeSql() {
        return sql;
    }
}
