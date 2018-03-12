package org.safesql.connection;

public interface Update extends Result<Tuple> {

    int execute();

}
