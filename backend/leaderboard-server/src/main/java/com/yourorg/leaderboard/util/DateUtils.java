package com.yourorg.leaderboard.util; // Package for utility classes

import java.time.*; // Import classes for date and time
import java.time.format.DateTimeParseException; // Import exception for date parsing errors
import java.time.temporal.ChronoUnit; // Import time unit enumeration

public class DateUtils { // Utility class for date-related operations

    // Returns a human-readable "time ago" string for a given ISO date string
    public static String getDaysAgoString(String isoDateString) {
        // Return empty string if input is null or blank
        if (isoDateString == null || isoDateString.isBlank()) {
            return "";
        }

        try {
            // Parse the ISO date string into an Instant
            Instant compareInstant = Instant.parse(isoDateString);

            // Get current time with timezone
            ZonedDateTime now = ZonedDateTime.now();

            // Convert Instant to ZonedDateTime using system default timezone
            ZonedDateTime compareTime = compareInstant.atZone(ZoneId.systemDefault());

            // Calculate difference in months between current time and input time
            long months = ChronoUnit.MONTHS.between(compareTime.withDayOfMonth(1), now.withDayOfMonth(1));

            // Calculate difference in days
            long days = ChronoUnit.DAYS.between(compareTime.toLocalDate(), now.toLocalDate());

            // Calculate difference in hours
            long hours = ChronoUnit.HOURS.between(compareTime, now);

            // Calculate difference in minutes
            long minutes = ChronoUnit.MINUTES.between(compareTime, now);

            // Return formatted string based on the largest available time unit
            if (months > 0) {
                return months + "달 전"; // "X months ago"
            } else if (days > 0) {
                return days + "일 전"; // "X days ago"
            } else if (hours > 0) {
                return hours + "시간 전"; // "X hours ago"
            } else if (minutes > 0) {
                return minutes + "분 전"; // "X minutes ago"
            } else {
                return "방금 전"; // "Just now"
            }
        } catch (DateTimeParseException e) {
            // If parsing fails, return empty string
            return "";
        }
    }
}
