package org.dhatim.safesql;

import org.dhatim.safesql.SafeSql;
import org.dhatim.safesql.assertion.AbstractSafeSqlAssert;

class SimpleSafeSqlAssert extends AbstractSafeSqlAssert<SimpleSafeSqlAssert> {

    private SimpleSafeSqlAssert(SafeSql actual) {
        super(actual, SimpleSafeSqlAssert.class);
    }

    public static SimpleSafeSqlAssert assertThat(SafeSql actual) {
        return new SimpleSafeSqlAssert(actual);
    }

}
