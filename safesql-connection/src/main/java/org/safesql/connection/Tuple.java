package org.safesql.connection;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

public interface Tuple {

    boolean contains(String columnLabel);
    int size();
    <T> T get(String columnLabel);

    /**
     * Retrieves the value of the designated column in the current row
     * of this {@link Tuple} object as
     * a {@link String} in the Java programming language.
     *
     * @param columnLabel the column name
     * @return the column value; if the value is SQL <code>NULL</code>, the
     * value returned is <code>null</code>
     * @exception DatabaseAccessorException if the columnName is not found
     */
    String getString(String columnLabel);

    Boolean getBoolean(String columnLabel);

    /**
     * Retrieves the value of the designated column in the current row
     * of this {@link Tuple} object as
     * an {@link Integer} in the Java programming language.
     *
     * <p>Used for PostgreSQL data types {@code smallint}, {@code integer} and {@code serial}.
     *
     * @param columnLabel the column name
     * @return the column value; if the value is SQL <code>NULL</code>, the
     * value returned is <code>null</code>
     * @exception DatabaseAccessorException if the columnName is not found
     */
    Integer getInt(String columnLabel);

    /**
     * Retrieves the value of the designated column in the current row
     * of this {@link Tuple} object as
     * a {@link Long} in the Java programming language.
     *
     * <p>Used for PostgreSQL data types {@code bigint} and {@code bigserial}.
     * Can be used for PostgreSQL data types {@code smallint}, {@code integer}
     * and {@code serial}.
     *
     * @param columnLabel the column name
     * @return the column value; if the value is SQL <code>NULL</code>, the
     * value returned is <code>null</code>
     * @exception DatabaseAccessorException if the columnName is not found
     */
    Long getLong(String columnLabel);

    /**
     * Retrieves the value of the designated column in the current row
     * of this {@link Tuple} object as
     * a {@link Float} in the Java programming language.
     *
     * <p>Used for PostgreSQL data type {@code real}.
     * Can be used for PostgreSQL data types {@code smallint}, {@code integer},
     * {@code serial}, {@code bigint} and {@code bigserial}.
     *
     * @param columnLabel the column name
     * @return the column value; if the value is SQL <code>NULL</code>, the
     * value returned is <code>null</code>
     * @exception DatabaseAccessorException if the columnName is not found
     */
    Float getFloat(String columnLabel);

    /**
     * Retrieves the value of the designated column in the current row
     * of this {@link Tuple} object as
     * a {@link Double} in the Java programming language.
     *
     * <p>Used for PostgreSQL data type {@code double precision}.
     * Can be used for PostgreSQL data types {@code smallint}, {@code integer},
     * {@code serial}, {@code bigint}, {@code bigserial} and {@code real}.
     *
     * @param columnLabel the column name
     * @return the column value; if the value is SQL <code>NULL</code>, the
     * value returned is <code>null</code>
     * @exception DatabaseAccessorException if the columnName is not found
     */
    Double getDouble(String columnLabel);

    /**
     * Retrieves the value of the designated column in the current row
     * of this {@link Tuple} object as
     * a {@link BigDecimal} in the Java programming language.
     *
     * <p>Used for PostgreSQL data types {@code decimal} and {@code numeric}.
     * Can be used for PostgreSQL data types {@code smallint}, {@code integer},
     * {@code serial}, {@code bigint}, {@code bigserial}, {@code real}
     * and {@code double precision}.
     *
     * @param columnLabel the column name
     * @return the column value; if the value is SQL <code>NULL</code>, the
     * value returned is <code>null</code>
     * @exception DatabaseAccessorException if the columnName is not found
     */
    BigDecimal getBigDecimal(String columnLabel);

    /**
     * Retrieves the value of the designated column in the current row
     * of this {@link Tuple} object as
     * a {@link LocalDate} in the Java programming language.
     *
     * <p>Used for PostgreSQL data types {@code date}.
     *
     * @param columnLabel the column name
     * @return the column value; if the value is SQL <code>NULL</code>, the
     * value returned is <code>null</code>
     * @exception DatabaseAccessorException if the columnName is not found
     */
    LocalDate getDate(String columnLabel);

    /**
     * Retrieves the value of the designated column in the current row
     * of this {@link Tuple} object as
     * a {@link LocalTime} in the Java programming language.
     *
     * <p>Used for PostgreSQL data types {@code time without time zone}.
     *
     * @param columnLabel the column name
     * @return the column value; if the value is SQL <code>NULL</code>, the
     * value returned is <code>null</code>
     * @exception DatabaseAccessorException if the columnName is not found
     */
    LocalTime getTime(String columnLabel);

    /**
     * Retrieves the value of the designated column in the current row
     * of this {@link Tuple} object as
     * a {@link LocalDateTime} in the Java programming language.
     *
     * <p>Used for PostgreSQL data types {@code timestamp without time zone}.
     *
     * @param columnLabel the column name
     * @return the column value; if the value is SQL <code>NULL</code>, the
     * value returned is <code>null</code>
     * @exception DatabaseAccessorException if the columnName is not found
     */
    LocalDateTime getDateTime(String columnLabel);

    /**
     * Retrieves the value of the designated column in the current row
     * of this {@link Tuple} object as
     * an {@link OffsetDateTime} in the Java programming language.
     *
     * <p>Used for PostgreSQL data types {@code timestamp with time zone}.
     *
     * @param columnLabel the column name
     * @return the column value; if the value is SQL <code>NULL</code>, the
     * value returned is <code>null</code>
     * @exception DatabaseAccessorException if the columnName is not found
     */
    OffsetDateTime getTimestamp(String columnLabel);

    /**
     * Retrieves the value of the designated column in the current row
     * of this {@link Tuple} object as
     * a {@link UUID} in the Java programming language.
     *
     * @param columnLabel the column name
     * @return the column value; if the value is SQL <code>NULL</code>, the
     * value returned is <code>null</code>
     * @exception DatabaseAccessorException if the columnName is not found
     */
    UUID getUUID(String columnLabel);

    /**
     * Retrieves the value of the designated column in the current row
     * of this {@link Tuple} object as the best {@code Object} type.
     *
     * @param columnLabel the column name
     * @return the column value; if the value is SQL <code>NULL</code>, the
     * value returned is <code>null</code>
     * @exception DatabaseAccessorException if the columnName is not found
     */
    Object getObject(String columnLabel);

    /**
     * Retrieves the value of the designated column in the current row
     * of this {@link Tuple} object as an object of given type (with
     * automatic conversion).
     *
     * @param columnLabel the column name
     * @param valueClass required type of retrieved value
     * @return the column value; if the value is SQL <code>NULL</code>, the
     * value returned is <code>null</code>
     * @exception DatabaseAccessorException if the columnName is not found
     */
    <T> T getObject(String columnLabel, Class<T> valueClass);

    <I, T> T map(String columnLabel, Class<I> requiredType, Function<I, T> mapper);
    <T> T mapString(String columnLabel, Function<String, T> mapper);
    <T extends Enum<T>> T getEnum(String columnLabel, Class<T> enumClass);

    List<String> getStrings(String columnLabel);
    byte[] getBytes(String columnLabel);
    <T> List<T> getObjects(String columnLabel, Class<T> elementClass);

}
