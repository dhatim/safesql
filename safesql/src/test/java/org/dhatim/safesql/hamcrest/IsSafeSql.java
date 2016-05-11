package org.dhatim.safesql.hamcrest;

import java.util.Arrays;
import org.dhatim.safesql.SafeSql;
import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

public class IsSafeSql extends TypeSafeMatcher<SafeSql> {

    private final Matcher<? super String> sqlMatcher;
    private final Matcher<? super Object[]> parametersMatcher;
    
    public IsSafeSql(Matcher<? super String> sqlMatcher, Matcher<? super Object[]> parametersMatcher) {
        this.sqlMatcher = sqlMatcher;
        this.parametersMatcher = parametersMatcher;
    }
    
    @Override
    public void describeTo(Description description) {
        description.appendText("SafeSql{sql: ").appendDescriptionOf(sqlMatcher).appendText(", parameters: ").appendDescriptionOf(parametersMatcher).appendText("}");
    }

    @Override
    protected boolean matchesSafely(SafeSql item) {
        return sqlMatcher.matches(item.asSql()) && parametersMatcher.matches(item.getParameters());
    }
    
    @Override
    protected void describeMismatchSafely(SafeSql actual, Description mismatchDescription) {
        if (!sqlMatcher.matches(actual.asSql())) {
            mismatchDescription.appendText("sql was \"" + actual.asSql() + "\"");
            return;
        }
        if (!parametersMatcher.matches(actual.getParameters())) {
            mismatchDescription.appendText("parameters was " + Arrays.toString(actual.getParameters()));
            return;
        }
    }
    
    @Factory
    public static <T> Matcher<SafeSql> safesql(Matcher<? super String> sqlMatcher, Matcher<? super Object[]> parametersMatcher) {
        return new IsSafeSql(sqlMatcher, parametersMatcher);
    }

}
