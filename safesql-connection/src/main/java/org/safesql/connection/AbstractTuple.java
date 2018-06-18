package org.safesql.connection;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

public class AbstractTuple implements Tuple {

    @Override
    public boolean contains(String columnLabel) {
        return false;
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public <T> T get(String columnLabel) {
        return null;
    }

    @Override
    public String getString(String columnLabel) {
        return null;
    }

    @Override
    public Boolean getBoolean(String columnLabel) {
        return null;
    }

    @Override
    public Integer getInt(String columnLabel) {
        return null;
    }

    @Override
    public Long getLong(String columnLabel) {
        return null;
    }

    @Override
    public Float getFloat(String columnLabel) {
        return null;
    }

    @Override
    public Double getDouble(String columnLabel) {
        return null;
    }

    @Override
    public BigDecimal getBigDecimal(String columnLabel) {
        return null;
    }

    @Override
    public LocalDate getDate(String columnLabel) {
        return null;
    }

    @Override
    public LocalTime getTime(String columnLabel) {
        return null;
    }

    @Override
    public LocalDateTime getDateTime(String columnLabel) {
        return null;
    }

    @Override
    public OffsetDateTime getTimestamp(String columnLabel) {
        return null;
    }

    @Override
    public UUID getUUID(String columnLabel) {
        return null;
    }

    @Override
    public Object getObject(String columnLabel) {
        return null;
    }

    @Override
    public <T> T getObject(String columnLabel, Class<T> valueClass) {
        return null;
    }

    @Override
    public <I, T> T map(String columnLabel, Class<I> requiredType, Function<I, T> mapper) {
        return null;
    }

    @Override
    public <T> T mapString(String columnLabel, Function<String, T> mapper) {
        return map(columnLabel, String.class, mapper);
    }

    @Override
    public <T extends Enum<T>> T getEnum(String columnLabel, Class<T> enumClass) {
        return map(columnLabel, String.class, value -> Enum.valueOf(enumClass, value));
    }

    @Override
    public List<String> getStrings(String columnLabel) {
        return null;
    }

    @Override
    public byte[] getBytes(String columnLabel) {
        return new byte[0];
    }

    @Override
    public <T> List<T> getObjects(String columnLabel, Class<T> elementClass) {
        return null;
    }
}
