package org.safesql.connection;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

public interface Query extends Result<Tuple> {

    <T> Result<T> as(Class<T> requiredType);

    Query withFetchSize(int fetchSize);

    Query withQueryTimeout(int queryTimeout);

    default Result<Boolean> asBoolean() {
        return as(Boolean.class);
    }

    default Result<Integer> asInteger() {
        return as(Integer.class);
    }

    default Result<Long> asLong() {
        return as(Long.class);
    }

    default Result<String> asString() {
        return as(String.class);
    }

    default Result<BigDecimal> asBigDecimal() {
        return as(BigDecimal.class);
    }

    default Result<UUID> asUUID() {
        return as(UUID.class);
    }

    default Boolean toBoolean() {
        return as(Boolean.class).toObject();
    }

    default Integer toInteger() {
        return as(Integer.class).toObject();
    }

    default Long toLong() {
        return as(Long.class).toObject();
    }

    default BigDecimal toBigDecimal() {
        return as(BigDecimal.class).toObject();
    }

    default UUID toUUID() {
        return as(UUID.class).toObject();
    }

    default String toStringValue() {
        return as(String.class).toObject();
    }

    default Optional<Boolean> toOptionalBoolean() {
        return as(Boolean.class).toOptional();
    }

    default Optional<Integer> toOptionalInteger() {
        return as(Integer.class).toOptional();
    }

    default Optional<Long> toOptionalLong() {
        return as(Long.class).toOptional();
    }

    default Optional<BigDecimal> toOptionalBigDecimal() {
        return as(BigDecimal.class).toOptional();
    }

    default Optional<UUID> toOptionalUUID() {
        return as(UUID.class).toOptional();
    }

    default Optional<String> toOptionalString() {
        return as(String.class).toOptional();
    }

}
