package com.mosioj.ideescadeaux.core.utils.date;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;
import java.util.Optional;
import java.util.TimeZone;

public class MyDateFormatViewer extends SimpleDateFormat {

    /** Class logger. */
    private static final Logger logger = LogManager.getLogger(MyDateFormatViewer.class);

    /** Pattern to parse a news date. */
    public static final String DATE_FORMAT = "yyyy-MM-dd";

    /** Date Formatter for a timestamp */
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("d MMMM yyyy à HH'h'mm")
                                                                                  .withLocale(Locale.FRENCH);

    /** Date Formatter for a day */
    private static final DateTimeFormatter DAY_FORMATTER = DateTimeFormatter.ofPattern("d MMMM")
                                                                            .withLocale(Locale.FRENCH);

    /**
     * Format the given date using ${DATETIME_DISPLAY_FORMAT}.
     *
     * @param localDateTime The date to format.
     * @return The formatted date or an empty string if the date is null.
     */
    public static String formatMine(LocalDateTime localDateTime) {
        return formatOrElse(localDateTime, StringUtils.EMPTY);
    }

    /**
     * Format the given date.
     *
     * @param date The local date to format.
     * @return The formatted date.
     */
    public static String formatDayWithYearHidden(LocalDate date) {
        return date.format(DAY_FORMATTER);
    }

    /**
     * Format the given date using ${DATETIME_DISPLAY_FORMAT} if not null or return the provided default value.
     *
     * @param time          The local date time to format.
     * @param defaultFormat The default string to return if the give date is null.
     * @return The formatted date.
     */
    public static String formatOrElse(LocalDateTime time, String defaultFormat) {
        if (time == null) {
            return defaultFormat;
        }
        return time.format(DATE_TIME_FORMATTER);
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

    /**
     * @param date The date string to parse.
     * @return The corresponding date if it succeeds to parse it.
     */
    public static Optional<LocalDate> getAsDate(String date) {
        try {
            return Optional.of(LocalDate.parse(date, DateTimeFormatter.ISO_LOCAL_DATE)); // yyyy-MM-dd
        } catch (NullPointerException | DateTimeParseException e) {
            return Optional.empty();
        }
    }
}
