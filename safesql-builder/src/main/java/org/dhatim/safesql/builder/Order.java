package org.dhatim.safesql.builder;

import org.dhatim.safesql.SafeSqlBuilder;
import org.dhatim.safesql.SafeSqlizable;

public enum Order implements SafeSqlizable {
    ASC {
        @Override
        public void appendTo(SafeSqlBuilder builder) {
            builder.append("ASC");
        }
    },
    DESC {
        @Override
        public void appendTo(SafeSqlBuilder builder) {
            builder.append("DESC");
        }
    };
}
