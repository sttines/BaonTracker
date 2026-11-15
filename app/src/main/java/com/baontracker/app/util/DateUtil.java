package com.baontracker.app.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public final class DateUtil {

    public static final String[] DAYS =
            {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};

    private static final long ONE_DAY = 24L * 60 * 60 * 1000;

    private DateUtil() {
    }

    private static int toCalendarDay(String day) {
        switch (day) {
            case "Tuesday": return Calendar.TUESDAY;
            case "Wednesday": return Calendar.WEDNESDAY;
            case "Thursday": return Calendar.THURSDAY;
            case "Friday": return Calendar.FRIDAY;
            case "Saturday": return Calendar.SATURDAY;
            case "Sunday": return Calendar.SUNDAY;
            default: return Calendar.MONDAY;
        }
    }

    public static long startOfDay(long millis) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(millis);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTimeInMillis();
    }

    public static long startOfToday() {
        return startOfDay(System.currentTimeMillis());
    }

    /** Midnight of the most recent allowance day, which is the start of this week. */
    public static long weekStart(String weekStartDay) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(startOfToday());
        int diff = (c.get(Calendar.DAY_OF_WEEK) - toCalendarDay(weekStartDay) + 7) % 7;
        c.add(Calendar.DAY_OF_MONTH, -diff);
        return c.getTimeInMillis();
    }

    /** Days left in the week, counting today. Always 1 to 7. */
    public static int daysLeftInWeek(String weekStartDay) {
        int passed = (int) Math.round((startOfToday() - weekStart(weekStartDay)) / (double) ONE_DAY);
        return Math.max(1, 7 - passed);
    }

    public static long addDays(long millis, int days) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(millis);
        c.add(Calendar.DAY_OF_MONTH, days);
        return c.getTimeInMillis();
    }

    /** "Today", "Yesterday", or a date like "Thu, Nov 13". */
    public static String dayLabel(long millis) {
        long day = startOfDay(millis);
        long today = startOfToday();
        if (day == today) return "Today";
        if (day == startOfDay(addDays(today, -1))) return "Yesterday";
        return new SimpleDateFormat("EEE, MMM d", Locale.US).format(new Date(millis));
    }

    public static String time(long millis) {
        return new SimpleDateFormat("h:mm a", Locale.US).format(new Date(millis));
    }

    public static String shortDate(long millis) {
        return new SimpleDateFormat("MMM d", Locale.US).format(new Date(millis));
    }

    /** Used in date fields, for example "Today, Nov 15" or "Thu, Nov 13, 2026". */
    public static String fieldDate(long millis) {
        String date = new SimpleDateFormat("MMM d, yyyy", Locale.US).format(new Date(millis));
        String label = dayLabel(millis);
        if (label.equals("Today") || label.equals("Yesterday")) return label + ", " + date;
        return new SimpleDateFormat("EEE, MMM d, yyyy", Locale.US).format(new Date(millis));
    }
}
