package org.safesql.connection;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import org.dhatim.safesql.SafeSql;
import org.dhatim.safesql.SafeSqlizable;

public interface JdbcTemplateKind extends SafeConnection {

    default <T> T queryForObject(SafeSql sql, Class<T> requiredType) {
        return query(sql).as(requiredType).toObject();
    }

    default <T> T queryForObject(SafeSql sql, Function<Map<String, Object>, T> factory) {
        return query(sql).map(TupleMapAdapter::create).map(factory).toObject();
    }

    default <T> T queryForObject(SafeSqlizable sql, Function<Map<String, Object>, T> factory) {
        return query(sql).map(TupleMapAdapter::create).map(factory).toObject();
    }

    default <T> T queryForObject(SafeSqlizable sql, Class<T> requiredType) {
        return query(sql).as(requiredType).toObject();
    }

    default <T> Optional<T> queryForOptional(SafeSql sql, Class<T> requiredType) {
        return query(sql).as(requiredType).toOptional();
    }

    default <T> Optional<T> queryForOptional(SafeSqlizable sql, Class<T> requiredType) {
        return query(sql).as(requiredType).toOptional();
    }

    default <T> Optional<T> queryForOptional(SafeSql sql, Function<Map<String, Object>, T> factory) {
        return query(sql).map(TupleMapAdapter::create).map(factory).toOptional();
    }

    default <T> Optional<T> queryForOptional(SafeSqlizable sql, Function<Map<String, Object>, T> factory) {
        return query(sql).map(TupleMapAdapter::create).map(factory).toOptional();
    }

    default Map<String, Object> queryForMap(SafeSql sql) {
        return query(sql).map(TupleMapAdapter::create).toObject();
    }

    default Map<String, Object> queryForMap(SafeSqlizable sql) {
        return query(sql).map(TupleMapAdapter::create).toObject();
    }

    default List<Map<String, Object>> queryForList(SafeSql sql) {
        return query(sql).map(TupleMapAdapter::create).toList();
    }

    default List<Map<String, Object>> queryForList(SafeSqlizable sql) {
        return query(sql).map(TupleMapAdapter::create).toList();
    }

    default <T> List<T> queryForList(SafeSql sql, Class<T> elementType) {
        return query(sql).as(elementType).toList();
    }

    default <T> List<T> queryForList(SafeSqlizable sql, Class<T> elementType) {
        return query(sql).as(elementType).toList();
    }

    default <T> List<T> queryForList(SafeSql sql, Function<Map<String, Object>, T> factory) {
        return query(sql).map(TupleMapAdapter::create).map(factory).toList();
    }

    default <T> List<T> queryForList(SafeSqlizable sql, Function<Map<String, Object>, T> factory) {
        return query(sql).map(TupleMapAdapter::create).map(factory).toList();
    }

    default <T> Set<T> queryForSet(SafeSql sql, Class<T> elementType) {
        return query(sql).as(elementType).toSet();
    }

    default <T> Set<T> queryForSet(SafeSqlizable sql, Class<T> elementType) {
        return query(sql).as(elementType).toSet();
    }

    default <T> Set<T> queryForSet(SafeSql sql, Function<Map<String, Object>, T> factory) {
        return query(sql).map(TupleMapAdapter::create).map(factory).toSet();
    }

    default <T> Set<T> queryForSet(SafeSqlizable sql, Function<Map<String, Object>, T> factory) {
        return query(sql).map(TupleMapAdapter::create).map(factory).toSet();
    }

}
