package org.dhatim.safesql.assertion;

import java.util.List;
import org.dhatim.safesql.SafeSql;
import org.dhatim.safesql.parser.SqlTokenizer.Token;

public class Assertions {

    public static SafeSqlAssert assertThat(SafeSql actual) {
        return SafeSqlAssert.assertThat(actual);
    }
    
    public static TokenListAssert assertThat(List<Token> actual) {
        return TokenListAssert.assertThat(actual);
    }
    
    protected Assertions() {
    }
    
}
