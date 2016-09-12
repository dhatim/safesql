package org.dhatim.safesql.builder;

import java.util.List;
import org.dhatim.safesql.SafeSqlAppendable;

public class NamedWindow extends Window {

    private final String name;
    
    public NamedWindow(String name) {
        this.name = name;
    }
    
    public NamedWindow(String name, List<Operand> partition) {
        super(partition);
        this.name = name;
    }
    
    public String getName() {
        return name;
    }
    
    @Override
    public void appendTo(SafeSqlAppendable builder) {
        builder.appendIdentifier(name)
            .append(" AS ");
        super.appendTo(builder);
    }
    
    @Override
    public int hashCode() {
        return name.hashCode();
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (obj.getClass() == getClass()) {
            return name.equals(((NamedWindow) obj).name);
        }
        return false;
    }
    
}
