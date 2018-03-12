package org.safesql.connection;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public interface BatchUpdate<T> {
    void setValues(PreparedStatement ps, T element) throws SQLException;
}
