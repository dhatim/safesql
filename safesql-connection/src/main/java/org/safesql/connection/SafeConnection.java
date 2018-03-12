package org.safesql.connection;

import java.util.function.Consumer;
import java.util.stream.Stream;
import org.dhatim.safesql.SafeSql;
import org.dhatim.safesql.SafeSqlizable;

public interface SafeConnection {

    Query query(SafeSql sql);
    Update updateQuery(SafeSql sql);
    int update(SafeSql sql);

    default Query query(SafeSqlizable sql) {
        return query(sql.toSafeSql());
    }

    default Update updateQuery(SafeSqlizable sql) {
        return updateQuery(sql.toSafeSql());
    }

    <T> T readWriteTransaction(Transaction<T> transaction);
    <T> T readOnlyTransaction(Transaction<T> transaction);
    boolean isInTransaction();

    default void rwTransaction(Consumer<Transaction.Status> transaction) {
        readWriteTransaction(status -> {
            transaction.accept(status);
            return null;
        });
    }

    default void roTransaction(Consumer<Transaction.Status> transaction) {
        readOnlyTransaction(status -> {
            transaction.accept(status);
            return null;
        });
    }

    <T> int[] batchUpdate(String sql, Iterable<T> elements, BatchUpdate<T> preparator);
    <T> int[] batchUpdate(String sql, Stream<T> elements, BatchUpdate<T> preparator);


}
