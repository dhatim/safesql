package org.safesql.connection;

public interface Transaction<T> {

    interface Status {
        void rollBack();
        boolean isRolledback();
        boolean isCommited();
    }

    T executeInTransaction(Status status);
}
