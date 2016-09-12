package org.dhatim.safesql.testing.matcher;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.xpath.XPath;
import org.dhatim.safesql.SafeSql;
import org.dhatim.safesql.testing.parser.PSQLParser;
import org.dhatim.safesql.testing.parser.PSQLParser.SqlContext;

public class QueryPart {
    
    private final PSQLParser parser;
    private final SqlContext tree;
    private final List<ParseTree> currentElements;

    private QueryPart(PSQLParser parser) {
        this(parser, parser.sql());
    }
    
    private QueryPart(PSQLParser parser, SqlContext tree) {
        this(parser, tree, Arrays.asList(tree));
    }
    
    private QueryPart(PSQLParser parser, SqlContext tree, List<ParseTree> current) {
        this.parser = parser;
        this.tree = tree;
        this.currentElements = current;
    }
    
    public QueryPart derive(String xpath) {
        List<ParseTree> list = currentElements.stream().flatMap(p -> XPath.findAll(p, xpath, parser).stream()).collect(Collectors.toList());
        return new QueryPart(parser, tree, list);
    }
    
    public QueryPart derive(int fromIndex, int toIndex) {
        return new QueryPart(parser, tree, getChildren().subList(fromIndex, toIndex));
    }
    
    List<ParseTree> getChildren() {
        return Collections.unmodifiableList(currentElements);
    }
    
    public List<String> getTextChildren() {
        return getChildren().stream().map(ParseTree::getText).collect(Collectors.toList());
    }
    
    public Stream<String> getTextStream() {
        return currentElements.stream().map(ParseTree::getText);
    }
    
    Stream<ParseTree> children() {
        return currentElements.stream();
    }
    
    public String toString() {
        return getTextStream().collect(Collectors.joining(", ", "[", "]"));
    }
    
    public void printTree() {
        System.out.println("[");
        for (ParseTree tree : currentElements) {
            QueryParser.print(System.out, "   ", tree);
        }
        System.out.println("]");
    }
    
    /**
     * Create parse of sql string
     * @param sql
     * @return sql string parsed into a <code>SqlQuery</code> object
     */
    public static QueryPart parse(SafeSql sql) {
        QueryParser parser = new QueryParser(sql, true);
        parser.parse();
        return new QueryPart(parser.getParsed());
    }
    
}
