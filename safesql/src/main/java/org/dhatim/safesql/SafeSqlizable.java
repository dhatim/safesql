package org.dhatim.safesql;

@FunctionalInterface
public interface SafeSqlizable {
    
    /**
     * Returns a {@link SafeSql} version of this object
     * @return a {@link SafeSql}
     */
    default SafeSql toSafeSql() {
        SafeSqlBuilder sb = new SafeSqlBuilder();
        appendTo(sb);
        return sb.toSafeSql();
    }
    
    void appendTo(SafeSqlBuilder builder);
}
