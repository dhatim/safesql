package org.dhatim.safesql.builder;

import static org.dhatim.safesql.assertion.Assertions.*;

import org.junit.Test;

public class TemplateTest {

    @Test
    public void testSimple() {
        Template template = Template.of("POSITION({1} IN {2})");
        Operand operand = template.generate(Literal.of("searched"), Literal.of("string"));
        assertThat(operand.toSafeSql()).hasSql("POSITION('searched' IN 'string')");
    }
    
    @Test(expected=IndexOutOfBoundsException.class)
    public void testWithInvalidIndex() {
        Template template = Template.of("POSITION({1} IN {3})");
        Operand operand = template.generate(Literal.of("searched"), Literal.of("string"));
        assertThat(operand.toSafeSql()).hasSql("POSITION('searched' IN 'string')");
    }
    
    @Test(expected=IndexOutOfBoundsException.class)
    public void testWith0Index() {
        Template template = Template.of("POSITION({0} IN {3})");
        Operand operand = template.generate(Literal.of("searched"), Literal.of("string"));
        assertThat(operand.toSafeSql()).hasSql("POSITION('searched' IN 'string')");
    }
    
    @Test(expected=IndexOutOfBoundsException.class)
    public void testWithoutParameters() {
        Template template = Template.of("POSITION({1} IN {2})");
        Operand operand = template.generate();
        assertThat(operand.toSafeSql()).hasSql("POSITION('searched' IN 'string')");
    }
    
    @Test
    public void testNoParameters() {
        Template template = Template.of("'Hello'");
        Operand operand = template.generate();
        assertThat(operand.toSafeSql()).hasSql("'Hello'");
    }
    
}
