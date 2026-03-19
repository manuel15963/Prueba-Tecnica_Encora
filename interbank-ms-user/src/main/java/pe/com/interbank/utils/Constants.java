package pe.com.interbank.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Constants {

    public static final String TIME_ZONE_UTC = "UTC";
    public static final String TIME_ZONE_LIMA = "America/Lima";
    public static final String DATE_FORMAT_TIME_ZONE = "yyyy-MM-dd HH:mm:ss";
    public static final String BEARER = "Bearer ";
    public static final String ROLE_ADMIN = "ROLE_ADMIN";
    public static final String ROLE_USER = "ROLE_USER";
    public static final Boolean ENABLED= true;
    public static final String ERROR_SAVING= "Error saving {}: {}";
    public static final String ERROR_DELETED= "Error deleted {}: {}";
    public static final String ACCOUNT = "account";
    public static final String USER = "user";
    public static final String BALANCE = "balance";
    public static final String TRANSFER = "transfer";
    public static final String CORRELATION_ID_HEADER = "X-Correlation-Id";
    public static final String CORRELATION_ID_ATTR = "correlationId";
    public static final String REQUEST_START_TIME_ATTR = "requestStartTime";
    public static final String REQUEST_ERROR_ATTR = "requestError";
    public static final String MASK_CHAR = "*";
    public static final String MASK_HIDDEN = "******";
    public static final int MASK_VISIBLE_LAST_3 = 3;
    public static final int MASK_VISIBLE_LAST_4 = 4;

    public static LocalDateTime convertToLocalTimeZone(LocalDateTime date) {
        if (date != null) {
            return date.atZone(ZoneId.of(Constants.TIME_ZONE_UTC))
                    .withZoneSameInstant(ZoneId.of(Constants.TIME_ZONE_LIMA))
                    .toLocalDateTime();
        }
        return null;
    }

    public static String convertLocalDateTimeToString(LocalDateTime date) {
        if (date != null) {
            LocalDateTime limaTime = date.atZone(ZoneId.of(Constants.TIME_ZONE_UTC))
                    .withZoneSameInstant(ZoneId.of(Constants.TIME_ZONE_LIMA))
                    .toLocalDateTime();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Constants.DATE_FORMAT_TIME_ZONE);
            return limaTime.format(formatter);
        }
        return null;
    }
}
