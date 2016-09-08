package org.dhatim.safesql.testing;

import static org.dhatim.safesql.testing.Assertions.*;
import static org.dhatim.safesql.testing.Matchers.*;

import org.dhatim.safesql.SafeSqlUtils;
import org.junit.Test;

public class AssertionsTest {
    
    @Test
    public void testSimpleQuery() {
        assertThat(SafeSqlUtils.fromConstant("SELECT col1 FROM table1 WHERE col2 = 4")).isQueryWith(where(equal(column("col1"), literal(4))));
    }

}
