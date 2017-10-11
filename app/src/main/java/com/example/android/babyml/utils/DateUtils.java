package com.example.android.babyml.utils;

import com.example.android.babyml.data.Summarizable;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;

import java.util.TimeZone;

public class DateUtils {
    /**
     * Function gets current date time and applies particular time to it.
     * If hour/min are ahead of current time - a day earlier is returned.
     *
     * @return LocalDateTime
     */
    public static LocalDateTime applyTimeToCurrentLocalDate(int hour, int min) {
        LocalDateTime ldtNow = LocalDateTime.now();
        LocalTime ltNow = ldtNow.toLocalTime();

        LocalDate ld; // date to be used (either today or yesterday)
        LocalTime lt = new LocalTime(hour, min, 0); // time to be used

        if (lt.isAfter(ltNow)) {
            // Use yesterday's date as hour/time specifies time before midnight.
            ld = ldtNow.toLocalDate().minusDays(1);
        } else {
            // Use yesterday's date as hour/time specifies today's time during.
            ld = ldtNow.toLocalDate();
        }

        return new LocalDateTime(
                ld.getYear(),
                ld.getMonthOfYear(),
                ld.getDayOfMonth(),
                lt.getHourOfDay(),
                lt.getMinuteOfHour());
    }


    public static LocalDateTime applyTimeToNextAfterLocalDateTime(LocalDateTime preceedingLdt, int hour, int min) {
        LocalTime timeBefore = preceedingLdt.toLocalTime();
        LocalTime newLt = new LocalTime(hour, min);
        LocalDateTime newLdt;

        int difference = newLt.getMillisOfDay() - timeBefore.getMillisOfDay();
        if (difference >= 0) {
            newLdt = preceedingLdt.plusMillis(difference);
        } else {
            newLdt = preceedingLdt.plusMillis(difference).plusDays(1);
        }
        return newLdt;
    }

//    public static LocalDateTime applyTimeToNextAfterTimeMillis(long preecedingMillisTime, int hour, int min) {
//        LocalTime timeBefore = preceedingLdt.toLocalTime();
//        LocalTime newLt = new LocalTime(hour, min);
//
//        int absDifference = Math.abs(newLt.getMillisOfDay() - timeBefore.getMillisOfDay());
//        LocalDateTime newLdt = preceedingLdt.plusMillis(absDifference);
//        return newLdt;
//    }

    public static LocalDate summarizableToLocalDate(Summarizable s) {
        return milisToLocalDate( s.getTs() );
    }

    private static LocalDate milisToLocalDate(long millis) {
        DateTime dt = new DateTime(millis, DateTimeZone.forTimeZone(TimeZone.getDefault()));
        return dt.toLocalDate();
    }

    public static int[] toHoursMinArray(String s) {
        String[] data = s.split(":");
        int hours, min;

        try {
            hours = Integer.parseInt(data[0]);
            min = Integer.parseInt(data[1]);
            if (data.length != 2 || hours < 0 || hours > 23 || min < 0 || min > 59) {
                return null;
            }
        } catch (Exception e) { // Either NumberFormatException or NullPointerException
            return null;
        }
        return new int[]{ hours, min };
    }

}
