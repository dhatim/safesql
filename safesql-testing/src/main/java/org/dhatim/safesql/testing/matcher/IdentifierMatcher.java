package org.dhatim.safesql.testing.matcher;

import java.util.stream.Collectors;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.dhatim.safesql.testing.matcher.description.Description;
import org.dhatim.safesql.testing.parser.PSQLParser;

public class IdentifierMatcher extends AbstractXPathMatcher {

    private final String identifier;

    public IdentifierMatcher(String identifier) {
        super("identifier", "//identifier/*");
        this.identifier = identifier;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText(getName()).appendText(" is ").appendText(identifier);
    }

    @Override
    protected boolean matchesSafelyDerived(QueryPart item) {
        return item.children().map(IdentifierMatcher::extractIdentifier).anyMatch(identifier::equals);
    }

    @Override
    protected void describeMismatchSafelyDerived(QueryPart actual, Description mismatchDescription) {
        mismatchDescription.appendText(getName());
        mismatchDescription.appendText(" was ");
        mismatchDescription.appendValueList("[", ",", "]", actual.children().map(IdentifierMatcher::extractIdentifier).collect(Collectors.toList()));
    }
    
    private static String extractIdentifier(ParseTree parse) {
        if (parse instanceof TerminalNode) {
            int type = ((TerminalNode) parse).getSymbol().getType();
            if (type == PSQLParser.Identifier) {
                return parse.getText();
            } else if (type == PSQLParser.QuotedIdentifier) {
                return parse.getText().substring(1, parse.getText().length()-1);
            }
        }
        return parse.getText();
    }
    
}
