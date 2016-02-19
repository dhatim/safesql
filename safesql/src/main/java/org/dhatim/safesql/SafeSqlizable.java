package org.dhatim.safesql;

@FunctionalInterface
public interface SafeSqlizable {
    
    /**
     * Returns a {@link SafeSql} version of this object
     * @return a {@link SafeSql}
     */
    SafeSql toSafeSql();
}
