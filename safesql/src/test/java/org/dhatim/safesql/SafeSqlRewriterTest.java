package org.dhatim.safesql;

import org.junit.Test;

import static org.dhatim.safesql.fixtures.IsSafeSql.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import org.hamcrest.Matchers;

public class SafeSqlRewriterTest {
    
    @Test
    public void testRewrite() {
        SafeSql sql = new SafeSqlBuilder()
               .append("SELECT * FROM table WHERE ").appendIdentifier("FILE").append(" = ").param(null).append(" AND name = ").param("Hello")
               .toSafeSql();
        
        SafeSqlRewriter rewriter = new SafeSqlRewriter((sb, oldParam) -> {
            if (oldParam == null) {
                sb.append("(NULL)");
            } else {
                sb.param(oldParam);
            }
        });
        
        SafeSql newSql = rewriter.write(sql);
        
        assertThat(newSql, safesql(is("SELECT * FROM table WHERE \"FILE\" = (NULL) AND name = ?"), Matchers.<Object>arrayContaining("Hello")));
    }

}
