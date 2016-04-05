package org.dhatim.safesql.template;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public interface SafeSqlTemplates {
    
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.SOURCE)
    @interface Template {
        String value();
    }
    
    @Target(ElementType.PARAMETER)
    @Retention(RetentionPolicy.SOURCE)
    @interface Identifier {
    }

}
