package org.dhatim.safesql.testing;

import org.dhatim.safesql.SafeSql;

public class Assertions extends org.assertj.core.api.Assertions {

    public static SafeSqlAssert assertThat(SafeSql actual) {
        return SafeSqlAssert.assertThat(actual);
    }
    
}
