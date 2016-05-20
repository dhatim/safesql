package org.dhatim.safesql.builder;

import org.dhatim.safesql.SafeSql;
import org.dhatim.safesql.SafeSqlizable;

public interface SqlQuery extends SafeSqlizable {

    static SqlQuery fromSqlizable(SafeSqlizable sqlizable) {
        return sqlizable::appendTo;
    }
    
    static SqlQuery of(SafeSql sql) {
        return new SafeSqlQuery(sql);
    }
    
}
