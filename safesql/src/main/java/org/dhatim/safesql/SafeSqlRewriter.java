package org.dhatim.safesql;

import java.util.Objects;
import java.util.StringTokenizer;
import java.util.function.BiConsumer;

public abstract class SafeSqlRewriter {

    private static final int STATE_0 = 0;
    private static final int STATE_STRING = 1;
    private static final int STATE_IDENT = 2;
    private static final int STATE_PARAM = 3;

    //TODO miss $$ notation
    public void writeTo(SafeSql value, SafeSqlBuilder sb) {
        Object[] parameters = value.getParameters();
        int index = 0;
        StringTokenizer tokenizer = new StringTokenizer(value.asSql(), "\"'?", true);
        int state = 0;
        int previousState = 0;
        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();
            switch (token) {
                case "\"":
                    if (state == STATE_0) {
                        state = STATE_IDENT;
                    } else if (state == STATE_IDENT) {
                        state = STATE_0;
                    } else if (state == STATE_PARAM) {
                        index = writeParameter(sb, parameters, index);
                    }
                    sb.append(token);
                    break;
                case "'":
                    if (state == STATE_0) {
                        state = STATE_STRING;
                    } else if (state == STATE_STRING) {
                        state = STATE_0;
                    } else if (state == STATE_PARAM) {
                        index = writeParameter(sb, parameters, index);
                    }
                    sb.append(token);
                    break;
                case "?":
                    if (state == STATE_PARAM) {     // double quotation mark
                        sb.append("?");
                        state = STATE_0;
                    } else if (state != STATE_0) {  // quotation mark in string or ident
                        sb.append(token);
                    } else {
                        previousState = state;
                        state = STATE_PARAM;
                    }
                    break;
                default:
                    if (state == STATE_PARAM) {
                        index = writeParameter(sb, parameters, index);
                        state = previousState;
                    }
                    sb.append(token);
                    break;
            }
        }
        if (state == STATE_PARAM) {
            writeParameter(sb, parameters, index);
        }
    }

    private int writeParameter(SafeSqlBuilder sb, Object[] parameters, int index) {
        int idx = index++;
        if (idx >= parameters.length) {
            throw new IndexOutOfBoundsException("Parameter: " + idx + ", size: " + parameters.length);
        }
        Object parameter = parameters[idx];
        processParameter(sb, parameter);
        return idx;
    }

    public SafeSql write(SafeSql value) {
        SafeSqlBuilder sb = new SafeSqlBuilder();
        writeTo(value, sb);
        return sb.toSafeSql();
    }

    protected abstract void processParameter(SafeSqlBuilder sb, Object oldParameter);

    public static SafeSqlRewriter create(BiConsumer<SafeSqlBuilder, Object> parameterRewriter) {
        Objects.requireNonNull(parameterRewriter);
        return new SafeSqlRewriter() {
            @Override
            protected void processParameter(SafeSqlBuilder sb, Object oldParameter) {
                parameterRewriter.accept(sb, oldParameter);
            }
        };
    }

}
