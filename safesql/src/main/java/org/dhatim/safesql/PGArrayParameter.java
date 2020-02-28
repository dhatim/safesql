package org.dhatim.safesql;

import org.postgresql.util.PGobject;

import java.util.stream.StreamSupport;

@SuppressWarnings("serial")
public class PGArrayParameter<T> extends PGobject implements SafeSqlLiteralizable {

    public PGArrayParameter(String type, Iterable<T> c) {
        this(type, StreamSupport.stream(c.spliterator(), false).toArray());
    }

    public PGArrayParameter(String type, Object[] values) {
        this.type = type + "[]";
        this.value = asValue(values);
    }

    @Override
    public void appendLiteralized(SafeSqlBuilder sb) {
        sb.append("'").append(getValue()).append("'::").append(type);
    }

    private String asValue(Object[] values) {
        return ArraySupport.toString(values);
    }


}
