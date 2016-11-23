package org.dhatim.safesql;

import java.util.StringTokenizer;

public class SafeSqlRewriter {

    public interface ParameterWriter {
        void writeTo(SafeSqlBuilder sb, Object oldParameter);
    }

    private static final int STATE_0 = 0;
    private static final int STATE_STRING = 1;
    private static final int STATE_IDENT = 2;

    private ParameterWriter writer;

    public SafeSqlRewriter(ParameterWriter writer) {
        this.writer = writer;
    }

    public ParameterWriter getWriter() {
        return writer;
    }

    public void setWriter(ParameterWriter writer) {
        this.writer = writer;
    }

    //TODO miss $$ notation
    public void writeTo(SafeSql value, SafeSqlBuilder sb) {
        Object[] parameters = value.getParameters();
        int index = 0;
        StringTokenizer tokenizer = new StringTokenizer(value.asSql(), "\"'?", true);
        int state = 0;
        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();
            switch (token) {
            case "\"":
                if (state == STATE_0) {
                    state = STATE_IDENT;
                } else if (state == STATE_IDENT) {
                    state = STATE_0;
                }
                sb.append(token);
                break;
            case "'":
                if (state == STATE_0) {
                    state = STATE_STRING;
                } else if (state == STATE_STRING) {
                    state = STATE_0;
                }
                sb.append(token);
                break;
            case "?":
                if (state != STATE_0) {
                    sb.append(token);
                } else {
                    int idx = index++;
                    if (idx >= parameters.length) {
                        throw new IndexOutOfBoundsException("Parameter: " + idx + ", size: " + parameters.length);
                    }
                    Object parameter = parameters[idx];
                    writer.writeTo(sb, parameter);
                }
                break;
            default:
                sb.append(token);
                break;
            }
        }
    }

    public SafeSql write(SafeSql value) {
        SafeSqlBuilder sb = new SafeSqlBuilder();
        writeTo(value, sb);
        return sb.toSafeSql();
    }

}
