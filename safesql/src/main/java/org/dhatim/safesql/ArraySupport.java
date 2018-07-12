package org.dhatim.safesql;

import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.UUID;

final class ArraySupport {

    private static final char VALUE_QUOTE = '"';
    private static final char[] HEX_CODE = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    private ArraySupport() {
    }

    public static String toString(Iterable<?> iterable) {
        StringBuilder sb = new StringBuilder().append('{');
        boolean first = true;
        for (Object element : iterable) {
            if (first) {
                first = false;
            } else {
                sb.append(',');
            }
            appendElement(sb, element);
        }
        return sb.append('}').toString();
    }

    public static String toString(Object... elements) {
        StringBuilder sb = new StringBuilder().append('{');
        for (int i = 0; i < elements.length; ++i) {
            if (i > 0) {
                sb.append(',');
            }
            appendElement(sb, elements[i]);
        }
        return sb.append('}').toString();
    }

    public static void appendHexBytes(StringBuilder sb, byte[] bytes) {
        for (byte b : bytes) {
            sb.append(HEX_CODE[(b >> 4) & 0xF]);
            sb.append(HEX_CODE[(b & 0xF)]);
        }
    }

    private static void appendElement(StringBuilder sb, Object obj) {
        if (obj == null) {
            sb.append("NULL");
        } else if (obj instanceof Boolean) {
            sb.append((Boolean) obj ? "TRUE" : "FALSE");
        } else if (obj instanceof Number) {
            sb.append(obj);
        } else if (obj instanceof Timestamp) {
            sb.append(VALUE_QUOTE).append(SafeSqlUtils.TIMESTAMP_FORMAT_WITH_TZ.format((Timestamp) obj)).append(VALUE_QUOTE);
        } else if (obj instanceof Time) {
            sb.append(VALUE_QUOTE).append(SafeSqlUtils.TIME_FORMAT_WITH_TZ.format((Time) obj)).append(VALUE_QUOTE);
        } else if (obj instanceof java.sql.Date) {
            sb.append(VALUE_QUOTE).append(((java.sql.Date) obj).toLocalDate()).append(VALUE_QUOTE);
        } else if (obj instanceof LocalDate) {
            sb.append(VALUE_QUOTE).append(obj).append(VALUE_QUOTE);
        } else if (obj instanceof LocalTime) {
            sb.append(VALUE_QUOTE).append(obj).append(VALUE_QUOTE);
        } else if (obj instanceof LocalDateTime) {
            sb.append(VALUE_QUOTE).append(obj).append(VALUE_QUOTE);
        } else if (obj instanceof OffsetDateTime) {
            sb.append(VALUE_QUOTE).append(SafeSqlUtils.TIMESTAMP_FORMATTER_WITH_TZ.format((OffsetDateTime) obj)).append(VALUE_QUOTE);
        } else if (obj instanceof UUID) {
            sb.append(VALUE_QUOTE).append(obj).append(VALUE_QUOTE);
        } else if (obj instanceof byte[]) {
            sb.append(VALUE_QUOTE).append("\\x");
            appendHexBytes(sb, (byte[]) obj);
            sb.append(VALUE_QUOTE);
        } else {
            sb.append(VALUE_QUOTE).append(obj).append(VALUE_QUOTE);
        }
    }

}
