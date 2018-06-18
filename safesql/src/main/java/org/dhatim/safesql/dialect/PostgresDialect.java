package org.dhatim.safesql.dialect;

import java.math.BigDecimal;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.UUID;
import java.util.regex.Pattern;
import org.dhatim.safesql.Dialect;
import org.dhatim.safesql.SafeSqlBuilder;
import org.dhatim.safesql.SafeSqlLiteralizable;

public class PostgresDialect extends Dialect {

    private static final char STRING_QUOTE_CHAR = '\'';
    private static final String STRING_QUOTE = "'";
    private static final String ESCAPED_STRING_QUOTE = "''";

    private static final char IDENTIFIER_QUOTE_CHAR = '"';
    private static final String IDENTIFIER_QUOTE = "\"";
    private static final String ESCAPED_IDENTIFIER_QUOTE = "\"\"";

    private final DateTimeFormatter timestampWithTimeZoneFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSX");
    private final DateFormat timeWithTimeZoneFormat = new SimpleDateFormat("hh:mm:ss:SSSXXX");
    private final DateFormat timestampWithTimeZoneFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss:SSSXXX");

    private final Pattern defaultLikeValueEscape = Pattern.compile("([\\_%])");

    private final char[] hex = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    @Override
    public StringBuilder escapeStringLiteral(StringBuilder builder, String s) {
        return builder.append(STRING_QUOTE_CHAR).append(s.replace(STRING_QUOTE, ESCAPED_STRING_QUOTE)).append(STRING_QUOTE_CHAR);
    }

    @Override
    public StringBuilder escapeBytesLiteral(StringBuilder builder, byte[] bytes) {
        builder.append("'\\x");
        for (byte b : bytes) {
            builder.append(hex[(b >> 4) & 0xF]);
            builder.append(hex[(b & 0xF)]);
        }
        return builder.append('\'');
    }

    @Override
    public StringBuilder escapeIdentifier(StringBuilder builder, String identifier) {
        if (needEscapeIdentifier(identifier)) {
            builder.append(IDENTIFIER_QUOTE_CHAR).append(identifier.replace(IDENTIFIER_QUOTE, ESCAPED_IDENTIFIER_QUOTE)).append(IDENTIFIER_QUOTE_CHAR);
        } else {
            builder.append(identifier);
        }
        return builder;
    }

    static boolean needEscapeIdentifier(String identifier) {
        Objects.requireNonNull(identifier, "null identifier");
        for (int i=0; i<identifier.length(); i++) {
            char ch = identifier.charAt(i);
            if (i == 0) {
                if (!(Character.isLetter(ch) || ch == '_')) {
                    return true;
                }
            } else {
                if (!(Character.isLetterOrDigit(ch) || ch == '_' || ch == '$')) {
                    return true;
                }
            }
            if (Character.isLetter(ch) && !Character.isLowerCase(ch)) {
                return true;
            }
        }
        return false;
    }

    protected void appendLiteralizedParameter(SafeSqlBuilder sb, Object obj) {
        if (obj == null) {
            sb.append("NULL");
        } else if (obj instanceof Boolean) {
            sb.append((Boolean) obj ? "TRUE" : "FALSE");
        } else if (obj instanceof BigDecimal) {
            sb.append(obj.toString()).append("::numeric");
        } else if (obj instanceof Number) {
            sb.append(((Number) obj).toString());
        } else if (obj instanceof Timestamp) {
            sb.append("TIMESTAMP WITH TIME ZONE ").append(STRING_QUOTE);
            sb.append(timestampWithTimeZoneFormat.format((Timestamp) obj));
            sb.append(STRING_QUOTE);
        } else if (obj instanceof Time) {
            sb.append("TIME WITH TIME ZONE ").append(STRING_QUOTE);
            sb.append(timeWithTimeZoneFormat.format((Time) obj));
            sb.append(STRING_QUOTE);
        } else if (obj instanceof java.sql.Date) {
            sb.append("DATE ").append(STRING_QUOTE);
            sb.append(((java.sql.Date) obj).toLocalDate().toString());
            sb.append(STRING_QUOTE);
        } else if (obj instanceof LocalDate) {
            sb.append("DATE ").append(STRING_QUOTE);
            sb.append(((LocalDate) obj).toString());
            sb.append(STRING_QUOTE);
        } else if (obj instanceof LocalTime) {
            sb.append("TIME ").append(STRING_QUOTE);
            sb.append(((LocalTime) obj).toString());
            sb.append(STRING_QUOTE);
        } else if (obj instanceof LocalDateTime) {
            sb.append("TIMESTAMP ").append(STRING_QUOTE);
            LocalDateTime other = (LocalDateTime) obj;
            sb.append(other.toLocalDate().toString()).append(' ').append(other.toLocalTime().toString());
            sb.append(STRING_QUOTE);
        } else if (obj instanceof OffsetDateTime) {
            sb.append("TIMESTAMP WITH TIME ZONE ").append(STRING_QUOTE);
            sb.append(timestampWithTimeZoneFormatter.format((OffsetDateTime) obj));
            sb.append(STRING_QUOTE);
        } else if (obj instanceof UUID) {
            sb.append("UUID ").literal(obj.toString());
        } else if (obj instanceof SafeSqlLiteralizable) {
            ((SafeSqlLiteralizable) obj).appendLiteralized(sb);
        } else if (obj instanceof byte[]) {
            sb.append("BYTEA ").literal((byte[]) obj);
        } else {
            sb.literal(obj.toString());
        }
    }

    @Override
    public String escapeLikeValue(String s, char escapeChar) {
        return Pattern.compile("([" + escapeChar + "_%])").matcher(s).replaceAll(escapeChar + "$1");
    }

    @Override
    public String escapeLikeValue(String s) {
        return defaultLikeValueEscape.matcher(s).replaceAll("\\\\$1");
    }

}
