package org.dhatim.safesql.testing.matcher;

import org.dhatim.safesql.testing.matcher.description.Description;
import org.dhatim.safesql.testing.matcher.description.SelfDescribing;

public interface Matcher extends SelfDescribing {
    
    boolean matches(QueryPart item);
    void describeMismatch(QueryPart item, Description mismatchDescription);
    void describeTo(Description description);
    
}
