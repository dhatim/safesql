package org.dhatim.safesql.builder;

import java.util.Objects;
import org.dhatim.safesql.SafeSql;
import org.dhatim.safesql.SafeSqlBuilder;

public class Column implements Operand {
    
    private static class Distinct implements Operand {

        private final Operand operand;

        public Distinct(Operand operand) {
            this.operand = operand;
        }
        
        @Override
        public SafeSql toSafeSql() {
            return new SafeSqlBuilder().appendConstant("DISTINCT ").append(operand).toSafeSql();
        }
        
    }

    private static final String ALL = "*";
    private static final Column ALL_COLUMN = new Column(ALL);

    private final Alias alias;
    private final String name;

    public Column(Alias alias, String name) {
        this.alias = alias;
        this.name = name;
    }

    public Column(String name) {
        this(null, name);
    }
    
    public Alias getAlias() {
        return alias;
    }
    
    public String getName() {
        return name;
    }

    @Override
    public SafeSql toSafeSql() {
        SafeSqlBuilder sb = new SafeSqlBuilder();
        if (alias != null) {
            sb.append(alias).appendConstant(".");
        }
        return sb.appendIdentifier(name).toSafeSql();
    }
    
    @Override
    public int hashCode() {
        return 56 ^ name.hashCode() ^ Objects.hashCode(alias);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (obj.getClass() == getClass()) {
            Column other = (Column) obj;
            return name.equals(other.name) && Objects.equals(alias, other.alias);
        }
        return false;
    }

    public static Column all() {
        return ALL_COLUMN;
    }

    public static Column allOf(Alias alias) {
        return new Column(alias, ALL);
    }
    
    public static Column idOf(Alias alias) {
        return new Column(alias, "id");
    }
    
    public static Operand distinct(Column column) {
        return new Distinct(column);
    }
    
}
