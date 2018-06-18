package org.safesql.connection;

public interface TransactionManager {
    boolean isInsideTransaction();
    int getTransactionLevel();

    default boolean isInsideNestedTransaction() {
        return isInsideTransaction() && getTransactionLevel() > 1;
    }
}
