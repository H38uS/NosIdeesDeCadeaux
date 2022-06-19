package com.mosioj.ideescadeaux.core.model.entities;

import com.google.gson.annotations.Expose;
import com.mosioj.ideescadeaux.core.model.repositories.SousReservationRepository;
import com.mosioj.ideescadeaux.core.utils.date.MyDateFormatViewer;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class BookingInformation {

    // FIXME : en faire une table et mettre une colonne dans IDEES

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
    private BookingInformation(BookingType type, User bookingOwner, IdeaGroup group, LocalDateTime bookedOn) {
        this.type = type;
        this.bookingOwner = bookingOwner;
        this.group = group;
        bookingReadableDate = MyDateFormatViewer.formatMine(bookedOn);
    }

    /**
     * @param ideaId The idea identifier.
     * @return All people that have booked this idea. Can be by direct booking, by a group, or by a partial booking.
     */
    public List<User> getBookers(int ideaId) {
        if (type == BookingType.SINGLE_PERSON) {
            return getBookingOwner().isPresent() ? Collections.singletonList(getBookingOwner().get()) : Collections.emptyList();
        }
        if (type == BookingType.GROUP) {
            final Set<IdeaGroupContent> ideaGroupContents = getBookingGroup().map(IdeaGroup::getShares)
                                                                             .orElse(Collections.emptySet());
            return ideaGroupContents.stream().map(IdeaGroupContent::getUser).collect(Collectors.toList());
        }
        if (type == BookingType.PARTIAL) {
            return SousReservationRepository.getSousReservation(ideaId)
                                            .stream()
                                            .map(SousReservationEntity::getUser)
                                            .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    /**
     * @return True if and only if this idea is booked somehow.
     */
    public boolean isBooked() {
        return type != BookingType.NONE;
    }

    /**
     * @return The booking type.
     */
    public BookingType getBookingType() {
        return type;
    }

    /**
     * @return The booking owner if any.
     */
    public Optional<User> getBookingOwner() {
        return Optional.ofNullable(bookingOwner);
    }

    /**
     * @return The booking group if any.
     */
    public Optional<IdeaGroup> getBookingGroup() {
        return Optional.ofNullable(group);
    }

    public static BookingInformation fromAllPossibilities(User bookedBy,
                                                          IdeaGroup group,
                                                          String isSubBooked,
                                                          LocalDateTime bookedOn) {
        if (bookedBy == null) {
            if (group == null) {
                if ("Y".equals(isSubBooked)) {
                    return BookingInformation.fromAPartialReservation(bookedOn);
                } else {
                    return BookingInformation.noBooking();
                }
            } else {
                return BookingInformation.fromAGroup(group, bookedOn);
            }
        } else {
            return BookingInformation.fromASingleUser(bookedBy, bookedOn);
        }
    }

    public static BookingInformation fromAGroup(IdeaGroup group, LocalDateTime bookedOn) {
        return new BookingInformation(BookingType.GROUP, null, group, bookedOn);
    }

    public static BookingInformation fromASingleUser(User bookingOwner, LocalDateTime bookedOn) {
        return new BookingInformation(BookingType.SINGLE_PERSON, bookingOwner, null, bookedOn);
    }

    public static BookingInformation fromAPartialReservation(LocalDateTime bookedOn) {
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
