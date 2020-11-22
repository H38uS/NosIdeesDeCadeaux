package com.mosioj.ideescadeaux.core.utils.date;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class MyDateFormatViewer extends SimpleDateFormat {

    /** Class logger. */
    private static final Logger logger = LogManager.getLogger(MyDateFormatViewer.class);

    /** Pattern to parse a news date. */
    public static final String DATE_FORMAT = "yyyy-MM-dd";

    /** Date Formatter for a timestamp */
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("d MMMM yyyy à HH'h'mm");

    /** Date Formatter for a day */
    private static final DateTimeFormatter DAY_FORMATTER = DateTimeFormatter.ofPattern("d MMMM");

    /**
     * Format the given date using ${DATETIME_DISPLAY_FORMAT}.
     *
     * @param date The date to format.
     * @return The formatted date or an empty string if the date is null.
     */
    public static String formatMine(Date date) {
        return formatOrElse(date, StringUtils.EMPTY);
    }

    /**
     * Format the given date using ${DISPLAY_DATE_FORMAT}.
     *
     * @param time The number of milliseconds since the epoch.
     * @return The formatted date.
     */
    public static String formatDayWithYearHidden(Instant time) {
        return time.atZone(ZoneId.of("Europe/Paris")).toLocalDateTime().format(DAY_FORMATTER);
    }

    /**
     * Format the given date using ${DATETIME_DISPLAY_FORMAT} if not null or return the provided default value.
     *
     * @param date          The date to format.
     * @param defaultFormat The default string to return if the give date is null.
     * @return The formatted date.
     */
    public static String formatOrElse(Date date, String defaultFormat) {
        if (date == null) {
            return defaultFormat;
        }
        return date.toInstant().atZone(ZoneId.of("Europe/Paris")).toLocalDateTime().format(DATE_TIME_FORMATTER);
    }

    /**
     * Attention: n'utiliser que pour visualiser !!
     *
     * @param pattern The datetime pattern.
     */
    public MyDateFormatViewer(String pattern) {
        super(pattern, Locale.FRANCE);
        calendar.setTimeZone(TimeZone.getTimeZone("Europe/Paris"));
        logger.trace(calendar.getTimeZone());
        setTimeZone(calendar.getTimeZone());
    }
}
