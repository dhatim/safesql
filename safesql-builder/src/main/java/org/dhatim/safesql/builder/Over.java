package org.dhatim.safesql.builder;

import org.dhatim.safesql.SafeSqlBuilder;

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
    public void appendTo(SafeSqlBuilder sb) {
        sb.append(windowFunction);
        sb.appendConstant(" OVER ");
        if (window instanceof NamedWindow) {
            sb.appendIdentifier(((NamedWindow) window).getName());
        } else {
            sb.append(window);
        }
    }

}
