package com.mosioj.ideescadeaux.webapp.servlets.service;

import com.mosioj.ideescadeaux.core.model.entities.BookingInformation;
import com.mosioj.ideescadeaux.core.model.entities.IdeaGroup;
import com.mosioj.ideescadeaux.core.model.entities.Idee;
import com.mosioj.ideescadeaux.core.model.entities.SousReservationEntity;
import com.mosioj.ideescadeaux.core.model.notifications.NType;
import com.mosioj.ideescadeaux.core.model.repositories.*;
import com.mosioj.ideescadeaux.webapp.servlets.AbstractTestServletWebApp;
import org.junit.Before;
import org.junit.Test;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

public class ServiceRestoreIdeaTest extends AbstractTestServletWebApp {

    public ServiceRestoreIdeaTest() {
        super(new ServiceRestoreIdea());
    }

    @Before
    public void setBirthdayToYesterday() {
        firefox.setBirthday(LocalDate.now().minusDays(1));
    }

    @Test
    public void restoringBookingShouldKeepTheGroup() throws SQLException {

        // Given a deleted idea with a group
        Idee idee = IdeesRepository.persistsIdea(Idee.builder()
                                                     .withText("Une nouvelle idée !")
                                                     .withOwner(firefox)
                                                     .build());
        IdeaGroup group = GroupIdeaRepository.createAGroup(50, 30, friendOfFirefox.getId());
        IdeesRepository.bookByGroup(idee.getId(), group.getId());
        IdeesRepository.remove(idee);

        // Doing the restore
        when(request.getParameter(ServiceRestoreIdea.IDEE_ID_PARAM)).thenReturn(String.valueOf(idee.getId()));
        when(request.getParameter(ServiceRestoreIdea.RESTORE_BOOKING)).thenReturn("true");
        StringServiceResponse resp = doTestServicePost();

        // The group does exist as well as the idea
        assertTrue(resp.isOK());
        assertTrue(GroupIdeaRepository.getGroupDetails(group.getId()).isPresent());
        final IdeaGroup foundGroup = IdeesRepository.getIdea(idee.getId())
                                                    .flatMap(Idee::getBookingInformation)
                                                    .flatMap(BookingInformation::getBookingGroup)
                                                    .orElseThrow(SQLException::new);
        assertEquals(group, foundGroup);
    }

    @Test
    public void restoringBookingShouldKeepTheBooking() throws SQLException {

        // Given a deleted idea with a booking by a person
        Idee idee = IdeesRepository.persistsIdea(Idee.builder()
                                                     .withText("Une nouvelle idée !")
                                                     .withOwner(firefox)
                                                     .build());
        IdeesRepository.reserver(idee.getId(), friendOfFirefox.getId());
        IdeesRepository.remove(idee);

        // Doing the restore
        when(request.getParameter(ServiceRestoreIdea.IDEE_ID_PARAM)).thenReturn(String.valueOf(idee.getId()));
        when(request.getParameter(ServiceRestoreIdea.RESTORE_BOOKING)).thenReturn("true");
        StringServiceResponse resp = doTestServicePost();

        // The booking does exist as well as the idea
        assertTrue(resp.isOK());
        assertEquals(friendOfFirefox, IdeesRepository.getIdea(idee.getId())
                                                     .flatMap(Idee::getBookingInformation)
                                                     .flatMap(BookingInformation::getBookingOwner)
                                                     .orElse(null));
    }

    @Test
    public void restoringBookingShouldKeepTheSubBooking() throws SQLException {

        // Given a deleted idea with a partial booking
        Idee idee = IdeesRepository.persistsIdea(Idee.builder()
                                                     .withText("Une nouvelle idée !")
                                                     .withOwner(firefox)
                                                     .build());
        SousReservationRepository.sousReserver(idee.getId(), friendOfFirefox.getId(), "Ma sous-rés!");
        IdeesRepository.remove(idee);

        // Doing the restore
        when(request.getParameter(ServiceRestoreIdea.IDEE_ID_PARAM)).thenReturn(String.valueOf(idee.getId()));
        when(request.getParameter(ServiceRestoreIdea.RESTORE_BOOKING)).thenReturn("true");
        StringServiceResponse resp = doTestServicePost();

        // The partial booking does exist as well as the idea
        assertTrue(resp.isOK());
        assertEquals(Optional.of(friendOfFirefox),
                     SousReservationRepository.getSousReservation(idee.getId())
                                              .stream()
                                              .map(SousReservationEntity::getUser)
                                              .findAny());
    }

    @Test
    public void restoringBookingSendsNotifications() throws SQLException {

        // Given a deleted idea with a booking by a person
        Idee idee = IdeesRepository.persistsIdea(Idee.builder()
                                                     .withText("Une nouvelle idée !")
                                                     .withOwner(firefox)
                                                     .build());
        IdeesRepository.reserver(idee.getId(), friendOfFirefox.getId());
        final TestServiceDeleteIdea deleteService = new TestServiceDeleteIdea();
        deleteService.registerParameter(ServiceDeleteIdea.IDEE_ID_PARAM, idee.getId());
        StringServiceResponse resp = deleteService.doTestServicePost();

        // Notifications verifications
        assertTrue(resp.isOK());
        assertEquals(1,
                     NotificationsRepository.fetcher()
                                            .whereOwner(friendOfFirefox)
                                            .whereType(NType.BOOKED_REMOVE)
                                            .whereIdea(idee)
                                            .fetch()
                                            .size());
        assertFalse(NotificationsRepository.fetcher()
                                           .whereOwner(friendOfFirefox)
                                           .whereType(NType.IDEA_RESTORED)
                                           .whereIdea(idee)
                                           .hasAny());

        // Doing the restore without the booking
        when(request.getParameter(ServiceRestoreIdea.IDEE_ID_PARAM)).thenReturn(String.valueOf(idee.getId()));
        when(request.getParameter(ServiceRestoreIdea.RESTORE_BOOKING)).thenReturn("true");
        resp = doTestServicePost();

        // Notifications
        //   - The booking remove has been deleted (we restored the idea)
        //   - We have an idea restored notification
        assertTrue(resp.isOK());
        assertFalse(NotificationsRepository.fetcher()
                                           .whereOwner(friendOfFirefox)
                                           .whereType(NType.BOOKED_REMOVE)
                                           .whereIdea(idee)
                                           .hasAny());
        assertEquals(1,
                     NotificationsRepository.fetcher()
                                            .whereOwner(friendOfFirefox)
                                            .whereType(NType.IDEA_RESTORED)
                                            .whereIdea(idee)
                                            .fetch()
                                            .size());
    }

    @Test
    public void restoringNonBookingShouldDeleteTheGroup() throws SQLException {

        // Given a deleted idea with a group
        Idee idee = IdeesRepository.persistsIdea(Idee.builder()
                                                     .withText("Une nouvelle idée !")
                                                     .withOwner(firefox)
                                                     .build());
        IdeaGroup group = GroupIdeaRepository.createAGroup(50, 30, friendOfFirefox.getId());
        IdeesRepository.bookByGroup(idee.getId(), group.getId());
        IdeesRepository.remove(idee);

        // Doing the restore
        when(request.getParameter(ServiceRestoreIdea.IDEE_ID_PARAM)).thenReturn(String.valueOf(idee.getId()));
        when(request.getParameter(ServiceRestoreIdea.RESTORE_BOOKING)).thenReturn("false");
        StringServiceResponse resp = doTestServicePost();

        // The idea exist while the group doesn't
        assertTrue(resp.isOK());
        assertEquals(Optional.of(idee), IdeesRepository.getIdea(idee.getId()));
        assertFalse(GroupIdeaRepository.getGroupDetails(group.getId()).isPresent());
    }

    @Test
    public void restoringNonBookingShouldDeleteTheBooking() throws SQLException {

        // Given a deleted idea with a booking by a person
        Idee idee = IdeesRepository.persistsIdea(Idee.builder()
                                                     .withText("Une nouvelle idée !")
                                                     .withOwner(firefox)
                                                     .build());
        IdeesRepository.reserver(idee.getId(), friendOfFirefox.getId());
        IdeesRepository.remove(idee);

        // Doing the restore
        when(request.getParameter(ServiceRestoreIdea.IDEE_ID_PARAM)).thenReturn(String.valueOf(idee.getId()));
        when(request.getParameter(ServiceRestoreIdea.RESTORE_BOOKING)).thenReturn("false");
        StringServiceResponse resp = doTestServicePost();

        // The idea exist while the booking doesn't
        assertTrue(resp.isOK());
        assertEquals(Optional.of(idee), IdeesRepository.getIdea(idee.getId()));
        assertTrue(IdeesRepository.canBook(idee.getId(), friendOfFirefox.getId()));
    }

    @Test
    public void restoringNonBookingShouldDeleteTheSubBooking() throws SQLException {

        // Given a deleted idea with a partial booking
        Idee idee = IdeesRepository.persistsIdea(Idee.builder()
                                                     .withText("Une nouvelle idée !")
                                                     .withOwner(firefox)
                                                     .build());
        SousReservationRepository.sousReserver(idee.getId(), friendOfFirefox.getId(), "Ma sous-rés!");
        IdeesRepository.remove(idee);

        // Doing the restore
        when(request.getParameter(ServiceRestoreIdea.IDEE_ID_PARAM)).thenReturn(String.valueOf(idee.getId()));
        when(request.getParameter(ServiceRestoreIdea.RESTORE_BOOKING)).thenReturn("false");
        StringServiceResponse resp = doTestServicePost();

        // The idea exist while the partial booking doesn't
        assertTrue(resp.isOK());
        assertEquals(Optional.of(idee), IdeesRepository.getIdea(idee.getId()));
        assertEquals(Collections.EMPTY_LIST, SousReservationRepository.getSousReservation(idee.getId()));
    }

    @Test
    public void restoringNonBookingDoesSendUpdateNotification() throws SQLException {

        // Given a deleted idea with a booking by a person
        Idee idee = IdeesRepository.persistsIdea(Idee.builder()
                                                     .withText("Une nouvelle idée !")
                                                     .withOwner(firefox)
                                                     .build());
        IdeesRepository.reserver(idee.getId(), friendOfFirefox.getId());
        final TestServiceDeleteIdea deleteService = new TestServiceDeleteIdea();
        deleteService.registerParameter(ServiceDeleteIdea.IDEE_ID_PARAM, idee.getId());
        StringServiceResponse resp = deleteService.doTestServicePost();

        // Notifications verifications
        assertTrue(resp.isOK());
        assertEquals(1,
                     NotificationsRepository.fetcher()
                                            .whereOwner(friendOfFirefox)
                                            .whereType(NType.BOOKED_REMOVE)
                                            .whereIdea(idee)
                                            .fetch()
                                            .size());
        assertFalse(NotificationsRepository.fetcher()
                                           .whereOwner(friendOfFirefox)
                                           .whereType(NType.IDEA_RESTORED)
                                           .whereIdea(idee)
                                           .hasAny());

        // Doing the restore without the booking
        when(request.getParameter(ServiceRestoreIdea.IDEE_ID_PARAM)).thenReturn(String.valueOf(idee.getId()));
        when(request.getParameter(ServiceRestoreIdea.RESTORE_BOOKING)).thenReturn("false");
        resp = doTestServicePost();

        // Notifications
        //   - The booking remove has been deleted (we restored the idea)
        //   - We have an idea restored notification
        assertTrue(resp.isOK());
        assertFalse(NotificationsRepository.fetcher()
                                           .whereOwner(friendOfFirefox)
                                           .whereType(NType.BOOKED_REMOVE)
                                           .whereIdea(idee)
                                           .hasAny());
        assertEquals(1,
                     NotificationsRepository.fetcher()
                                            .whereOwner(friendOfFirefox)
                                            .whereType(NType.IDEA_RESTORED)
                                            .whereIdea(idee)
                                            .fetch()
                                            .size());
    }

    @Test
    public void restoringSendsBirthdayNotification() throws SQLException {

        // The birthday is closed!
        firefox.setBirthday(LocalDate.now().plusDays(3));
        // Given a deleted idea with a booking by a person
        Idee idee = IdeesRepository.persistsIdea(Idee.builder()
                                                     .withText("Une nouvelle idée !")
                                                     .withOwner(firefox)
                                                     .build());
        IdeesRepository.reserver(idee.getId(), friendOfFirefox.getId());
        final TestServiceDeleteIdea deleteService = new TestServiceDeleteIdea();
        deleteService.registerParameter(ServiceDeleteIdea.IDEE_ID_PARAM, idee.getId());
        StringServiceResponse resp = deleteService.doTestServicePost();

        // Notifications verifications
        assertTrue(resp.isOK());
        assertFalse(NotificationsRepository.fetcher()
                                           .whereOwner(theAdmin)
                                           .whereType(NType.NEW_IDEA_BIRTHDAY_SOON)
                                           .whereIdea(idee)
                                           .hasAny());
        assertTrue(UserRelationsRepository.associationExists(firefox, theAdmin));

        // Doing the restore without the booking
        when(request.getParameter(ServiceRestoreIdea.IDEE_ID_PARAM)).thenReturn(String.valueOf(idee.getId()));
        when(request.getParameter(ServiceRestoreIdea.RESTORE_BOOKING)).thenReturn("false");
        resp = doTestServicePost();

        // Notifications
        //   - We have an idea modified given birthday closed notification
        assertTrue(resp.isOK());
        assertEquals(1,
                     NotificationsRepository.fetcher()
                                            .whereOwner(theAdmin)
                                            .whereType(NType.NEW_IDEA_BIRTHDAY_SOON)
                                            .whereIdea(idee)
                                            .fetch()
                                            .size());
    }

    @Test
    public void multipleDeleteRestoreShouldNotStackNotifications() throws SQLException {

        // Given a deleted idea with a booking by a person
        Idee idee = IdeesRepository.persistsIdea(Idee.builder()
                                                     .withText("Une nouvelle idée !")
                                                     .withOwner(firefox)
                                                     .build());
        IdeesRepository.reserver(idee.getId(), friendOfFirefox.getId());
        final TestServiceDeleteIdea deleteService = new TestServiceDeleteIdea();
        deleteService.registerParameter(ServiceDeleteIdea.IDEE_ID_PARAM, idee.getId());
        StringServiceResponse resp = deleteService.doTestServicePost();

        // Notifications verifications
        assertTrue(resp.isOK());
        assertEquals(1,
                     NotificationsRepository.fetcher()
                                            .whereOwner(friendOfFirefox)
                                            .whereType(NType.BOOKED_REMOVE)
                                            .whereIdea(idee)
                                            .fetch().size());
        assertFalse(NotificationsRepository.fetcher()
                                           .whereOwner(friendOfFirefox)
                                           .whereType(NType.IDEA_RESTORED)
                                           .whereIdea(idee)
                                           .hasAny());

        // Restore / Delete / Restore
        when(request.getParameter(ServiceRestoreIdea.IDEE_ID_PARAM)).thenReturn(String.valueOf(idee.getId()));
        when(request.getParameter(ServiceRestoreIdea.RESTORE_BOOKING)).thenReturn("true");
        resp = doTestServicePost();
        assertTrue(resp.isOK());
        resp = deleteService.doTestServicePost();
        assertTrue(resp.isOK());
        resp = doTestServicePost();
        assertTrue(resp.isOK());

        // Only one notification
        assertFalse(NotificationsRepository.fetcher()
                                           .whereOwner(friendOfFirefox)
                                           .whereType(NType.BOOKED_REMOVE)
                                           .whereIdea(idee)
                                           .hasAny());
        assertEquals(1,
                     NotificationsRepository.fetcher()
                                            .whereOwner(friendOfFirefox)
                                            .whereType(NType.IDEA_RESTORED)
                                            .whereIdea(idee)
                                            .fetch().size());
    }

    @Test
    public void surpriseAreDefinitelyGone() throws SQLException {

        // Given a deleted surprise
        Idee idee = IdeesRepository.persistsIdea(Idee.builder()
                                                     .withText("Une nouvelle idée !")
                                                     .withOwner(firefox)
                                                     .withSurpriseOwner(friendOfFirefox)
                                                     .build());
        IdeesRepository.remove(idee);

        // The surprise is really deleted... It cannot be retrieved anymore.
        assertEquals(Optional.empty(), IdeesRepository.getIdea(idee.getId()));
        assertEquals(Optional.empty(), IdeesRepository.getDeletedIdea(idee.getId()));
        assertEquals(0, ds.selectCountStar("select count(*) from IDEES where id = ?", idee.getId()));
    }

}