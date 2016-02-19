package org.dhatim.safesql.builder;

import org.dhatim.safesql.SafeSql;
import org.dhatim.safesql.SafeSqlizable;

public interface Query extends SafeSqlizable {

    static Query fromSqlizable(SafeSqlizable sqlizable) {
        return () -> sqlizable.toSafeSql();
    }
    
    static Query fronConstant(SafeSql sql) {
        return () -> sql;
    }
    
}
