package org.dhatim.safesql.builder;

import java.util.Arrays;
import org.dhatim.safesql.SafeSqlAppendable;

public class Call implements Operand {

    private final String functionName;
    private final Operand[] arguments;

    public Call(String functionName, Operand... arguments) {
        this.functionName = functionName;
        this.arguments = arguments.clone();
    }
    
    public String getFunctionName() {
        return functionName;
    }
    
    public Operand[] getArguments() {
        return arguments.clone();
    }

    @Override
    public void appendTo(SafeSqlAppendable builder) {
        builder.append(functionName).append("(");
        builder.appendJoinedSqlizable(", ", Arrays.asList(arguments));
        builder.append(")");
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Arrays.hashCode(arguments);
        result = prime * result + functionName.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() == obj.getClass()) {
            Call other = (Call) obj;
            return Arrays.equals(arguments, other.arguments) && functionName.equals(other.functionName);
        }
        return false;
    }

}
