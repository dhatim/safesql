package org.dhatim.safesql;

import static org.assertj.core.api.Assertions.*;

import static org.dhatim.safesql.SqlTokenizer.TokenType.*;

import java.util.List;
import java.util.stream.Collectors;
import org.dhatim.safesql.SqlTokenizer.Token;
import org.dhatim.safesql.SqlTokenizer.TokenClass;
import org.dhatim.safesql.SqlTokenizer.TokenType;
import org.junit.Test;

public class SqlTokenizerTest {

    @Test
    public void testSimpleSql() {
        String sql = "SELECT jambon FROM charcuterie WHERE taille = 5";
        SqlTokenizer tokenizer = new SqlTokenizer(sql);
        assertThat(tokenizer.stream().map(Token::tokenClass).collect(Collectors.toList()))
                .containsExactly(TokenClass.IDENTIFIER, TokenClass.WHITESPACE, TokenClass.IDENTIFIER, TokenClass.WHITESPACE, TokenClass.IDENTIFIER, TokenClass.WHITESPACE, 
                        TokenClass.IDENTIFIER, TokenClass.WHITESPACE, TokenClass.IDENTIFIER, TokenClass.WHITESPACE, TokenClass.IDENTIFIER, TokenClass.WHITESPACE, 
                        TokenClass.SYMBOL, TokenClass.WHITESPACE, TokenClass.LITERAL);
    }
    
    @Test
    public void testQuotedIndentifier() {
        assertThat(tokenize("\"SELECT\"")).containsExactly(TokenType.QUOTED_IDENTIFIER);
        assertThat(tokenize("\"SELECT WHERE\"")).containsExactly(TokenType.QUOTED_IDENTIFIER);
        assertThat(tokenize("\"SELECT WHERE \"\"hello\"\"\"")).containsExactly(TokenType.QUOTED_IDENTIFIER);
    }
    
    @Test(expected=SqlParseException.class)
    public void testQuotedIndentifierWithZeroCode() {
        tokenize("\"SELECT \0\"");
    }
    
    @Test
    public void testUnicodeQuotedIndentifier() {
        assertThat(tokenize("U&\"d\\0061t\\+000061\"")).containsExactly(TokenType.UNICODE_QUOTED_IDENTIFIER);
        assertThat(tokenize("u&\"d\\0061t\\+000061\"")).containsExactly(TokenType.UNICODE_QUOTED_IDENTIFIER);
    }
    
    @Test
    public void testNonUnicodeQuotedIndentifier() {
        assertThat(tokenize("U&A")).containsExactly(IDENTIFIER, OPERATOR, IDENTIFIER);
    }
    
    @Test
    public void testString() {
        assertThat(tokenize("'SELECT'")).containsExactly(STRING);
        assertThat(tokenize("'SELECT WHERE'")).containsExactly(STRING);
        assertThat(tokenize("'Dianne''s horse'")).containsExactly(TokenType.STRING);
    }
    
    @Test
    public void testEscapedString() {
        assertThat(tokenize("E'foo'")).containsExactly(ESCAPED_STRING);
        assertThat(tokenize("e'foo'")).containsExactly(ESCAPED_STRING);
    }
    
    @Test
    public void testUnicodeString() {
        assertThat(tokenize("U&'d\\0061t\\+000061'")).containsExactly(TokenType.UNICODE_STRING);
        assertThat(tokenize("u&'d\\0061t\\+000061'")).containsExactly(TokenType.UNICODE_STRING);
    }
    
    @Test
    public void testBitString() {
        assertThat(tokenize("B'1001'")).containsExactly(BITSTRING);
        //assertThat(tokenize("b'1001'")).containsExactly(BITSTRING);
    }
    
    private static List<TokenType> tokenize(String sql) {
        SqlTokenizer tokenizer = new SqlTokenizer(sql);
        return tokenizer.stream().map(Token::type).collect(Collectors.toList());
    }
    
}
