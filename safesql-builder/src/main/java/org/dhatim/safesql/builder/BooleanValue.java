package org.dhatim.safesql.builder;

public class BooleanValue extends Value implements Condition {

    public BooleanValue(boolean data) {
        super(data);
    }

    @Override
    public Condition negate() {
        return new BooleanValue(!((Boolean) value()));
    }
        
}
