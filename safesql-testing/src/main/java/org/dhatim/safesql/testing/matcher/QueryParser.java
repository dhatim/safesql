package org.dhatim.safesql.testing.matcher;

import java.io.PrintStream;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.dhatim.safesql.SafeSql;
import org.dhatim.safesql.testing.ParseException;
import org.dhatim.safesql.testing.parser.PSQLLexer;
import org.dhatim.safesql.testing.parser.PSQLParser;

public class QueryParser {
    
    private class ParserListener extends BaseErrorListener {
        
        @Override
        public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {
            if (raiseErrors) {
                throw new ParseException(msg, e);
            }
        }
        
    }
    
    private final SafeSql sql;
    private final boolean raiseErrors;
    
    private PSQLParser parser;

    public QueryParser(SafeSql sql, boolean raiseErrors) {
        this.sql = sql;
        this.raiseErrors = raiseErrors;
    }
    
    public void parse() {
        CharStream inputStream = new ANTLRInputStream(sql.asString());
        PSQLLexer lexer = new PSQLLexer(inputStream);
        lexer.removeErrorListeners();
        lexer.addErrorListener(new ParserListener());
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        parser = new PSQLParser(tokens);
        parser.removeErrorListeners();
        parser.addErrorListener(new ParserListener());
    }
    
    public static void print(PrintStream out, String indent, ParseTree tree) {
        ParseTreeWalker walker = new ParseTreeWalker();
        walker.walk(new ParseTreeListener() {
            private int spaces = 0;

            @Override
            public void enterEveryRule(ParserRuleContext ctx) {
                ln("> " + nameOf(ctx.getRuleIndex()));
                spaces++;
            }

            @Override
            public void exitEveryRule(ParserRuleContext ctx) {
                spaces--;
                ln("< " + nameOf(ctx.getRuleIndex()));
            }

            @Override
            public void visitErrorNode(ErrorNode node) {
                ln("X " + node.getText());
            }

            @Override
            public void visitTerminal(TerminalNode node) {
                ln("| " + terminalNameOf(node.getSymbol().getType()) + " => " + node.getText());
            }
            
            private String nameOf(int id) {
                return PSQLParser.ruleNames[id];
            }
            
            private String terminalNameOf(int type) {
                return PSQLLexer.VOCABULARY.getDisplayName(type);
            }

            private void ln(String s) {
                out.println(indent + toSpaces() + s);
            }

            private String toSpaces() {
                return space(spaces * 2);
            }
            
            private String space(int n) {
                return Stream.generate(() -> " ").limit(n).collect(Collectors.joining());
            }
        }, tree);
    }
    
    public void print(PrintStream out) {
        requireParser();
        print(out, "", parser.sql());
    }
    
    private void requireParser() {
        if (parser == null) {
            throw new IllegalStateException("No parsing has been done");
        }
    }
    
    public PSQLParser getParsed() {
        return parser;
    }
    
}
