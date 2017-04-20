package org.dhatim.safesql;

import org.dhatim.safesql.assertion.Assertions;
import org.junit.Test;

public class SafeSqlRewriterTest {

    @Test
    public void testRewrite() {
        SafeSql sql = new SafeSqlBuilder()
               .append("SELECT * FROM table WHERE ").identifier("FILE").append(" = ").param(null).append(" AND name = ").param("Hello")
               .toSafeSql();

        SafeSqlRewriter rewriter = new SafeSqlRewriter((sb, oldParam) -> {
            if (oldParam == null) {
                sb.append("(NULL)");
            } else {
                sb.param(oldParam);
            }
        });

        SafeSql newSql = rewriter.write(sql);

        Assertions.assertThat(newSql)
                .hasSql("SELECT * FROM table WHERE \"FILE\" = (NULL) AND name = ?")
                .hasParameters("Hello");
    }

}
