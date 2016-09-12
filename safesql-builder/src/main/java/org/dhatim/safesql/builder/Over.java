package org.dhatim.safesql.builder;

import org.dhatim.safesql.SafeSqlAppendable;

public class Over implements Operand {

    private final Call windowFunction;
    private final Window window;
    
    public Over(Call windowFunction, Window window) {
        this.windowFunction = windowFunction;
        this.window = window;
    }
    
    public Window getWindow() {
        return window;
    }
    
    public Call getWindowFunction() {
        return windowFunction;
    }
    
    @Override
    public void appendTo(SafeSqlAppendable sb) {
        sb.append(windowFunction);
        sb.append(" OVER ");
        if (window instanceof NamedWindow) {
            sb.appendIdentifier(((NamedWindow) window).getName());
        } else {
            sb.append(window);
        }
    }

}
