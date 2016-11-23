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

    /**
     * Retrieves a version of the sql query that do not contain <code>'?'</code> parameter placeholder
     * @return the sql query with parameter inside
     */
    default String asString() {
        return SafeSqlUtils.toString(this);
    }

    static SafeSql constant(String s) {
        return SafeSqlUtils.fromConstant(s);
    }

    static SafeSql identifier(String identifier) {
        return SafeSqlUtils.fromIdentifier(identifier);
    }

    static SafeSql parameter(Object parameter) {
        return SafeSqlUtils.escape(parameter);
    }

}
