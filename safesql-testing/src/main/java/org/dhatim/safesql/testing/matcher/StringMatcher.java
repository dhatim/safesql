package org.dhatim.safesql.testing.matcher;

import java.util.stream.Collectors;
import org.antlr.v4.runtime.tree.ParseTree;
import org.dhatim.safesql.testing.matcher.description.Description;

class StringMatcher extends AbstractXPathMatcher {
    
    private final String value;
    private final boolean ignoreCase;

    public StringMatcher(String name, String xpath, String value, boolean ignoreCase) {
        super(name, xpath);
        this.value = value;
        this.ignoreCase = ignoreCase;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText(getName()).appendText(" is ").appendValue(value);
    }

    @Override
    protected boolean matchesSafelyDerived(QueryPart item) {
        return item.children().map(ParseTree::getText).anyMatch(ignoreCase ? value::equalsIgnoreCase : value::equals);
    }

    @Override
    protected void describeMismatchSafelyDerived(QueryPart actual, Description mismatchDescription) {
        mismatchDescription.appendText(getName());
        mismatchDescription.appendText(" was ");
        mismatchDescription.appendValueList("[", ",", "]", actual.children().map(ParseTree::getText).collect(Collectors.toList()));
    }

}
