package org.dhatim.safesql.template;

import org.dhatim.safesql.SafeSql;
import org.dhatim.safesql.template.SafeSqlTemplates.Template;

public interface SimpleTemplates extends SafeSqlTemplates {

    @Template("SELECT * FROM file WHERE name = :name AND name = :ident")
    SafeSql simpleQuery(String name, @Identifier String ident);
    
}
