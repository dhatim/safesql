package org.dhatim.safesql.testing;

import java.util.LinkedList;

public class Ordered extends AbstractMatcher implements Matcher {

    private final Iterable<Matcher> matchers;

    public Ordered(Iterable<Matcher> matchers) {
        this.matchers = matchers;
    }
    
    @Override
    public final boolean matches(QueryPart item) {
        return item != null && matchesSafely(item, new Description.NullDescription());
    }
    
    protected boolean matchesSafely(QueryPart item, Description mismatchDescription) {
        // Copy matchers
        LinkedList<Matcher> matcherList = new LinkedList<>();
        for (Matcher m : matchers) {
            matcherList.add(m);
        }
        
        int lastRemoveIndex = 0;
        for (int i=0; i<item.getChildren().size(); i++) {
            //System.out.println("DEBUG for " + i + "/" + item.getChildren().size());
            QueryPart current = item.derive(i, i+1);
            
            Matcher matcher = matcherList.getFirst();
            boolean match = matcher.matches(current);
            //System.out.println("DEBUG [" + matcherList.size() + "] match " + StringDescription.toString(matcher) + " on " + current.getTextChildren().toString() + " => " + match);
            
            if (/*matcherList.getFirst().matches(current)*/match) {
                matcherList.removeFirst();
                lastRemoveIndex = i + 1;
                if (matcherList.isEmpty()) {
                    break;
                }
            }
        }
        
        if (matcherList.isEmpty()) {
            return true;
        } else {
            matcherList.getFirst().describeMismatch(item.derive(lastRemoveIndex, item.getChildren().size()), mismatchDescription);
            return false;
        }
    }

    @Override
    public void describeTo(Description description) {
        description.appendList("(", " " + "and" + " ", ")", matchers);
    }

    @Override
    public void describeMismatch(QueryPart item, Description mismatchDescription) {
        if (item == null) {
            super.describeMismatch(item, mismatchDescription);
          } else {
            matchesSafely(item, mismatchDescription);
          }
    }

}