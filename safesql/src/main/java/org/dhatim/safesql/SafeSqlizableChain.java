package org.dhatim.safesql;

import java.util.Collection;
import java.util.stream.Stream;

public class SafeSqlizableChain implements SafeSqlizable {

    private final SafeSqlizable[] elements;
    
    public SafeSqlizableChain(SafeSqlizable... array) {
        elements = array.clone();
    }
    
    public SafeSqlizableChain(Collection<SafeSqlizable> collection) {
        elements = collection.toArray(new SafeSqlizable[collection.size()]);
    }
    
    public SafeSqlizableChain(Stream<SafeSqlizable> stream) {
        elements = stream.toArray(SafeSqlizable[]::new);
    }
    
    @Override
    public void appendTo(SafeSqlBuilder builder) {
        for (SafeSqlizable element : elements) {
            builder.append(element);
        }
    }

}
