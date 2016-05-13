package org.dhatim.safesql.assertion;

import org.dhatim.safesql.SafeSql;

public class Assertions {

    public static SafeSqlAssert assertThat(SafeSql actual) {
        return SafeSqlAssert.assertThat(actual);
    }
    
    protected Assertions() {
    }
    
}
