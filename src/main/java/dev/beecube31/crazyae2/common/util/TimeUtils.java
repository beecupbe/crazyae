package dev.beecube31.crazyae2.common.util;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class TimeUtils {
    public static String formatTimeForZone(long timestampMillis, ZoneId zoneId) {
        Instant instant = Instant.ofEpochMilli(timestampMillis);
        ZonedDateTime zonedDateTime = instant.atZone(zoneId);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        return zonedDateTime.format(formatter);
    }
}
