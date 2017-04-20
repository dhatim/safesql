package org.dhatim.safesql.builder;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.dhatim.safesql.SafeSqlAppendable;
import org.dhatim.safesql.SafeSqlizable;

public class Jointure extends AbstractHasJointure implements WhereClause, SafeSqlizable {

    private final List<Condition> conditions = new ArrayList<>();
    private final String tableName;
    private final Alias alias;
    private final JointureType type;
    private final String schema;

    Jointure(JointureType type, String schema, String tableName, Alias alias) {
        this.type = type;
        this.schema = schema;
        this.tableName = tableName;
        this.alias = alias;
    }
    
    public JointureType getType() {
        return type;
    }

    @Override
    public Jointure and(Condition condition) {
        Objects.requireNonNull(condition);
        conditions.add(condition);
        return this;
    }

    @Override
    public void appendTo(SafeSqlAppendable sb) {
        sb.append(type).append(" ");
        if (hasJointures()) {
            sb.append("(");
        }
        if (schema != null) {
            sb.identifier(schema).append(".");
        }
        sb.identifier(tableName);
        if (alias != null) {
            sb.append(" ").append(alias);
        }
        List<Jointure> jointures = getJointures();
        if (!jointures.isEmpty()) {
            sb.append(" ").joinedSqlizables(" ", jointures).append(")");
        }
        sb.append(" ON ");
        if (!conditions.isEmpty()) {
            sb.joinedSqlizables(" AND ", conditions);
        }
    }

}
