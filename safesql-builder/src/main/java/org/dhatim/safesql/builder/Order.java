package org.dhatim.safesql.builder;

import org.dhatim.safesql.SafeSqlAppendable;
import org.dhatim.safesql.SafeSqlizable;

public enum Order implements SafeSqlizable {
    ASC {
        @Override
        public void appendTo(SafeSqlAppendable builder) {
            builder.append("ASC");
        }
    },
    DESC {
        @Override
        public void appendTo(SafeSqlAppendable builder) {
            builder.append("DESC");
        }
    };
}
