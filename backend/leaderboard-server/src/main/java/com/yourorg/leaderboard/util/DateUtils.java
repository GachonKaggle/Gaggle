package com.yourorg.leaderboard.util;

import java.time.*;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;

public class DateUtils {
    public static String getDaysAgoString(String isoDateString) {
        if (isoDateString == null || isoDateString.isBlank()) {
            return "";
        }
        try {
            Instant compareInstant = Instant.parse(isoDateString);
            ZonedDateTime now = ZonedDateTime.now();
            ZonedDateTime compareTime = compareInstant.atZone(ZoneId.systemDefault());

            long months = ChronoUnit.MONTHS.between(compareTime.withDayOfMonth(1), now.withDayOfMonth(1));
            long days = ChronoUnit.DAYS.between(compareTime.toLocalDate(), now.toLocalDate());
            long hours = ChronoUnit.HOURS.between(compareTime, now);
            long minutes = ChronoUnit.MINUTES.between(compareTime, now);

            if (months > 0) {
                return months + "달 전";
            } else if (days > 0) {
                return days + "일 전";
            } else if (hours > 0) {
                return hours + "시간 전";
            } else if (minutes > 0) {
                return minutes + "분 전";
            } else {
                return "방금 전";
            }
        } catch (DateTimeParseException e) {
            return "";
        }
    }
}
