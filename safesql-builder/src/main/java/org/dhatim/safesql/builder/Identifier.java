package org.dhatim.safesql.builder;

import org.dhatim.safesql.SafeSql;
import org.dhatim.safesql.SafeSqlBuilder;
import org.dhatim.safesql.SafeSqlUtils;
import org.dhatim.safesql.SafeSqlizable;

public final class Identifier implements SafeSqlizable {

    private final String name;

    public Identifier(String name) {
        this.name = name;
    }
    
    public String getName() {
        return name;
    }
    
    @Override
    public void appendTo(SafeSqlBuilder builder) {
        builder.appendIdentifier(name);
    }
    
    @Override
    public SafeSql toSafeSql() {
        return SafeSqlUtils.fromIdentifier(name);
    }

}
