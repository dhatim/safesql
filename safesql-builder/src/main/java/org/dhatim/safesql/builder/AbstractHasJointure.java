package org.dhatim.safesql.builder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class AbstractHasJointure implements HasJointure {

    private final List<Jointure> jointures = new ArrayList<>();
    
    @Override
    public Jointure innerJoin(String schema, String tableName, Alias alias) {
        return join(JointureType.INNER, schema, tableName, alias);
    }

    @Override
    public Jointure insertInnerJoin(Jointure before, String schema, String tableName, Alias alias) {
        return insertJoin(before, JointureType.INNER, schema, tableName, alias);
    }

    @Override
    public Jointure leftJoin(String schema, String tableName, Alias alias) {
        return join(JointureType.LEFT_OUTER, schema, tableName, alias);
    }
    
    @Override
    public Jointure insertLeftJoin(Jointure before, String schema, String tableName, Alias alias) {
        return insertJoin(before, JointureType.LEFT_OUTER, schema, tableName, alias);
    }
    
    public Jointure insertJoin(Jointure before, JointureType type, String schema, String tableName, Alias alias) {
        int index = jointures.indexOf(before);
        if (index == -1) {
            index = 0;
        }
        Jointure jointure = new Jointure(type, schema, tableName, alias);
        jointures.add(index, jointure);
        return jointure;
    }
    
    public Jointure join(JointureType type, String schema, String tableName, Alias alias) {
        Jointure jointure = new Jointure(type, schema, tableName, alias);
        jointures.add(jointure);
        return jointure;
    }
    
    protected List<Jointure> getJointures() {
        return Collections.unmodifiableList(jointures);
    }
    
    protected boolean hasJointures() {
        return !jointures.isEmpty();
    }

}
