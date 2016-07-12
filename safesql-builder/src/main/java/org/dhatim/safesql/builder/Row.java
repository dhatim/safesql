package org.dhatim.safesql.builder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.dhatim.safesql.SafeSqlBuilder;

public class Row implements Operand {

    private final ArrayList<Operand> elements = new ArrayList<>();

    public Row(Operand... elements) {
        this(Arrays.asList(elements));
    }
    
    public Row(List<Operand> elements) {
        this.elements.addAll(elements);
    }
    
    public List<Operand> getElements() {
        return Collections.unmodifiableList(elements);
    }
    
    public int getLength() {
        return elements.size();
    }

    @Override
    public void appendTo(SafeSqlBuilder builder) {
        builder.append("(").appendJoined(", ", elements).append(")");
    }

}
