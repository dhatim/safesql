package org.safesql.connection;

public interface Update extends Result<Tuple> {

    long execute();

}
