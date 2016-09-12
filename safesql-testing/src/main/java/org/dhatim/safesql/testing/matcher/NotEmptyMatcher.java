package org.dhatim.safesql.testing.matcher;

import org.dhatim.safesql.testing.matcher.description.Description;

class NotEmptyMatcher extends AbstractMatcher {

    @Override
    public void describeTo(Description description) {
    }

    @Override
    public void describeMismatch(QueryPart actual, Description mismatchDescription) {
        mismatchDescription.appendText(" was not empty ").appendValueList("[", ", ", "]", actual.getTextChildren());
    }

    @Override
    public boolean matches(QueryPart item) {
        return item.children().count() != 0;
    }
    
}
