package com.mosioj.ideescadeaux.core.model.entities;

import com.mosioj.ideescadeaux.core.utils.date.MyDateFormatViewer;
import org.junit.Test;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.Assert.assertEquals;

public class UserTest {

    /** Date formatter. */
    private static final SimpleDateFormat SDF = new MyDateFormatViewer(MyDateFormatViewer.DATE_FORMAT);

    private static Date toDate(String date) throws ParseException {
        return new Date(SDF.parse(date).getTime());
    }

    @Test
    public void testNbDayBeforeBirthday() throws ParseException {

        // Some dates
        final Date _4mai2020 = toDate("2020-05-04");
        final Date _10mai2020 = toDate("2020-05-10");
        final Date _31decembre2020 = toDate("2020-12-31");
        final Date _1janvier2021 = toDate("2021-01-01");
        final Date _4mai1989 = toDate("1989-05-04");

        // Giving null to an argument returns an empty option
        assertEquals(Optional.empty(), User.getNbDayBeforeBirthday(null, null));
        assertEquals(Optional.empty(), User.getNbDayBeforeBirthday(LocalDate.now(), null));
        assertEquals(Optional.empty(), User.getNbDayBeforeBirthday(null, _4mai2020));

        // Testing the diff
        assertEquals(Optional.of(0L), User.getNbDayBeforeBirthday(LocalDate.of(2020, 5, 4), _4mai2020));
        assertEquals(Optional.of(6L), User.getNbDayBeforeBirthday(LocalDate.of(2020, 5, 4), _10mai2020));
        assertEquals(Optional.of(1L), User.getNbDayBeforeBirthday(LocalDate.of(2020, 12, 30), _31decembre2020));
        assertEquals(Optional.of(2L), User.getNbDayBeforeBirthday(LocalDate.of(2020, 12, 30), _1janvier2021));
        assertEquals(Optional.of(0L), User.getNbDayBeforeBirthday(LocalDate.of(2020, 5, 4), _4mai1989));
        assertEquals(Optional.of(9L), User.getNbDayBeforeBirthday(LocalDate.of(2020, 4, 25), _4mai1989));
        assertEquals(Optional.of(364L), User.getNbDayBeforeBirthday(LocalDate.of(2020, 5, 5), _4mai1989));
    }

}