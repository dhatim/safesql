package org.dhatim.safesql.testing;

public abstract class AbstractXPathMatcher implements Matcher {

    private final String xpath;
    private final String name;

    public AbstractXPathMatcher(String name, String xpath) {
        this.xpath = xpath;
        this.name = name;
    }

    public final boolean matches(QueryPart item) {
        return matchesSafelyDerived(derive(item));
    }
    
    protected abstract boolean matchesSafelyDerived(QueryPart item);
    
    public final void describeMismatch(QueryPart actual, Description mismatchDescription) {
        describeMismatchSafelyDerived(derive(actual), mismatchDescription);
    }
    
    protected abstract void describeMismatchSafelyDerived(QueryPart actual, Description mismatchDescription);

    public String getName() {
        return name;
    }
    
    private QueryPart derive(QueryPart actual) {
        return xpath == null || "".equals(xpath) ? actual : actual.derive(xpath);
    }
    
}
