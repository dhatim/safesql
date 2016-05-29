package org.dhatim.safesql.assertion;

import java.util.List;
import java.util.stream.Collectors;
import org.assertj.core.api.AbstractAssert;
import org.dhatim.safesql.parser.SqlToken;
import org.dhatim.safesql.parser.SqlTokenKind;
import org.dhatim.safesql.parser.SqlTokenType;

public class TokenListAssert extends AbstractAssert<TokenListAssert, List<SqlToken>> {
    
    public TokenListAssert hasTokens(SqlTokenType... types) {
        isNotNull();
        if (types == null) {
            failWithMessage("Expecting types not to be null.");
        }
        org.assertj.core.api.Assertions.assertThat(actual.stream().map(SqlToken::type).collect(Collectors.toList())).containsExactly(types);
        return myself;
    }
    
    public TokenListAssert hasValues(String... values) {
        isNotNull();
        if (values == null) {
            failWithMessage("Expecting values not to be null.");
        }
        org.assertj.core.api.Assertions.assertThat(actual.stream().map(SqlToken::value).collect(Collectors.toList())).containsExactly(values);
        return myself;
    }
    
    public TokenListAssert hasTokenClasses(SqlTokenKind... classes) {
        isNotNull();
        if (classes == null) {
            failWithMessage("Expecting classes not to be null.");
        }
        org.assertj.core.api.Assertions.assertThat(actual.stream().map(SqlToken::kind).collect(Collectors.toList())).containsExactly(classes);
        return myself;
    }
    
    /*public TokenListAssert hasLiteralizedSql(String sql) {
        isNotNull();
        String actualSql = actual.asString();
        if (!Objects.equals(actualSql, sql)) {
            failWithMessage("\nExpecting SafeSql :\n  <%s>\nto have literalized sql:\n  <%s>\nbut had literalized sql:\n  <%s>", toString(actual), sql, actualSql);
        }
        return myself;
    }
    
    public TokenListAssert hasSql(String sql) {
        isNotNull();
        String actualSql = actual.asSql();
        if (!Objects.equals(actualSql, sql)) {
            failWithMessage("\nExpecting SafeSql :\n  <%s>\nto have sql:\n  <%s>\nbut had sql:\n  <%s>", toString(actual), sql, actualSql);
        }
        return myself;
    }

    public TokenListAssert hasEmptySql() {
        isNotNull();
        String actualSql = actual.asSql();
        if (!actualSql.isEmpty()) {
            failWithMessage("\nExpecting SafeSql :\n  <%s>\nto have empty sql\nbut had sql:\n  <%s>", toString(actual), actualSql);
        }
        return myself;
    }

    public TokenListAssert hasParameters(Object... parameters) {
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

    public TokenListAssert hasParameterCount(int size) {
        isNotNull();
        int actualSize = actual.getParameters().length;
        if (size != actualSize) {
            failWithMessage("\nExpecting SafeSql :\n <%s>\nto have %s parameters\nbut had %s parameters", toString(actual), size, actualSize);
        }
        return myself;
    }
    
    public TokenListAssert hasEmptyParameters() {
        isNotNull();
        int actualSize = actual.getParameters().length;
        if (actualSize != 0) {
            failWithMessage("\nExpecting SafeSql :\n <%s>\nto have no parameters\nbut had %s parameters", toString(actual), actualSize);
        }
        return myself;
    }*/

    private TokenListAssert(List<SqlToken> actual) {
        super(actual, TokenListAssert.class);
    }

    private String toString(List<SqlToken> actual) {
        return "SafeSql{" + actual + '}';
    }

    public static TokenListAssert assertThat(List<SqlToken> actual) {
        return new TokenListAssert(actual);
    }

}
