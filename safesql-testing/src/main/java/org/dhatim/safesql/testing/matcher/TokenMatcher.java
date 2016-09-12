package org.dhatim.safesql.testing.matcher;

import java.util.stream.Collectors;
import org.antlr.v4.runtime.tree.ParseTree;
import org.dhatim.safesql.testing.matcher.description.Description;

class TokenMatcher extends AbstractXPathMatcher {

    private final String token;

    public TokenMatcher(String name, String xpath, String token) {
        super(name, xpath);
        this.token = token;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText(getName()).appendText(" is ").appendText(token);
    }

    @Override
    protected boolean matchesSafelyDerived(QueryPart item) {
        return item.getTextStream().anyMatch(token::equals);
    }

    @Override
    protected void describeMismatchSafelyDerived(QueryPart actual, Description mismatchDescription) {
        mismatchDescription.appendText(getName());
        mismatchDescription.appendText(" was ");
        mismatchDescription.appendValueList("[", ",", "]", actual.children().map(ParseTree::getText).collect(Collectors.toList()));
    }
    
}
