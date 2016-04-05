package org.safesql.template.processor.test;

import org.dhatim.safesql.SafeSql;
import org.dhatim.safesql.template.SafeSqlTemplates;

public interface MyTemplate extends SafeSqlTemplates {

    @Template("SELECT * FROM file WHERE name = :name")
    SafeSql simpleQuery(String name);
    
}
