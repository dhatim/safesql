package org.dhatim.safesql.testing;

public interface Matcher extends SelfDescribing {
    
    boolean matches(QueryPart item);
    void describeMismatch(QueryPart item, Description mismatchDescription);
    void describeTo(Description description);
    
}
