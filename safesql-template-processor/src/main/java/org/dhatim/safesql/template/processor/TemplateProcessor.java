package org.dhatim.safesql.template.processor;

import java.util.Set;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import org.dhatim.safesql.template.SafeSqlTemplates;

@SupportedAnnotationTypes({"org.dhatim.safesql.template.SafeSqlTemplates.Template"})
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class TemplateProcessor extends AbstractProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (Element elem : roundEnv.getElementsAnnotatedWith(SafeSqlTemplates.Template.class)) {
            String message = "annotation found in " + elem.getSimpleName();
            processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, message);
            
        }
        
        //processingEnv.getFiler().createSourceFile("lol.java").
        
        
        return true;
    }

}
