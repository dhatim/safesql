package org.dhatim.safesql;

/**
 * An object that implements this interface encapsulates SQL that is guaranteed to use placeholder parameters
 */
public interface SafeSql {

    /**
     * Retrieves the sql query that may contain one or more <code>'?'</code> parameter placeholder
     * @return the sql query
     */
    String asSql();

    /**
     * Retrieves all parameter of the sql query in the correct order
     * @return parameters of the sql query
     */
    Object[] getParameters();

    Dialect getDialect();

    /**
     * Retrieves a version of the sql query that do not contain <code>'?'</code> parameter placeholder
     * @return the sql query with parameter inside
     */
    default String asString() {
        return getDialect().toString(this);
    }

    static SafeSql constant(String s) {
        return Dialect.getDefault().fromConstant(s);
    }

    static SafeSql identifier(String identifier) {
        return Dialect.getDefault().fromIdentifier(identifier);
    }

    static SafeSql parameter(Object parameter) {
        return Dialect.getDefault().fromParameter(parameter);
    }

    static SafeSql literal(String string) {
        return Dialect.getDefault().fromStringLiteral(string);
    }

    static SafeSql concat(SafeSql a, SafeSql b) {
        return SafeSqlUtils.concat(a, b);
    }

}
