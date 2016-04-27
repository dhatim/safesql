package org.dhatim.safesql;

import static org.dhatim.safesql.fixtures.IsSafeSql.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import java.util.Arrays;
import java.util.List;
import org.hamcrest.Matchers;
import org.junit.Test;

public class SafeSqlBuilderTest {
    
    private static class MySafeSqlizable implements SafeSqlizable {
        
        private static final String MUST_BE = "SELECT * FROM table WHERE column = ? GROUP BY id";
        
        @Override
        public void appendTo(SafeSqlBuilder builder) {
            builder.append("SELECT * FROM table WHERE column = ")
                    .param(5)
                    .append(" GROUP BY id");
        }
    }

    @Test
    public void testAppendConstant() {
        assertThat(new SafeSqlBuilder().append("SELECT").append(" * ").append("FROM table").toSafeSql(), safesql(is("SELECT * FROM table"), emptyArray()));
    }
    
    @Test
    public void testAppendNumber() {
        assertThat(new SafeSqlBuilder().param(5).append(" ").param(1.1).toSafeSql(), safesql(is("? ?"), Matchers.<Object>arrayContaining(5, 1.1)));
    }
    
    @Test
    public void testAppendObject() {
        assertThat(new SafeSqlBuilder().param(true).toSafeSql(), safesql(is("?"), Matchers.<Object>arrayContaining(true)));
    }
    
    @Test
    public void testAppendIdentifier() {
        assertThat(new SafeSqlBuilder().append("SELECT ").appendIdentifier("S21.G00.32.001").toSafeSql(), safesql(is("SELECT \"S21.G00.32.001\""), emptyArray()));
        assertThat(new SafeSqlBuilder().append("SELECT ").appendIdentifier("hello").toSafeSql(), safesql(is("SELECT hello"), emptyArray()));
    }
    
    @Test
    public void testAppendEscaped() {
        assertThat(new SafeSqlBuilder().append("SELECT * FORM table WHERE column = ").param("Hello the world").toSafeSql(), 
                safesql(is("SELECT * FORM table WHERE column = ?"), arrayContaining("Hello the world")));
    }
    
    @Test
    public void testAppendSafeSql() {
        assertThat("without parameters", new SafeSqlBuilder().append("SELECT").append(SafeSqlUtils.fromConstant(" * FROM table")).toSafeSql(), safesql(is("SELECT * FROM table"), emptyArray()));
        assertThat("with parameters", new SafeSqlBuilder().append("SELECT ").append(SafeSqlUtils.escape("Hello the world")).toSafeSql(), safesql(is("SELECT ?"), arrayContaining("Hello the world")));
    }
    
    @Test
    public void testAppendSafeSqlizable() {
        assertThat(new SafeSqlBuilder().append(new MySafeSqlizable()).append(" ORDER BY name").toSafeSql(), safesql(is(MySafeSqlizable.MUST_BE + " ORDER BY name"), arrayContaining(5)));
    }
    
    @Test
    public void testAppendJoined() {
        List<SafeSqlizable> list = Arrays.asList(new MySafeSqlizable(), new MySafeSqlizable());
        assertThat(new SafeSqlBuilder().append("(").appendJoined("; ", list).append(")").toSafeSql(), safesql(is("(" + MySafeSqlizable.MUST_BE + "; " + MySafeSqlizable.MUST_BE + ")"), 
                arrayContaining(5, 5)));
        
        assertThat(new SafeSqlBuilder().appendJoined(", ", Arrays.asList(new MySafeSqlizable())).toSafeSql(), safesql(is(MySafeSqlizable.MUST_BE), arrayContaining(5)));
        assertThat(new SafeSqlBuilder().appendJoined(", ", Arrays.asList()).toSafeSql(), safesql(isEmptyString(), emptyArray()));
    }
    
}
