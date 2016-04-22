package org.dhatim.safesql;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class SafeSqlJoiner implements SafeSqlizable {
    
    private static class InternalBuilder {
        
        private final StringBuilder builder = new StringBuilder();
        private final ArrayList<Object> parameters = new ArrayList<>();
        
        public InternalBuilder append(SafeSql sql) {
            builder.append(sql.asSql());
            for (Object param : sql.getParameters()) {
                parameters.add(param);
            }
            return this;
        }
        
        public void append(InternalBuilder other, SafeSql after) {
            builder.append(other.builder, after.asSql().length(), other.builder.length());
            int afterLength = after.getParameters().length;
            parameters.addAll(Arrays.asList(other.parameters).subList(afterLength, other.parameters.size() - afterLength));
        }
        
        public SafeSql toSafeSql() {
            return new SafeSqlImpl(builder.toString(), parameters.toArray());
        }
        
        public void setLength(int sqlLength, int paramCount) {
            builder.setLength(sqlLength);
            int currentSize = parameters.size();
            if (paramCount < currentSize) {
                parameters.subList(paramCount, currentSize).clear();
            }
        }
        
    }
    
    private final SafeSql prefix;
    private final SafeSql delimiter;
    private final SafeSql suffix;
    
    private InternalBuilder value;

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
            InternalBuilder builder = prepareBuilder();
            builder.append(other.value, other.prefix);
        }
        return this;
    }
    
    private InternalBuilder prepareBuilder() {
        if (value != null) {
            value.append(delimiter);
        } else {
            value = new InternalBuilder().append(prefix);
        }
        return value;
    }
    
    @Override
    public void appendTo(SafeSqlBuilder builder) {
        builder.append(toSafeSql());
    }
    
    @Override
    public SafeSql toSafeSql() {
        if (value == null) {
            return emptyValue;
        } else {
            if (SafeSqlUtils.isEmpty(suffix)) {
                return value.toSafeSql();
            } else {
                int initialSqlLength = value.builder.length();
                int initialParamCount = value.parameters.size();
                SafeSql result = value.append(suffix).toSafeSql();
                value.setLength(initialSqlLength, initialParamCount);
                return result;
            }
        }
    }
    
}
