package org.dhatim.safesql;

import java.util.Objects;
import org.dhatim.safesql.dialect.PostgresDialect;

public abstract class Dialect {

    private static Dialect instance = new PostgresDialect();

    private final SafeSql empty = new StringSafeSqlImpl(this, "");

    public static Dialect getDefault() {
        return instance;
    }

    public static void setDefaultDialect(Dialect dialect) {
        instance = Objects.requireNonNull(dialect);
    }

    public abstract StringBuilder escapeStringLiteral(StringBuilder builder, String s);

    public abstract StringBuilder escapeBytesLiteral(StringBuilder builder, byte[] bytes);

    public abstract StringBuilder escapeIdentifier(StringBuilder builder, String identifier);

    public String toString(SafeSql sql) {
        return literalize(sql).asSql();
    }

    public final SafeSql fromConstant(String s) {
        Objects.requireNonNull(s);
        if (s.isEmpty()) {
            return empty();
        }
        return new StringSafeSqlImpl(this, s);
    }

    public SafeSql empty() {
        return empty;
    }

    public final SafeSql fromIdentifier(String identifier) {
        StringBuilder builder = new StringBuilder();
        escapeIdentifier(builder, identifier);
        return new StringSafeSqlImpl(this, builder.toString());
    }

    public final SafeSql fromParameter(Object parameter) {
        return new SafeSqlImpl(this, "?", new Object[]{parameter});
    }

    public final SafeSql fromStringLiteral(String string) {
        StringBuilder builder = new StringBuilder();
        escapeStringLiteral(builder, string);
        return new StringSafeSqlImpl(this, builder.toString());
    }

    protected abstract void appendLiteralizedParameter(SafeSqlBuilder sb, Object obj);

    public abstract String escapeLikeValue(String s, char escapeChar);

    public abstract String escapeLikeValue(String s);

    public SafeSql literalize(SafeSql sql) {
        return SafeSqlRewriter.create(this::appendLiteralizedParameter).write(sql);
    }

}
