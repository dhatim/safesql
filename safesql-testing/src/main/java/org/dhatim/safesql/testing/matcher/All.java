package org.dhatim.safesql.testing.matcher;

import java.util.LinkedList;
import org.dhatim.safesql.testing.matcher.description.Description;

abstract class All extends AbstractMatcher {
    
    public static class Ordered extends All {
        
        public Ordered(Iterable<Matcher> matchers) {
            super(matchers);
        }

        @Override
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
        
    }
    
    public static class Unordered extends All {

        public Unordered(Iterable<Matcher> matchers) {
            super(matchers);
        }

        @Override
        protected boolean matchesSafely(QueryPart item, Description mismatchDescription) {
            for (Matcher matcher : matchers) {
                if (!matcher.matches(item)) {
                    //mismatch.appendDescriptionOf(matcher).appendText(" ");
                    matcher.describeMismatch(item, mismatchDescription);
                  return false;
                }
            }
            return true;
        }
        
    }

    protected final Iterable<Matcher> matchers;

    public All(Iterable<Matcher> matchers) {
        this.matchers = matchers;
    }

    @Override
    public boolean matches(QueryPart item) {
        return item != null && matchesSafely(item, Description.NONE);
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

    protected abstract boolean matchesSafely(QueryPart item, Description mismatchDescription);

}
