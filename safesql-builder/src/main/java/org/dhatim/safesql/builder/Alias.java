package org.dhatim.safesql.builder;

import java.util.Objects;
import org.dhatim.safesql.SafeSql;
import org.dhatim.safesql.SafeSqlBuilder;
import org.dhatim.safesql.SafeSqlUtils;
import org.dhatim.safesql.SafeSqlizable;

public final class Alias implements SafeSqlizable {

    private final AliasOwner owner;
    private final String name;

    public Alias(AliasOwner owner, String name) {
        Objects.requireNonNull(name, "Alias name must be not null");
        if (name.isEmpty()) {
            throw new IllegalArgumentException("Alias name must be not empty");
        }
        this.owner = owner;
        this.name = name;
    }

    public AliasOwner getOwner() {
        return owner;
    }

    public String getName() {
        return name;
    }

    @Override
    public int hashCode() {
        return Objects.hash(owner, name);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof Alias) {
            Alias other = (Alias) obj;
            return other.name.equals(name) && Objects.equals(owner, other.owner);
        }

        return false;
    }

    @Override
    public SafeSql toSafeSql() {
        return SafeSqlUtils.fromIdentifier(name);
    }

    @Override
    public void appendTo(SafeSqlBuilder builder) {
        builder.identifier(name);
    }

}
