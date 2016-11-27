package org.dhatim.safesql.builder;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import org.dhatim.safesql.SafeSqlAppendable;
import org.dhatim.safesql.SafeSqlizable;

public class Template {

    private static interface Element {
        
        SafeSqlizable generate(Operand[] parameters);
        
    }
    
    private static class Parameter implements Element {
        
        private final int index;
        
        public Parameter(int index) {
            this.index = index;
        }
        
        @Override
        public SafeSqlizable generate(Operand[] parameters) {
            if (index > parameters.length) {
                throw new IndexOutOfBoundsException("Index " + index + " in " + parameters.length + " parameters (index 0-based)");
            }
            return parameters[index - 1];
        }
        
    }
    
    private static class Constant extends org.dhatim.safesql.builder.Constant implements Element {

        public Constant(String sql) {
            super(sql);
        }
        
        public SafeSqlizable generate(Operand[] parameters) {
            return this;
        }
        
    }
    
    protected class TemplateOperand implements Operand {
        
        private final SafeSqlizable[] elements;
        
        public TemplateOperand(SafeSqlizable[] elements) {
            this.elements = elements.clone();
        }

        @Override
        public void appendTo(SafeSqlAppendable builder) {
            for (SafeSqlizable element : elements) {
                builder.append(element);
            }
        }
        
        public Template getTemplate() {
            return Template.this;
        }
        
    }
    
    private final String pattern;
    private final ArrayList<Element> elements = new ArrayList<>();
    
    protected Template(String pattern) {
        this.pattern = pattern;
        compile(pattern, elements);
    }
    
    protected void compile(String pattern, List<Element> toList) {
        StringTokenizer tokenizer = new StringTokenizer(pattern, "{}", true);
        boolean parameter = false;
        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();
            switch (token) {
                case "{":
                    parameter = true;
                    break;
                case "}":
                    parameter = false;
                    break;
                default:
                    toList.add(parameter ? toParameter(token) : new Constant(token));
                    break;
            }
        }
    }
    
    public String getPattern() {
        return pattern;
    }

    public static Template of(String pattern) {
        return new Template(pattern);
    }
    
    private static Parameter toParameter(String value) {
        int index = Integer.parseInt(value);
        if (index <= 0) {
            throw new IndexOutOfBoundsException("index < 0 is not valid for SafeSql templates");
        }
        return new Parameter(index);
    }
    
    public Operand generate(Operand... parameters) {
        SafeSqlizable[] parts = new SafeSqlizable[elements.size()];
        for (int i=0; i<elements.size(); i++) {
            parts[i] = elements.get(i).generate(parameters);
        }
        return create(parts);
    }
    
    protected Operand create(SafeSqlizable[] parts) {
        return new TemplateOperand(parts);
    }
    
}
