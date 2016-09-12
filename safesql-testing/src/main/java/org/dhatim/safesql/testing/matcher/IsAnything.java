package org.dhatim.safesql.testing.matcher;

import org.dhatim.safesql.testing.matcher.description.Description;

class IsAnything extends AbstractMatcher {
    
    private final String message;

    public IsAnything() {
        this("ANYTHING");
    }

    public IsAnything(String message) {
        this.message = message;
    }

    @Override
    public boolean matches(QueryPart o) {
        return true;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText(message);
    }

}
