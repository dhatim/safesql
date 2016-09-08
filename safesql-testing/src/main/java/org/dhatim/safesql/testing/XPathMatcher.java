package org.dhatim.safesql.testing;

public class XPathMatcher extends AbstractXPathMatcher {

    private final Matcher matcher;

    public XPathMatcher(String name, String xpath, Matcher matcher) {
        super(name, xpath);
        this.matcher = matcher;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("has " + getName() + " ").appendDescriptionOf(matcher);
    }

    @Override
    protected boolean matchesSafelyDerived(QueryPart item) {
        return matcher.matches(item);
    }

    @Override
    protected void describeMismatchSafelyDerived(QueryPart actual, Description mismatchDescription) {
        if (!matcher.matches(actual)) {
            mismatchDescription.appendText(getName() + "/");
            matcher.describeMismatch(actual, mismatchDescription);
        }
    }

}
