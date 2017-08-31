package org.dhatim.safesql;

import java.util.ArrayList;

public class SafeSqlBuilder extends AbstractSafeSqlBuilder<SafeSqlBuilder> {

    public SafeSqlBuilder() {
        this(new StringBuilder(), new ArrayList<>());
    }

    public SafeSqlBuilder(String query) {
        this(new StringBuilder(query), new ArrayList<>());
    }

    public SafeSqlBuilder(SafeSqlBuilder other) {
        this(new StringBuilder(other.sqlBuilder.toString()), new ArrayList<>(other.parameters));
    }

    private SafeSqlBuilder(StringBuilder stringBuilder, ArrayList<Object> parameters) {
        super(SafeSqlBuilder.class, stringBuilder, parameters);
    }

}
