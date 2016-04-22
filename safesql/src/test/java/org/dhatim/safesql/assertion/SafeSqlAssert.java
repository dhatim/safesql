package org.dhatim.safesql.assertion;

import org.assertj.core.api.AbstractAssert;
import org.assertj.core.util.Objects;
import org.dhatim.safesql.SafeSql;

public class SafeSqlAssert extends AbstractAssert<SafeSqlAssert, SafeSql> {

    
    public SafeSqlAssert hasSql(String sql) {
        isNotNull();
        String actualSql = actual.asSql();
        if (!Objects.areEqual(actualSql, sql)) {
            failWithMessage("\nExpecting sql :\n  <%s>\nto be:\n  <%s>\nbut was:\n  <%s>", actual, sql, actualSql);
        }
        return myself;
    }
    
    public SafeSqlAssert hasParameters(Object... parameters) {
        isNotNull();
        if (parameters == null) {
            failWithMessage("Expecting parameters not to be null.");
        }
        org.assertj.core.api.Assertions.assertThat(actual.getParameters()).containsExactly(parameters);
        return myself;
    }
    
    private SafeSqlAssert(SafeSql actual) {
        super(actual, SafeSqlAssert.class);
    }
    
    public static SafeSqlAssert assertThat(SafeSql actual) {
        return new SafeSqlAssert(actual);
    }

}
