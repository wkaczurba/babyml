package com.example.android.babyml.utils;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;

/**
 * Created by WKaczurb on 8/12/2017.
 */

public class DateUtils {
    /**
     * Function gets current date time and applies particular time to it.
     * If hour/min are ahead of current time - a day earlier is returned.
     *
     * TODO: Move this to Utils class.
     *
     * @return
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
