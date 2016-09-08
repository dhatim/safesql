package org.dhatim.safesql.assertion;

import java.util.Arrays;
import java.util.Objects;
import org.assertj.core.api.AbstractAssert;
import org.dhatim.safesql.SafeSql;

public class AbstractSafeSqlAssert<S extends AbstractSafeSqlAssert<S>> extends AbstractAssert<S, SafeSql> {
    
    protected AbstractSafeSqlAssert(SafeSql actual, Class<S> selfType) {
        super(actual, selfType);
    }

    public S hasLiteralizedSql(String sql) {
        isNotNull();
        String actualSql = actual.asString();
        if (!Objects.equals(actualSql, sql)) {
            failWithMessage("\nExpecting SafeSql :\n  <%s>\nto have literalized sql:\n  <%s>\nbut had literalized sql:\n  <%s>", toString(actual), sql, actualSql);
        }
        return myself;
    }
    
    public S hasSql(String sql) {
        isNotNull();
        String actualSql = actual.asSql();
        if (!Objects.equals(actualSql, sql)) {
            failWithMessage("\nExpecting SafeSql :\n  <%s>\nto have sql:\n  <%s>\nbut had sql:\n  <%s>", toString(actual), sql, actualSql);
        }
        return myself;
    }

    public S hasEmptySql() {
        isNotNull();
        String actualSql = actual.asSql();
        if (!actualSql.isEmpty()) {
            failWithMessage("\nExpecting SafeSql :\n  <%s>\nto have empty sql\nbut had sql:\n  <%s>", toString(actual), actualSql);
        }
        return myself;
    }

    public S hasParameters(Object... parameters) {
        isNotNull();
        if (parameters == null) {
            failWithMessage("Expecting parameters not to be null.");
        }
        hasParameterCount(parameters.length);
        Object[] actualParameters = actual.getParameters();
        int actualLength = actualParameters.length;
        for (int i = 0; i < actualLength; i++) {
            Object actualElement = actualParameters[i];
            Object expectedElement = parameters[i];
            if (!Objects.equals(actualElement, expectedElement)) {
                failWithMessage("%nActual paremeters and expected parameters have not the same elements, at index %d actual elements was:%n <%s>%nwhereas expected element was:%n <%s>%nfor SafeSql <%s>",
                        i, actualElement, expectedElement, toString(actual));
            }
        }
        return myself;
    }

    public S hasParameterCount(int size) {
        isNotNull();
        int actualSize = actual.getParameters().length;
        if (size != actualSize) {
            failWithMessage("\nExpecting SafeSql :\n <%s>\nto have %s parameters\nbut had %s parameters", toString(actual), size, actualSize);
        }
        return myself;
    }
    
    public S hasEmptyParameters() {
        isNotNull();
        int actualSize = actual.getParameters().length;
        if (actualSize != 0) {
            failWithMessage("\nExpecting SafeSql :\n <%s>\nto have no parameters\nbut had %s parameters", toString(actual), actualSize);
        }
        return myself;
    }

    private String toString(SafeSql obj) {
        return "SafeSql{sql: " + obj.asSql() + ", parameters: " + Arrays.toString(obj.getParameters()) + '}';
    }

}
