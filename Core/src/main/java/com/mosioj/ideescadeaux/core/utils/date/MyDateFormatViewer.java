package com.mosioj.ideescadeaux.core.utils.date;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class MyDateFormatViewer extends SimpleDateFormat {

    private static final long serialVersionUID = 3200032903715571847L;
    private static final Logger logger = LogManager.getLogger(MyDateFormatViewer.class);

    private static final String DATETIME_DISPLAY_FORMAT = "d MMMM yyyy à HH'h'mm";
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    private static final String DISPLAY_DATE_FORMAT = "d MMMM";

    /** Date Formatter */
    private static final SimpleDateFormat MODIFICATION_DATE_FORMAT = new MyDateFormatViewer(DATETIME_DISPLAY_FORMAT);
    private static final SimpleDateFormat DAY_FORMATER = new MyDateFormatViewer(DISPLAY_DATE_FORMAT);

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
    public static String formatDayWithYearHidden(long time) {
        return DAY_FORMATER.format(new Date(time));
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
        return MODIFICATION_DATE_FORMAT.format(date);
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
