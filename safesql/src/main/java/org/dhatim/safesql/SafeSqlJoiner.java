package org.dhatim.safesql;

import java.util.Objects;

public class SafeSqlJoiner implements SafeSqlizable {
    
    private final SafeSql prefix;
    private final SafeSql delimiter;
    private final SafeSql suffix;
    
    private SafeSqlBuilder value;

    private SafeSql emptyValue;
    
    public SafeSqlJoiner(SafeSql delimiter) {
        this(delimiter, SafeSqlUtils.EMPTY, SafeSqlUtils.EMPTY);
    }
    
    public SafeSqlJoiner(SafeSql delimiter, SafeSql prefix, SafeSql suffix) {
        Objects.requireNonNull(prefix, "The prefix must not be null");
        Objects.requireNonNull(delimiter, "The delimiter must not be null");
        Objects.requireNonNull(suffix, "The suffix must not be null");
        this.prefix = prefix;
        this.delimiter = delimiter;
        this.suffix = suffix;
        this.emptyValue = SafeSqlUtils.concat(this.prefix, this.suffix);
    }
    
    public SafeSqlJoiner setEmptyValue(SafeSql emptyValue) {
        this.emptyValue = Objects.requireNonNull(emptyValue, "The empty value must not be null");
        return this;
    }
    
    public SafeSqlJoiner add(SafeSql newElement) {
        prepareBuilder().append(newElement);
        return this;
    }
    
    public SafeSqlJoiner add(SafeSqlizable newElement) {
        prepareBuilder().append(newElement.toSafeSql());
        return this;
    }
    
    public SafeSqlJoiner merge(SafeSqlJoiner other) {
        Objects.requireNonNull(other);
        if (other.value != null) {
            SafeSqlBuilder builder = prepareBuilder();
            builder.append(other.value, SafeSqlBuilder.getLength(other.prefix));
        }
        return this;
    }
    
    private SafeSqlBuilder prepareBuilder() {
        if (value != null) {
            value.append(delimiter);
        } else {
            value = new SafeSqlBuilder().append(prefix);
        }
        return value;
    }
    
    @Override
    public void appendTo(SafeSqlBuilder builder) {
        if (value == null) {
            builder.append(emptyValue);
        } else {
            builder.append(value);
            builder.append(suffix);
        }
    }
    
    @Override
    public SafeSql toSafeSql() {
        if (value == null) {
            return emptyValue;
        } else {
            if (SafeSqlUtils.isEmpty(suffix)) {
                return value.toSafeSql();
            } else {
                SafeSqlBuilder.Position pos = value.getLength();
                SafeSql result = value.append(suffix).toSafeSql();
                value.setLength(pos);
                return result;
            }
        }
    }
    
}
