package com.mosioj.ideescadeaux.core.model.entities;

import com.google.gson.annotations.Expose;
import com.mosioj.ideescadeaux.core.utils.date.MyDateFormatViewer;

import java.sql.Timestamp;
import java.util.Optional;

public class BookingInformation {

    /** The booking type. */
    @Expose
    protected final BookingType type;

    /** The booking group. Not null if and only if the idea is booked by a group. */
    @Expose
    protected final IdeaGroup group;

    /** The booking owner. Not null if and only if only a single person has booked the entire idea. */
    @Expose
    protected final User bookingOwner;

    /** When this idea has been booked or an empty string. */
    @Expose
    protected final String bookingReadableDate;

    /**
     * Class contructor.
     *
     * @param type         The booking type.
     * @param bookingOwner The booking owner. Not null if and only if only a single person has booked the entire idea.
     * @param group        The booking group. Not null if and only if the idea is booked by a group.
     * @param bookedOn     When the idea has been booked (can be null).
     */
    private BookingInformation(BookingType type, User bookingOwner, IdeaGroup group, Timestamp bookedOn) {
        this.type = type;
        this.bookingOwner = bookingOwner;
        this.group = group;
        bookingReadableDate = MyDateFormatViewer.formatMine(bookedOn);
    }

    /**
     * @return The booking type.
     */
    public BookingType getBookingType() {
        return type;
    }

    /**
     *
     * @return The booking owner if any.
     */
    public Optional<User> getBookingOwner() {
        return Optional.ofNullable(bookingOwner);
    }

    /**
     *
     * @return The booking group if any.
     */
    public Optional<IdeaGroup> getBookingGroup() {
        return Optional.ofNullable(group);
    }

    public static BookingInformation fromAGroup(IdeaGroup group, Timestamp bookedOn) {
        return new BookingInformation(BookingType.GROUP, null, group, bookedOn);
    }

    public static BookingInformation fromASingleUser(User bookingOwner, Timestamp bookedOn) {
        return new BookingInformation(BookingType.SINGLE_PERSON, bookingOwner, null, bookedOn);
    }

    public static BookingInformation fromAPartialReservation(Timestamp bookedOn) {
        return new BookingInformation(BookingType.PARTIAL, null, null, bookedOn);
    }

    public static BookingInformation noBooking() {
        return new BookingInformation(BookingType.NONE, null, null, null);
    }

    public enum BookingType {

        /** Booked entirely by a single person */
        SINGLE_PERSON,

        /** Booked entirely by a group (at least one person) */
        GROUP,

        /** Subpart(s) booked by at least one person */
        PARTIAL,

        /** Not booked - this idea is free! */
        NONE
    }
}
