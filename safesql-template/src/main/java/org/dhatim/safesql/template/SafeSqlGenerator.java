package org.dhatim.safesql.template;

public class SafeSqlGenerator {

    public static <T extends SafeSqlTemplates> T create(Class<T> templateClass) {
        System.out.println(templateClass.getName());
        return null;
    }
    
}
