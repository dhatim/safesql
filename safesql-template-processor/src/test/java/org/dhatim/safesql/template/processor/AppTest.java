package org.dhatim.safesql.template.processor;

import static com.google.common.truth.Truth.*;
import static com.google.testing.compile.JavaSourceSubjectFactory.*;

import com.google.testing.compile.JavaFileObjects;
import org.junit.Test;

/**
 * Unit test for simple App.
 */
public class AppTest {
    
    @Test
    public void testSimpleTemplate() {
        assert_().about(javaSource())
            .that(JavaFileObjects.forResource("org/dhatim/safesql/template/SimpleTemplates.java"))
            .processedWith(new TemplateProcessor())
            .compilesWithoutError()
            .withNoteContaining("annotation found");
    }
    
    
}
