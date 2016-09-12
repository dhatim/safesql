package org.dhatim.safesql.testing.matcher;

import java.util.stream.Collectors;
import org.antlr.v4.runtime.tree.ParseTree;
import org.dhatim.safesql.testing.matcher.description.Description;

public class ValueMatcher<T> extends AbstractXPathMatcher {

    private final T value;
    private final boolean quoted;

    public ValueMatcher(String name, String xpath, T value, boolean quoted) {
        super(name, xpath);
        this.value = value;
        this.quoted = quoted;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText(getName()).appendText(" is ").appendValue(value);
    }

    @Override
    protected boolean matchesSafelyDerived(QueryPart item) {
        String stringValue = quoted ? String.format("'%s'", value.toString()) : value.toString();
        return item.getTextStream().anyMatch(stringValue::equals);
    }

    @Override
    protected void describeMismatchSafelyDerived(QueryPart actual, Description mismatchDescription) {
        mismatchDescription.appendText(getName() + " for ").appendValue(value);
        mismatchDescription.appendText(" was ");
        mismatchDescription.appendValueList("[", ",", "]", actual.children().map(ParseTree::getText).collect(Collectors.toList()));
    }

}
