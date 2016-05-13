package org.dhatim.safesql.template;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public interface SafeSqlTemplates {
    
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.SOURCE)
    @interface Template {
        String value();
    }
    
    @Target(ElementType.PARAMETER)
    @Retention(RetentionPolicy.SOURCE)
    @interface Identifier {
    }

}

/*

@Template("SELECT * FROM :table WHERE name = :name")
SafeSql query(@Identifier String table, String name);

@Template("SELECT * FROM :schemaTable WHERE name = :name")
SafeSql query(@DottedSchemaTable String schemaTable, String name);

@Template("WITH :cte AS cte, SELECT * FROM :table t INNER JOIN cte c ON c.id = t.cte_id")
SafeSql query(SafeSql cte, @Identifier String table, String name);

@Template("SELECT * FROM table WHERE name IN (:names)")
SafeSql query(List<String> names);

@Template("SELECT * FROM table WHERE name = :name")
SafeSqlizable query(String name);

@Template("INSERT INTO :errorTable (\"DECLARATION\", \"FILE\", \"CONTROL\", \"REVISION\", line, \"rowId\") SELECT DISTINCT \"DECLARATION\", :fileId, :controlId, :revId, \"firstLine\", id FROM (:sql) errors")
@Var(name="errorTable", value="error_{fileId}", type=@Identifier)
SafeSql insert(UUID fileId, int controlId, int revId, SafeSql sql);



*/
