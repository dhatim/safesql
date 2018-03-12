package org.safesql.connection;

public interface Transaction<T> {

    public static interface Status {
        void setRollbackOnly();
        boolean isRollbackOnly();
        boolean isCompleted();
    }

    T execute(Status status);
}
