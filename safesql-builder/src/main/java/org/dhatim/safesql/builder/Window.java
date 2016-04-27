package org.dhatim.safesql.builder;

import java.util.ArrayList;
import java.util.List;
import org.dhatim.safesql.SafeSqlBuilder;
import org.dhatim.safesql.SafeSqlizable;

public class Window implements SafeSqlizable {

    private final List<Operand> partition = new ArrayList<>();
    
    protected Window() {
    }
    
    public Window(List<Operand> partition) {
        this.partition.addAll(partition);
    }
    
    public List<Operand> getPartition() {
        return new ArrayList<>(partition);
    }
    
    public void setPartition(List<Operand> newPartitions) {
        partition.clear();
        partition.addAll(newPartitions);
    }
    
    public void addPartition(Operand operand) {
        partition.add(operand);
    }

    @Override
    public void appendTo(SafeSqlBuilder builder) {
        builder.append("(PARTITION BY ")
                .appendJoined(", ", partition)
                .append(')');
    }
    
}
