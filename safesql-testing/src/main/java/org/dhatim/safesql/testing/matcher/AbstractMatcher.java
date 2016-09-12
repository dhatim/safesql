package org.dhatim.safesql.testing.matcher;

import org.dhatim.safesql.testing.matcher.description.Description;
import org.dhatim.safesql.testing.matcher.description.StringDescription;

public abstract class AbstractMatcher implements Matcher {

    @Override
    public void describeMismatch(QueryPart item, Description description) {
        description.appendText("was ").appendValue(item);
    }

    @Override
    public String toString() {
        return StringDescription.toString(this);
    }

}
