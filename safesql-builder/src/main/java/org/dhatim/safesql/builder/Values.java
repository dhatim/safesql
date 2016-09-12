package org.dhatim.safesql.builder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.dhatim.safesql.SafeSqlAppendable;

public class Values implements SqlQuery {

private final ArrayList<Row> rows = new ArrayList<>();
    
    public Values() {
    }
    
    public Values(Row... rows) {
        this(Arrays.asList(rows));
    }
    
    public Values(List<Row> rows) {
        this.rows.addAll(rows);
    }
    
    @Override
    public void appendTo(SafeSqlAppendable builder) {
        validate();
        builder.append("VALUES ");
        builder.appendJoined(", ", rows);
    }
    
    private void validate() {
        if (rows.isEmpty()) {
            throw new BuilderException("VALUES clause muse have at least one row");
        }
        int len = rows.get(0).getLength();
        if (rows.stream().anyMatch(row -> row.getLength() != len)) {
            throw new BuilderException("VALUES rows must have all the same size " + len);
        }
    }

}
