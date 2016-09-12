package org.dhatim.safesql.testing;

import org.assertj.core.api.AbstractObjectArrayAssert;
import org.assertj.core.api.AssertionsForClassTypes;
import org.dhatim.safesql.SafeSql;
import org.dhatim.safesql.assertion.AbstractSafeSqlAssert;
import org.dhatim.safesql.testing.matcher.Matcher;
import org.dhatim.safesql.testing.matcher.Matchers;
import org.dhatim.safesql.testing.matcher.QueryPart;
import org.dhatim.safesql.testing.matcher.description.Description;
import org.dhatim.safesql.testing.matcher.description.StringDescription;

public class SafeSqlAssert extends AbstractSafeSqlAssert<SafeSqlAssert> {
    
    private QueryPart parsedQuery;

    private SafeSqlAssert(SafeSql actual) {
        super(actual, SafeSqlAssert.class);
    }

    public AbstractObjectArrayAssert<?, Object> forParameters() {
        isNotNull();
        return AssertionsForClassTypes.assertThat(actual.getParameters());
    }
    
    public SafeSqlAssert isQueryWith(Matcher matcher) {
        isNotNull();
        tryParse();
        assertThat(descriptionText(), parsedQuery, matcher);
        return this;
    }
    
    public SafeSqlAssert isQueryWith(Matcher... matchers) {
        isNotNull();
        tryParse();
        assertThat(descriptionText(), parsedQuery, Matchers.allOf(matchers));
        return this;
    }
    
    private void tryParse() {
        if (parsedQuery == null) {
            parsedQuery = QueryPart.parse(actual);
        }
    }
    
    private static void assertThat(String reason, QueryPart actual, Matcher matcher) {
        if (!matcher.matches(actual)) {
            Description description = new StringDescription();
            description.appendText(reason)
                       .appendText("\nExpected: ")
                       .appendDescriptionOf(matcher)
                       .appendText("\n     but: ");
            matcher.describeMismatch(actual, description);
            
            throw new AssertionError(description.toString());
        }
    }
    
    public static SafeSqlAssert assertThat(SafeSql actual) {
        return new SafeSqlAssert(actual);
    }
    
}
