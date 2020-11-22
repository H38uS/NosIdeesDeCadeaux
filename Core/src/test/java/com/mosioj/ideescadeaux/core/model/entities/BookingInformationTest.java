package com.mosioj.ideescadeaux.core.model.entities;

import com.mosioj.ideescadeaux.core.TemplateTest;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;

import java.sql.Timestamp;
import java.time.Instant;

public class BookingInformationTest extends TemplateTest {

    private static final Instant MODIF_DATE = Timestamp.valueOf("2007-09-23 10:10:10.0").toInstant();
    private static final String EXPECTED_DATE_STRING = "23 septembre 2007 à 10h10";

    @Test
    public void noneTypeShouldBeCorrectlyInitialized() {
        final BookingInformation noBooking = BookingInformation.noBooking();
        Assert.assertEquals(BookingInformation.BookingType.NONE, noBooking.type);
        Assert.assertNull(noBooking.bookingOwner);
        Assert.assertNull(noBooking.group);
        Assert.assertEquals(StringUtils.EMPTY, noBooking.bookingReadableDate);
    }

    @Test
    public void groupTypeShouldBeCorrectlyInitialized() {
        IdeaGroup group = new IdeaGroup(53, 200);
        final BookingInformation groupBooking = BookingInformation.fromAGroup(group, MODIF_DATE);
        Assert.assertEquals(BookingInformation.BookingType.GROUP, groupBooking.type);
        Assert.assertNull(groupBooking.bookingOwner);
        Assert.assertEquals(group, groupBooking.group);
        Assert.assertEquals(EXPECTED_DATE_STRING, groupBooking.bookingReadableDate);
    }

    @Test
    public void partialTypeShouldBeCorrectlyInitialized() {
        final BookingInformation partialBooking = BookingInformation.fromAPartialReservation(MODIF_DATE);
        Assert.assertEquals(BookingInformation.BookingType.PARTIAL, partialBooking.type);
        Assert.assertNull(partialBooking.bookingOwner);
        Assert.assertNull(partialBooking.group);
        Assert.assertEquals(EXPECTED_DATE_STRING, partialBooking.bookingReadableDate);
    }

    @Test
    public void singlePersonTypeShouldBeCorrectlyInitialized() {
        final User aUser = firefox;
        final BookingInformation singlePerson = BookingInformation.fromASingleUser(aUser, MODIF_DATE);
        Assert.assertEquals(BookingInformation.BookingType.SINGLE_PERSON, singlePerson.type);
        Assert.assertEquals(aUser, singlePerson.bookingOwner);
        Assert.assertNull(singlePerson.group);
        Assert.assertEquals(EXPECTED_DATE_STRING, singlePerson.bookingReadableDate);
    }
}