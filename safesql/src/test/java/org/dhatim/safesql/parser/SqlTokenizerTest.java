package org.dhatim.safesql.parser;

import static org.assertj.core.api.Assertions.*;
import static org.dhatim.safesql.assertion.Assertions.*;
import static org.dhatim.safesql.parser.SqlTokenizer.TokenType.*;

import java.util.List;
import java.util.stream.Collectors;

import org.dhatim.safesql.parser.SqlParseException;
import org.dhatim.safesql.parser.SqlTokenizer;
import org.dhatim.safesql.parser.SqlTokenizer.Token;
import org.dhatim.safesql.parser.SqlTokenizer.TokenClass;
import org.dhatim.safesql.parser.SqlTokenizer.TokenType;
import org.junit.Test;

public class SqlTokenizerTest {

    @Test
    public void testSimpleSql() {
        String sql = "SELECT jambon FROM charcuterie WHERE taille = 5";
        assertThat(tokenize(sql)).hasTokenClasses(TokenClass.IDENTIFIER, TokenClass.WHITESPACE, TokenClass.IDENTIFIER, TokenClass.WHITESPACE, TokenClass.IDENTIFIER, TokenClass.WHITESPACE, 
                        TokenClass.IDENTIFIER, TokenClass.WHITESPACE, TokenClass.IDENTIFIER, TokenClass.WHITESPACE, TokenClass.IDENTIFIER, TokenClass.WHITESPACE, 
                        TokenClass.SYMBOL, TokenClass.WHITESPACE, TokenClass.LITERAL);
    }
    
    @Test 
    public void testWhitespace() {
        assertThat(tokenize(" ")).hasTokens(WHITESPACE).hasValues(" ");
        assertThat(tokenize("\n")).hasTokens(WHITESPACE).hasValues("\n");
        assertThat(tokenize("    \n\n\t\n  ")).hasTokens(WHITESPACE).hasValues("    \n\n\t\n  ");
    }
    
    @Test 
    public void testIdentifier() {
        assertThat(tokenize("a")).hasTokens(IDENTIFIER).hasValues("a");
        assertThat(tokenize("abc")).hasTokens(IDENTIFIER).hasValues("abc");
        assertThat(tokenize("ab$c")).hasTokens(IDENTIFIER).hasValues("ab$c");
        assertThat(tokenize("abc123")).hasTokens(IDENTIFIER).hasValues("abc123");
        assertThat(tokenize("_abc123")).hasTokens(IDENTIFIER).hasValues("_abc123");
    }
    
    @Test
    public void testIdentifier2() {
        assertThat(tokenize("U$")).hasTokens(IDENTIFIER).hasValues("U$");
        assertThat(tokenize("Ub")).hasTokens(IDENTIFIER).hasValues("Ub");
        assertThat(tokenize("U%")).hasTokens(IDENTIFIER, OPERATOR).hasValues("U", "%");
        assertThat(tokenize("E$")).hasTokens(IDENTIFIER).hasValues("E$");
        assertThat(tokenize("E=")).hasTokens(IDENTIFIER, OPERATOR).hasValues("E", "=");
        assertThat(tokenize("b$")).hasTokens(IDENTIFIER).hasValues("b$");
        assertThat(tokenize("b=")).hasTokens(IDENTIFIER, OPERATOR).hasValues("b", "=");
        assertThat(tokenize("X5")).hasTokens(IDENTIFIER).hasValues("X5");
        assertThat(tokenize("X=")).hasTokens(IDENTIFIER, OPERATOR).hasValues("X", "=");

    }
    
    @Test 
    public void testNumeric() {
        assertThat(tokenize("42")).hasTokens(NUMERIC).hasValues("42");
        assertThat(tokenize("3.5")).hasTokens(NUMERIC).hasValues("3.5");
        assertThat(tokenize("4.")).hasTokens(NUMERIC).hasValues("4.");
        assertThat(tokenize(".001")).hasTokens(NUMERIC).hasValues(".001");
        assertThat(tokenize("5e2")).hasTokens(NUMERIC).hasValues("5e2");
        assertThat(tokenize("1.92e-3")).hasTokens(NUMERIC).hasValues("1.92e-3");
    }
    
    @Test 
    public void testNumericAndIdentifier() {
        assertThat(tokenize("2a")).hasTokens(NUMERIC, IDENTIFIER).hasValues("2", "a");
    }
    
    @Test
    public void testQuotedIndentifier() {
        assertThat(tokenize("\"SELECT\"")).hasTokens(TokenType.QUOTED_IDENTIFIER).hasValues("\"SELECT\"");
        assertThat(tokenize("\"SELECT WHERE\"")).hasTokens(TokenType.QUOTED_IDENTIFIER).hasValues("\"SELECT WHERE\"");
        assertThat(tokenize("\"SELECT WHERE \"\"hello\"\"\"")).hasTokens(TokenType.QUOTED_IDENTIFIER).hasValues("\"SELECT WHERE \"\"hello\"\"\"");
    }
    
    @Test(expected=SqlParseException.class)
    public void testQuotedIndentifierWithZeroCode() {
        tokenize("\"SELECT \0\"");
    }
    
    @Test
    public void testUnicodeQuotedIndentifier() {
        assertThat(tokenize("U&\"d\\0061t\\+000061\"")).hasTokens(TokenType.UNICODE_QUOTED_IDENTIFIER).hasValues("U&\"d\\0061t\\+000061\"");
        assertThat(tokenize("u&\"d\\0061t\\+000061\"")).hasTokens(TokenType.UNICODE_QUOTED_IDENTIFIER).hasValues("u&\"d\\0061t\\+000061\"");
    }
    
    @Test
    public void testNonUnicodeQuotedIndentifier() {
        assertThat(tokenize("U&A")).hasTokens(IDENTIFIER, OPERATOR, IDENTIFIER);
    }
    
    @Test
    public void testString() {
        assertThat(tokenize("'SELECT'")).hasTokens(STRING).hasValues("'SELECT'");
        assertThat(tokenize("'SELECT WHERE'")).hasTokens(STRING).hasValues("'SELECT WHERE'");
        assertThat(tokenize("'Dianne''s horse'")).hasTokens(TokenType.STRING).hasValues("'Dianne''s horse'");
    }
    
    @Test(expected=SqlParseException.class)
    public void testNonTerminatedString() {
    	tokenize("'SELECT");
    }
    
    @Test
    public void testEscapedString() {
        assertThat(tokenize("E'foo'")).hasTokens(ESCAPED_STRING).hasValues("E'foo'");
        assertThat(tokenize("e'foo'")).hasTokens(ESCAPED_STRING).hasValues("e'foo'");
    }
    
    @Test
    public void testUnicodeString() {
        assertThat(tokenize("U&'d\\0061t\\+000061'")).hasTokens(TokenType.UNICODE_STRING).hasValues("U&'d\\0061t\\+000061'");
        assertThat(tokenize("u&'d\\0061t\\+000061'")).hasTokens(TokenType.UNICODE_STRING).hasValues("u&'d\\0061t\\+000061'");
    }
    
    @Test
    public void testBitString() {
        assertThat(tokenize("B'1001'")).hasTokens(BITSTRING).hasValues("B'1001'");
        assertThat(tokenize("b'1001'")).hasTokens(BITSTRING).hasValues("b'1001'");
    }
    
    @Test(expected=SqlParseException.class)
    public void testNonBitString() {
        assertThat(tokenize("B'10x01'")).hasTokens(BITSTRING);
    }
    
    @Test
    public void testHexString() {
        assertThat(tokenize("X'1FF'")).hasTokens(HEXSTRING).hasValues("X'1FF'");
        assertThat(tokenize("x'1FF'")).hasTokens(HEXSTRING).hasValues("x'1FF'");
    }
    
    @Test(expected=SqlParseException.class)
    public void testNonHexString() {
        assertThat(tokenize("X'1FzF'")).hasTokens(HEXSTRING);
    }
    
    @Test
    public void testPositionalParameter() {
        assertThat(tokenize("$12")).hasTokens(POSITIONAL_PARAMETER).hasValues("$12");
        assertThat(tokenize("$12ab")).hasTokens(POSITIONAL_PARAMETER, IDENTIFIER).hasValues("$12", "ab");
    }
    
    @Test
    public void testDollarQuotedString() {
    	assertThat(tokenize("$$hello\nthis is it$$")).hasTokens(DOLLAR_QUOTED_STRING).hasValues("$$hello\nthis is it$$");
        assertThat(tokenize("$abc$hello i'm here$abc$")).hasTokens(DOLLAR_QUOTED_STRING).hasValues("$abc$hello i'm here$abc$");
        assertThat(tokenize("$abc$hello $cba$ here$abc$")).hasTokens(DOLLAR_QUOTED_STRING).hasValues("$abc$hello $cba$ here$abc$");
    }
    
    @Test(expected=SqlParseException.class)
    public void testNonTerminatedDollarQuotedString() {
        tokenize("$abc$hello i'm here$cbe$");
    }
    
    @Test(expected=SqlParseException.class)
    public void testNonTerminatedDollarQuotedString2() {
        tokenize("$abc$hello i'm here");
    }
    
    @Test(expected=SqlParseException.class)
    public void testNonTerminatedDollarQuotedString3() {
        tokenize("$abc$hello i'm here$ab");
    }
    
    @Test
    public void testOperator() {
        assertThat(tokenize("-")).hasTokens(OPERATOR).hasValues("-");
        assertThat(tokenize("+")).hasTokens(OPERATOR).hasValues("+");
        assertThat(tokenize("%")).hasTokens(OPERATOR).hasValues("%");
    }
    
    @Test
    public void testMultiCharacterOperator() {
        assertThat(tokenize("/<")).hasTokens(OPERATOR).hasValues("/<");
        assertThat(tokenize("%%")).hasTokens(OPERATOR).hasValues("%%");
        assertThat(tokenize("@-")).hasTokens(OPERATOR).hasValues("@-");
        assertThat(tokenize("*?")).hasTokens(OPERATOR).hasValues("*?");
    }
    
    @Test(expected=SqlParseException.class)
    public void testInvalidMultiCharacterOperator() {
       tokenize("*-");
    }
    
    @Test(expected=SqlParseException.class)
    public void testInvalidMultiCharacterOperator2() {
    	tokenize("**+");
    }
    
    @Test
    public void testMultiCharacterOperator3() {
        assertThat(tokenize("/=<")).hasTokens(OPERATOR).hasValues("/=<");
        assertThat(tokenize("%=%")).hasTokens(OPERATOR).hasValues("%=%");
        assertThat(tokenize("@=-")).hasTokens(OPERATOR).hasValues("@=-");
        assertThat(tokenize("**~")).hasTokens(OPERATOR).hasValues("**~");
    }
    
    @Test
    public void testMultiCharacterOperatorOthers() {
    	assertThat(tokenize("/=+=")).hasTokens(OPERATOR).hasValues("/=+=");
    	assertThat(tokenize("/=+-=")).hasTokens(OPERATOR).hasValues("/=+-=");
    	assertThat(tokenize("/=+/=")).hasTokens(OPERATOR).hasValues("/=+/=");
    	assertThat(tokenize("/=+-?")).hasTokens(OPERATOR).hasValues("/=+-?");
    	assertThat(tokenize("/=+-/")).hasTokens(OPERATOR).hasValues("/=+-/");
    	assertThat(tokenize("/=+-/!")).hasTokens(OPERATOR).hasValues("/=+-/!");
    	assertThat(tokenize("/=+-/?")).hasTokens(OPERATOR).hasValues("/=+-/?");
    	assertThat(tokenize("/=+-//")).hasTokens(OPERATOR).hasValues("/=+-//");
    	assertThat(tokenize("/=~-?")).hasTokens(OPERATOR).hasValues("/=~-?");
    	assertThat(tokenize("/=~-")).hasTokens(OPERATOR).hasValues("/=~-");
    	assertThat(tokenize("/=~-/")).hasTokens(OPERATOR).hasValues("/=~-/");
    	assertThat(tokenize("~=/?")).hasTokens(OPERATOR).hasValues("~=/?");
    	assertThat(tokenize("~=/-")).hasTokens(OPERATOR).hasValues("~=/-");
    	assertThat(tokenize("/=-*")).hasTokens(OPERATOR).hasValues("/=-*");
    	assertThat(tokenize("/=-~")).hasTokens(OPERATOR).hasValues("/=-~");
    	assertThat(tokenize("/=-/")).hasTokens(OPERATOR).hasValues("/=-/");
    	assertThat(tokenize("-+>")).hasTokens(OPERATOR).hasValues("-+>");
    	assertThat(tokenize("->")).hasTokens(OPERATOR).hasValues("->");
    	assertThat(tokenize("-~")).hasTokens(OPERATOR).hasValues("-~");
    	assertThat(tokenize("-/")).hasTokens(OPERATOR).hasValues("-/");
    }
    
    @Test
    public void testMultiCharacterOperatorOthersFollowed() {
    	assertThat(tokenize("~=/-a")).hasTokens(OPERATOR, IDENTIFIER).hasValues("~=/-", "a");
    }
    
    @Test(expected=SqlParseException.class)
    public void testMultiCharacterOperatorWithFinalPlusWithIdent() {
    	tokenize("++-a");
    }
    
    @Test(expected=SqlParseException.class)
    public void testInvalidMultiCharacterOperatorOther() {
    	tokenize("/=+-+");
    }
    
    @Test(expected=SqlParseException.class)
    public void testInvalidMultiCharacterOperator3() {
        tokenize("*/-");
    }
    
    @Test(expected=SqlParseException.class)
    public void testInvalidMultiCharacterOperatorOthers() {
    	tokenize("*/+");
    }
    
    @Test
    public void testLongMultiCharacterOperator() {
        assertThat(tokenize("+++++@@@@@@****////<<<<<>>>>>=====")).hasTokens(OPERATOR).hasValues("+++++@@@@@@****////<<<<<>>>>>=====");
    }
    
    @Test
    public void testLineComment() {
        assertThat(tokenize("--")).hasTokens(LINE_COMMENT).hasValues("--");
        assertThat(tokenize("--Hello")).hasTokens(LINE_COMMENT).hasValues("--Hello");
        assertThat(tokenize("--Lorem ipsum")).hasTokens(LINE_COMMENT).hasValues("--Lorem ipsum");
    }
    
    @Test
    public void testMultipleLineComment() {
        assertThat(tokenize("-- Lorem ipsum dolor sit amet\n-- Lorem ipsum dolor sit amet\n-- Lorem ipsum dolor sit amet"))
                .hasTokens(LINE_COMMENT, WHITESPACE, LINE_COMMENT, WHITESPACE, LINE_COMMENT)
                .hasValues("-- Lorem ipsum dolor sit amet", "\n", "-- Lorem ipsum dolor sit amet", "\n", "-- Lorem ipsum dolor sit amet");
        assertThat(tokenize("-- Lorem ipsum dolor sit amet\n-- Lorem ipsum dolor sit amet\nSELECT"))
                .hasTokens(LINE_COMMENT, WHITESPACE, LINE_COMMENT, WHITESPACE, IDENTIFIER)
                .hasValues("-- Lorem ipsum dolor sit amet", "\n", "-- Lorem ipsum dolor sit amet", "\n", "SELECT");
    }
    
    @Test
    public void testOperatorLineComment() {
    	assertThat(tokenize("+=--")).hasTokens(OPERATOR, LINE_COMMENT).hasValues("+=", "--");
        assertThat(tokenize("~=--")).hasTokens(OPERATOR, LINE_COMMENT).hasValues("~=", "--");
        assertThat(tokenize("+=--++")).hasTokens(OPERATOR, LINE_COMMENT).hasValues("+=", "--++");
    }
    
    @Test
    public void testBlockComment() {
        assertThat(tokenize("/**/")).hasTokens(BLOCK_COMMENT).hasValues("/**/");
        assertThat(tokenize("/*Hello*/")).hasTokens(BLOCK_COMMENT).hasValues("/*Hello*/");
        assertThat(tokenize("/*Lorem ipsum*/")).hasTokens(BLOCK_COMMENT).hasValues("/*Lorem ipsum*/");
        assertThat(tokenize("/*Lorem ipsum* Hello*/")).hasTokens(BLOCK_COMMENT).hasValues("/*Lorem ipsum* Hello*/");
    }
    
    @Test
    public void testMultiLineBlockComment() {
        assertThat(tokenize("/*Lorem\nipsum\ndolor\nsit\namet*/")).hasTokens(BLOCK_COMMENT).hasValues("/*Lorem\nipsum\ndolor\nsit\namet*/");
    }
    
    @Test
    public void testMultiLevelBlockComment() {
        assertThat(tokenize("/*Lorem ipsum /*dolor sit*/ amet*/")).hasTokens(BLOCK_COMMENT).hasValues("/*Lorem ipsum /*dolor sit*/ amet*/");
    }
    
    @Test
    public void testMultiLevelBlockComment2() {
        assertThat(tokenize("/*Lorem ipsum /*dolor /* sit*/ */ amet*/")).hasTokens(BLOCK_COMMENT).hasValues("/*Lorem ipsum /*dolor /* sit*/ */ amet*/");
    }
    
    @Test
    public void testOperatorBlockComment() {
        assertThat(tokenize("+=/**/")).hasTokens(OPERATOR, BLOCK_COMMENT).hasValues("+=", "/**/");
        assertThat(tokenize("~=/**/")).hasTokens(OPERATOR, BLOCK_COMMENT).hasValues("~=", "/**/");
        assertThat(tokenize("+=/*++*/")).hasTokens(OPERATOR, BLOCK_COMMENT).hasValues("+=", "/*++*/");
    }
    
    @Test(expected=SqlParseException.class)
    public void testUnterminatedBlockComment() {
        assertThat(tokenize("/*HEllo"));
    }
    
    @Test(expected=SqlParseException.class)
    public void testUnterminatedBlockComment2() {
        assertThat(tokenize("/* *"));
    }
    
    @Test(expected=SqlParseException.class)
    public void testUnterminatedMultiLevelBlockComment() {
    	assertThat(tokenize("/* Lorem /* Ipsum */"));
    }
    
    @Test
    public void testCastOp() {
        assertThat(tokenize("::")).hasTokens(OPERATOR).hasValues("::");
    }
    
    @Test
    public void testSliceOp() {
        assertThat(tokenize(":")).hasTokens(OPERATOR).hasValues(":");
    }
    
    @Test
    public void testSymbols() {
        assertThat(tokenize("(")).hasTokens(LPAREN).hasValues("(");
        assertThat(tokenize(")")).hasTokens(RPAREN).hasValues(")");
        assertThat(tokenize("[")).hasTokens(LBRACK).hasValues("[");
        assertThat(tokenize("]")).hasTokens(RBRACK).hasValues("]");
        assertThat(tokenize(",")).hasTokens(COMMA).hasValues(",");
        assertThat(tokenize(";")).hasTokens(SEMI).hasValues(";");
        assertThat(tokenize(".")).hasTokens(DOT).hasValues(".");
    }
    
    @Test(expected=SqlParseException.class)
    public void testUnknownCharacter() {
    	tokenize("¶");
    }
    
    private static List<Token> tokenize(String sql) {
        SqlTokenizer tokenizer = new SqlTokenizer(sql);
        return tokenizer.stream().collect(Collectors.toList());
    }
    
}
