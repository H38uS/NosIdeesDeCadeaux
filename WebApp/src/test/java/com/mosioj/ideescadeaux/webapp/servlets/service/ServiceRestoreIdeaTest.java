package com.mosioj.ideescadeaux.webapp.servlets.service;

import com.mosioj.ideescadeaux.core.model.entities.BookingInformation;
import com.mosioj.ideescadeaux.core.model.entities.IdeaGroup;
import com.mosioj.ideescadeaux.core.model.entities.Idee;
import com.mosioj.ideescadeaux.core.model.entities.SousReservationEntity;
import com.mosioj.ideescadeaux.core.model.notifications.AbstractNotification;
import com.mosioj.ideescadeaux.core.model.notifications.NotificationType;
import com.mosioj.ideescadeaux.core.model.notifications.ParameterName;
import com.mosioj.ideescadeaux.core.model.repositories.*;
import com.mosioj.ideescadeaux.webapp.servlets.AbstractTestServletWebApp;
import org.junit.Before;
import org.junit.Test;

import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

public class ServiceRestoreIdeaTest extends AbstractTestServletWebApp {

    public ServiceRestoreIdeaTest() {
        super(new ServiceRestoreIdea());
    }

    @Before
    public void setBirthdayToYesterday() {
        firefox.setBirthday(Date.valueOf(LocalDate.now().minusDays(1)));
    }

    @Test
    public void restoringBookingShouldKeepTheGroup() throws SQLException {

        // Given a deleted idea with a group
        Idee idee = IdeesRepository.persistsIdea(Idee.builder()
                                                     .withText("Une nouvelle idée !")
                                                     .withOwner(firefox)
                                                     .build());
        int groupId = GroupIdeaRepository.createAGroup(50, 30, friendOfFirefox.getId());
        IdeesRepository.bookByGroup(idee.getId(), groupId);
        IdeesRepository.remove(idee);

        // Doing the restore
        when(request.getParameter(ServiceRestoreIdea.IDEE_ID_PARAM)).thenReturn(String.valueOf(idee.getId()));
        when(request.getParameter(ServiceRestoreIdea.RESTORE_BOOKING)).thenReturn("true");
        StringServiceResponse resp = doTestServicePost();

        // The group does exist as well as the idea
        assertTrue(resp.isOK());
        assertTrue(GroupIdeaRepository.getGroupDetails(groupId).isPresent());
        final int foundGroupId = IdeesRepository.getIdea(idee.getId())
                                                .flatMap(Idee::getBookingInformation)
                                                .flatMap(BookingInformation::getBookingGroup)
                                                .map(IdeaGroup::getId)
                                                .orElse(-1);
        assertEquals(groupId, foundGroupId);
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
        List<AbstractNotification> bookingRemove = NotificationsRepository.getNotifications(friendOfFirefox.getId(),
                                                                                            NotificationType.BOOKED_REMOVE,
                                                                                            ParameterName.IDEA_ID,
                                                                                            idee.getId());
        assertEquals(1, bookingRemove.size());
        assertEquals(0,
                     NotificationsRepository.getNotifications(friendOfFirefox.getId(),
                                                              NotificationType.IDEA_RESTORED,
                                                              ParameterName.IDEA_ID,
                                                              idee.getId()).size());

        // Doing the restore without the booking
        when(request.getParameter(ServiceRestoreIdea.IDEE_ID_PARAM)).thenReturn(String.valueOf(idee.getId()));
        when(request.getParameter(ServiceRestoreIdea.RESTORE_BOOKING)).thenReturn("true");
        resp = doTestServicePost();

        // Notifications
        //   - The booking remove has been deleted (we restored the idea)
        //   - We have an idea restored notification
        assertTrue(resp.isOK());
        assertEquals(Collections.EMPTY_LIST, NotificationsRepository.getNotifications(friendOfFirefox.getId(),
                                                                                      NotificationType.BOOKED_REMOVE,
                                                                                      ParameterName.IDEA_ID,
                                                                                      idee.getId()));
        assertEquals(1,
                     NotificationsRepository.getNotifications(friendOfFirefox.getId(),
                                                              NotificationType.IDEA_RESTORED,
                                                              ParameterName.IDEA_ID,
                                                              idee.getId()).size());
    }

    @Test
    public void restoringNonBookingShouldDeleteTheGroup() throws SQLException {

        // Given a deleted idea with a group
        Idee idee = IdeesRepository.persistsIdea(Idee.builder()
                                                     .withText("Une nouvelle idée !")
                                                     .withOwner(firefox)
                                                     .build());
        int groupId = GroupIdeaRepository.createAGroup(50, 30, friendOfFirefox.getId());
        IdeesRepository.bookByGroup(idee.getId(), groupId);
        IdeesRepository.remove(idee);

        // Doing the restore
        when(request.getParameter(ServiceRestoreIdea.IDEE_ID_PARAM)).thenReturn(String.valueOf(idee.getId()));
        when(request.getParameter(ServiceRestoreIdea.RESTORE_BOOKING)).thenReturn("false");
        StringServiceResponse resp = doTestServicePost();

        // The idea exist while the group doesn't
        assertTrue(resp.isOK());
        assertEquals(Optional.of(idee), IdeesRepository.getIdea(idee.getId()));
        assertFalse(GroupIdeaRepository.getGroupDetails(groupId).isPresent());
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
        List<AbstractNotification> bookingRemove = NotificationsRepository.getNotifications(friendOfFirefox.getId(),
                                                                                            NotificationType.BOOKED_REMOVE,
                                                                                            ParameterName.IDEA_ID,
                                                                                            idee.getId());
        assertEquals(1, bookingRemove.size());
        assertEquals(0,
                     NotificationsRepository.getNotifications(friendOfFirefox.getId(),
                                                              NotificationType.IDEA_RESTORED,
                                                              ParameterName.IDEA_ID,
                                                              idee.getId()).size());

        // Doing the restore without the booking
        when(request.getParameter(ServiceRestoreIdea.IDEE_ID_PARAM)).thenReturn(String.valueOf(idee.getId()));
        when(request.getParameter(ServiceRestoreIdea.RESTORE_BOOKING)).thenReturn("false");
        resp = doTestServicePost();

        // Notifications
        //   - The booking remove has been deleted (we restored the idea)
        //   - We have an idea restored notification
        assertTrue(resp.isOK());
        assertEquals(Collections.EMPTY_LIST, NotificationsRepository.getNotifications(friendOfFirefox.getId(),
                                                                                      NotificationType.BOOKED_REMOVE,
                                                                                      ParameterName.IDEA_ID,
                                                                                      idee.getId()));
        assertEquals(1,
                     NotificationsRepository.getNotifications(friendOfFirefox.getId(),
                                                              NotificationType.IDEA_RESTORED,
                                                              ParameterName.IDEA_ID,
                                                              idee.getId()).size());
    }

    @Test
    public void restoringSendsBirthdayNotification() throws SQLException {

        // The birthday is closed!
        firefox.setBirthday(Date.valueOf(LocalDate.now().plusDays(3)));
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
        assertEquals(0, NotificationsRepository.getNotifications(theAdmin.getId(),
                                                                 NotificationType.IDEA_OF_FRIEND_MODIFIED_WHEN_BIRTHDAY_IS_SOON,
                                                                 ParameterName.IDEA_ID,
                                                                 idee.getId()).size());
        assertTrue(UserRelationsRepository.associationExists(firefox.getId(), theAdmin.getId()));

        // Doing the restore without the booking
        when(request.getParameter(ServiceRestoreIdea.IDEE_ID_PARAM)).thenReturn(String.valueOf(idee.getId()));
        when(request.getParameter(ServiceRestoreIdea.RESTORE_BOOKING)).thenReturn("false");
        resp = doTestServicePost();

        // Notifications
        //   - We have an idea modified given birthday closed notification
        assertTrue(resp.isOK());
        assertEquals(1, NotificationsRepository.getNotifications(theAdmin.getId(),
                                                                 NotificationType.IDEA_OF_FRIEND_MODIFIED_WHEN_BIRTHDAY_IS_SOON,
                                                                 ParameterName.IDEA_ID,
                                                                 idee.getId()).size());
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
        List<AbstractNotification> bookingRemove = NotificationsRepository.getNotifications(friendOfFirefox.getId(),
                                                                                            NotificationType.BOOKED_REMOVE,
                                                                                            ParameterName.IDEA_ID,
                                                                                            idee.getId());
        assertEquals(1, bookingRemove.size());
        assertEquals(0,
                     NotificationsRepository.getNotifications(friendOfFirefox.getId(),
                                                              NotificationType.IDEA_RESTORED,
                                                              ParameterName.IDEA_ID,
                                                              idee.getId()).size());

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
        assertEquals(Collections.EMPTY_LIST, NotificationsRepository.getNotifications(friendOfFirefox.getId(),
                                                                                      NotificationType.BOOKED_REMOVE,
                                                                                      ParameterName.IDEA_ID,
                                                                                      idee.getId()));
        assertEquals(1,
                     NotificationsRepository.getNotifications(friendOfFirefox.getId(),
                                                              NotificationType.IDEA_RESTORED,
                                                              ParameterName.IDEA_ID,
                                                              idee.getId()).size());
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