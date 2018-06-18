package org.dhatim.safesql;

import java.util.Arrays;

class ConcatenatedSafeSql implements SafeSql {

    private final SafeSql a;
    private final SafeSql b;

    private String string;

    public ConcatenatedSafeSql(SafeSql a, SafeSql b) {
        if (a.getDialect() != b.getDialect()) {
            throw new IncompatibleDialectException();
        }
        this.a = a;
        this.b = b;
    }

    @Override
    public String asSql() {
        return a.asSql() + b.asSql();
    }

    @Override
    public Object[] getParameters() {
        Object[] pa = a.getParameters();
        Object[] pb = b.getParameters();
        Object[] params = Arrays.copyOf(pa, pa.length + pb.length);
        System.arraycopy(pb, 0, params, pa.length, pb.length);
        return params;
    }

    @Override
    public Dialect getDialect() {
        return null;
    }

    @Override
    public String asString() {
        if (string == null) {
            string = SafeSql.super.asString();
        }
        return string;
    }

}
