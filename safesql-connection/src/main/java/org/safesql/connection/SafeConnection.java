package org.safesql.connection;

import java.util.Iterator;
import java.util.stream.Stream;
import org.dhatim.safesql.SafeSql;
import org.dhatim.safesql.SafeSqlizable;

public interface SafeConnection {

    Query query(SafeSql sql);
    Update update(SafeSql sql);
    long directUpdate(SafeSql sql);

    default Query query(SafeSqlizable sql) {
        return query(sql.toSafeSql());
    }
    default Update update(SafeSqlizable sql) {
        return update(sql.toSafeSql());
    }
    default long directUpdate(SafeSqlizable sql) {
        return directUpdate(sql.toSafeSql());
    }

    <T> T beginRead(Transaction<T> transaction);
    <T> T beginWrite(Transaction<T> transaction);

    TransactionManager getTransactionManager();

    <T> int[] batchUpdate(String sql, Iterator<T> iterator, BatchUpdate<T> batch);

    default <T> int[] batchUpdate(String sql, Iterable<T> iterable, BatchUpdate<T> batch) {
        return batchUpdate(sql, iterable.iterator(), batch);
    }

}
