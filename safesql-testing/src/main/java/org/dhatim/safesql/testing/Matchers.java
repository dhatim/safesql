package org.dhatim.safesql.testing;

import java.util.Arrays;

public class Matchers {
    
    private static final String NO_PATH = "";

    public static Matcher where(Matcher matcher) {
        return xpath("where", "//where_clause", matcher);
    }
    
    public static Matcher literal(String s) {
        return new ValueMatcher<>("literal", NO_PATH, s, true);
    }
    
    public static Matcher literal(int n) {
        return new ValueMatcher<>("literal", NO_PATH, n, false);
    }
    
    public static Matcher equal(Matcher left, Matcher right) {
        return comparison("equality", "=", left, right);
    }
    
    public static Matcher column(String name) {
        return xpath("column", "//column_reference", identifier(name));
    }
    
    public static Matcher symbol(String literal) {
        return symbol("", literal);
    }
    
    public static Matcher identifier(String identifier) {
        return new IdentifierMatcher(identifier);
    }
    
    private static Matcher symbol(String xpath, String literal) {
        return xpath(String.format("symbol '%s'", literal), String.format("%s//'%s'", xpath, literal), unempty());
    }
    
    private static Matcher comparison(String name, String operator, Matcher left, Matcher right) {
        return xpath(name, "//comparison_predicate/*", orderedAllOf(left, symbol("//comp_op", operator), right));
    }
    
    private static Matcher orderedAllOf(Matcher... matchers) {
        return new Ordered(Arrays.asList(matchers));
    }
    
    private static Matcher xpath(String name, String xpath, Matcher matcher) {
        return new XPathMatcher(name, xpath, matcher);
    }
    
    private static Matcher unempty() {
        return new NotEmptyMatcher();
    }
    
}
