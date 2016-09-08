package org.dhatim.safesql.testing;

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
