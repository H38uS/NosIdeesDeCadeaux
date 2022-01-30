package com.mosioj.ideescadeaux.core.model.entities;

import org.junit.Test;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.Assert.assertEquals;

public class UserTest {

    @Test
    public void testNbDayBeforeBirthday() {

        // Some dates
        final LocalDate _4mai2020 = LocalDate.of(2020, 5, 4);
        final LocalDate _10mai2020 = LocalDate.of(2020, 5, 10);
        final LocalDate _31decembre2020 = LocalDate.of(2020, 12, 31);
        final LocalDate _1janvier2021 = LocalDate.of(2021, 1, 1);
        final LocalDate _4mai1989 = LocalDate.of(1989, 5, 4);

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