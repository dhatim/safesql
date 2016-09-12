package org.dhatim.safesql.testing.matcher;

import java.util.Arrays;

public class Matchers {
    
    private static final String NO_PATH = "";
    
    public static Matcher select(Matcher... matchers) {
        return xpath("select", "//select_list", orderedAllOf(matchers));
    }
    
    public static Matcher from(Matcher...matchers) {
        return xpath("from", "//from_clause", allOf(matchers));
    }
    
    public static Matcher leftJoin(String tableName) {
        return join(allOf(keyword("join type", "//join_type", "left"), table(tableName)));
    }
    
    private static Matcher join(Matcher matcher) {
        return xpath("join", "//table_reference/joined_table/joined_table_primary", matcher);
    }
    
    public static Matcher where(Matcher matcher) {
        return xpath("where", "//where_clause", matcher);
    }
    
    public static Matcher add(Matcher left, Matcher right) {
        return computeTerm("addition", "term", left, "+", right);
    }
    
    public static Matcher sub(Matcher left, Matcher right) {
        return computeTerm("substraction", "term", left, "-", right);
    }
    
    private static Matcher computeTerm(String name, String containerRule, Matcher left, String operator, Matcher right) {
        return xpath(name, "//numeric_value_expression/*", orderedAllOf(rule(containerRule, containerRule, left), symbol(operator), rule(containerRule, containerRule, right)));
    }
    
    public static Matcher mul(Matcher left, Matcher right) {
        return computeFactor("multiply", "factor", left, "*", right);
    }
    
    public static Matcher div(Matcher left, Matcher right) {
        return computeFactor("divide", "factor", left, "/", right);
    }
    
    private static Matcher computeFactor(String name, String containerRule, Matcher left, String operator, Matcher right) {
        return xpath(name, "//numeric_value_expression/term/*", orderedAllOf(rule(containerRule, containerRule, left), symbol(operator), rule(containerRule, containerRule, right)));
    }
    
    public static Matcher literal(String s) {
        return new ValueMatcher<>("literal", NO_PATH, s, true);
    }
    
    public static Matcher literal(int n) {
        return new ValueMatcher<>("literal", NO_PATH, n, false);
    }
    
    public static Matcher uuidLiteral(String uuid) {
        return new ValueMatcher<String>("literal", "//uuid_literal/*", uuid, true);
    }
    
    public static Matcher dateLiteral(String date) {
        return xpath("date", "//date_literal/*", literal(date));
    }
    
    public static Matcher row(Matcher... matchers) {
        return xpath("row", "//row_value_constructor/*", orderedAllOf(matchers));
    }
    
    public static Matcher like(Matcher operand, String pattern) {
        return xpath("like matcher", "//pattern_matching_predicate/*", orderedAllOf(operand, keyword("like keyword", NO_PATH, "like"), literal(pattern)));
    }
    
    public static Matcher equal(Matcher left, Matcher right) {
        return comparison("equality", "=", left, right);
    }
    
    public static Matcher unequal(Matcher left, Matcher right) {
        return xpath("unequality", "//comparison_predicate/*", orderedAllOf(left, token("//comp_op", "NOT_EQUAL"), right));
    }
    
    public static Matcher greater(Matcher left, Matcher right) {
        return comparison("greater", ">", left, right);
    }
    
    public static Matcher greaterEqual(Matcher left, Matcher right) {
        return comparison("greater and equal", ">=", left, right);
    }
    
    public static Matcher less(Matcher left, Matcher right) {
        return comparison("less", "<", left, right);
    }
    
    public static Matcher lessEqual(Matcher left, Matcher right) {
        return comparison("less and equal", "<=", left, right);
    }
    
    public static Matcher cast(Matcher operand, String type) {
        return xpath("primary", "//casted_value_expression_primary/*", orderedAllOf(xpath("operand", "//value_expression_primary", operand), node("::"), keyword("type", "//cast_target", type)));
    }
    
    public static Matcher nullCast(String type) {
        return xpath("primary", "//null_casted_value_expression/*", orderedAllOf(node("NULL"), node("::"), xpath("cast type", "//cast_target", keyword("//data_type", NO_PATH, type))));
    }
    
    public static Matcher position(Matcher searched, Matcher text) {
        return xpath("position", "//position_invocation/*", xpath("arguments", "//string_expression/*", orderedAllOf(searched, text)));
    }
    
    public static Matcher call(String functionName, Matcher... parameters) {
        return xpath("function", "//routine_invocation/*", orderedAllOf(xpath("function name", "//function_name/*", identifier(functionName)), xpath("arguments", "//sql_argument_list/*", orderedAllOf(parameters))));
    }
    
    public static Matcher table(String name) {
        return xpath("table", "//table_primary//table_name", identifier(name));
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
    
    public static Matcher any() {
        return new IsAnything();
    }
    
    public static Matcher not(Matcher matcher) {
        return xpath("not", "//boolean_factor/*", orderedAllOf(keyword("not keyword", NO_PATH, "not"), matcher));
    }
    
    private static Matcher symbol(String xpath, String literal) {
        return xpath(String.format("symbol '%s'", literal), String.format("%s//'%s'", xpath, literal), unempty());
    }
    
    private static Matcher keyword(String name, String xpath, String keyword) {
        return new StringMatcher(name, xpath, keyword, true);
    }
    
    private static Matcher token(String xpath, String token) {
        return xpath(String.format("token %s", token), String.format("%s//%s", xpath, token), unempty());
    }
    
    private static Matcher token(String token) {
        return token("", token);
    }
    
    public static Matcher and(Matcher left, Matcher right) {
        return rule("and", "and_predicate", orderedAllOf(left, token("AND"), right));
    }
    
    public static Matcher and(Matcher left, Matcher middle, Matcher right) {
        return rule("and", "and_predicate", orderedAllOf(left, token("AND"), middle, token("AND"), right));
    }
    
    public static Matcher or(Matcher left, Matcher right) {
        return rule("or", "or_predicate", orderedAllOf(left, token("OR"), right));
    }
    
    public static Matcher or(Matcher left, Matcher middle, Matcher right) {
        return rule("or", "or_predicate", orderedAllOf(left, token("OR"), middle, token("OR"), right));
    }
    
    private static Matcher comparison(String name, String operator, Matcher left, Matcher right) {
        return xpath(name, "//comparison_predicate/*", orderedAllOf(left, symbol("//comp_op", operator), right));
    }
    
    private static Matcher orderedAllOf(Matcher... matchers) {
        return new All.Ordered(Arrays.asList(matchers));
    }
    
    public static Matcher allOf(Matcher... matchers) {
        return new All.Unordered(Arrays.asList(matchers));
    }
    
    private static Matcher xpath(String name, String xpath, Matcher matcher) {
        return new XPathMatcher(name, xpath, matcher);
    }
    
    private static Matcher unempty() {
        return new NotEmptyMatcher();
    }
    
    private static Matcher node(String nodeName) {
        return new TokenMatcher(String.format("token '%s'", nodeName), "", nodeName);
    }
    
    private static Matcher rule(String name, String ruleName, Matcher matcher) {
        return xpath(name, "//" + ruleName + "/*", matcher);
    }
    
}
