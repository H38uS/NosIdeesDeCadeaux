package com.mosioj.ideescadeaux.webapp.servlets.service;

import com.mosioj.ideescadeaux.core.model.entities.BookingInformation;
import com.mosioj.ideescadeaux.core.model.entities.IdeaGroup;
import com.mosioj.ideescadeaux.core.model.entities.Idee;
import com.mosioj.ideescadeaux.core.model.entities.SousReservationEntity;
import com.mosioj.ideescadeaux.core.model.notifications.NType;
import com.mosioj.ideescadeaux.core.model.repositories.*;
import com.mosioj.ideescadeaux.webapp.servlets.AbstractTestServletWebApp;
import com.mosioj.ideescadeaux.webapp.servlets.StringServiceResponse;
import org.junit.Before;
import org.junit.Test;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;

import static org.junit.Assert.*;

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
        Idee idee = IdeesRepository.saveTheIdea(Idee.builder().withText("Une nouvelle idée !").withOwner(firefox));
        IdeaGroup group = GroupIdeaRepository.createAGroup(50, 30, friendOfFirefox);
        IdeesRepository.bookByGroup(idee, group);
        IdeesRepository.remove(idee);

        // Doing the restore
        bindRequestParam(ServiceRestoreIdea.IDEE_ID_PARAM, String.valueOf(idee.getId()));
        bindRequestParam(ServiceRestoreIdea.RESTORE_BOOKING, "true");
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
        Idee idee = IdeesRepository.saveTheIdea(Idee.builder().withText("Une nouvelle idée !").withOwner(firefox));
        IdeesRepository.reserver(idee, friendOfFirefox);
        IdeesRepository.remove(idee);

        // Doing the restore
        bindRequestParam(ServiceRestoreIdea.IDEE_ID_PARAM, String.valueOf(idee.getId()));
        bindRequestParam(ServiceRestoreIdea.RESTORE_BOOKING, "true");
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
        Idee idee = IdeesRepository.saveTheIdea(Idee.builder().withText("Une nouvelle idée !").withOwner(firefox));
        SousReservationRepository.sousReserver(idee.getId(), friendOfFirefox.getId(), "Ma sous-rés!");
        IdeesRepository.remove(idee);

        // Doing the restore
        bindRequestParam(ServiceRestoreIdea.IDEE_ID_PARAM, String.valueOf(idee.getId()));
        bindRequestParam(ServiceRestoreIdea.RESTORE_BOOKING, "true");
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
    public void restoringBookingSendsNotifications() {

        // Given a deleted idea with a booking by a person
        Idee idee = IdeesRepository.saveTheIdea(Idee.builder().withText("Une nouvelle idée !").withOwner(firefox));
        IdeesRepository.reserver(idee, friendOfFirefox);
        final TestServiceDeleteIdea deleteService = new TestServiceDeleteIdea();
        bindRequestParam(ServiceDeleteIdea.IDEE_ID_PARAM, idee.getId());
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
        bindRequestParam(ServiceRestoreIdea.IDEE_ID_PARAM, String.valueOf(idee.getId()));
        bindRequestParam(ServiceRestoreIdea.RESTORE_BOOKING, "true");
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
        Idee idee = IdeesRepository.saveTheIdea(Idee.builder().withText("Une nouvelle idée !").withOwner(firefox));
        IdeaGroup group = GroupIdeaRepository.createAGroup(50, 30, friendOfFirefox);
        IdeesRepository.bookByGroup(idee, group);
        IdeesRepository.remove(idee);

        // Doing the restore
        bindRequestParam(ServiceRestoreIdea.IDEE_ID_PARAM, String.valueOf(idee.getId()));
        bindRequestParam(ServiceRestoreIdea.RESTORE_BOOKING, "false");
        StringServiceResponse resp = doTestServicePost();

        // The idea exist while the group doesn't
        assertTrue(resp.isOK());
        assertEquals(Optional.of(idee), IdeesRepository.getIdea(idee.getId()));
        assertFalse(GroupIdeaRepository.getGroupDetails(group.getId()).isPresent());
    }

    @Test
    public void restoringNonBookingShouldDeleteTheBooking() throws SQLException {

        // Given a deleted idea with a booking by a person
        Idee idee = IdeesRepository.saveTheIdea(Idee.builder().withText("Une nouvelle idée !").withOwner(firefox));
        IdeesRepository.reserver(idee, friendOfFirefox);
        IdeesRepository.remove(idee);

        // Doing the restore
        bindRequestParam(ServiceRestoreIdea.IDEE_ID_PARAM, String.valueOf(idee.getId()));
        bindRequestParam(ServiceRestoreIdea.RESTORE_BOOKING, "false");
        StringServiceResponse resp = doTestServicePost();
        idee = IdeesRepository.getIdea(idee.getId()).orElseThrow(SQLException::new);

        // The idea exist while the booking doesn't
        assertTrue(resp.isOK());
        assertTrue(IdeesRepository.canBook(idee, friendOfFirefox));
    }

    @Test
    public void restoringNonBookingShouldDeleteTheSubBooking() throws SQLException {

        // Given a deleted idea with a partial booking
        Idee idee = IdeesRepository.saveTheIdea(Idee.builder().withText("Une nouvelle idée !").withOwner(firefox));
        SousReservationRepository.sousReserver(idee.getId(), friendOfFirefox.getId(), "Ma sous-rés!");
        IdeesRepository.remove(idee);

        // Doing the restore
        bindRequestParam(ServiceRestoreIdea.IDEE_ID_PARAM, String.valueOf(idee.getId()));
        bindRequestParam(ServiceRestoreIdea.RESTORE_BOOKING, "false");
        StringServiceResponse resp = doTestServicePost();

        // The idea exist while the partial booking doesn't
        assertTrue(resp.isOK());
        assertEquals(Optional.of(idee), IdeesRepository.getIdea(idee.getId()));
        assertEquals(Collections.EMPTY_LIST, SousReservationRepository.getSousReservation(idee.getId()));
    }

    @Test
    public void restoringNonBookingDoesSendUpdateNotification() {

        // Given a deleted idea with a booking by a person
        Idee idee = IdeesRepository.saveTheIdea(Idee.builder().withText("Une nouvelle idée !").withOwner(firefox));
        IdeesRepository.reserver(idee, friendOfFirefox);
        final TestServiceDeleteIdea deleteService = new TestServiceDeleteIdea();
        bindRequestParam(ServiceDeleteIdea.IDEE_ID_PARAM, idee.getId());
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
        bindRequestParam(ServiceRestoreIdea.IDEE_ID_PARAM, String.valueOf(idee.getId()));
        bindRequestParam(ServiceRestoreIdea.RESTORE_BOOKING, "false");
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
    public void restoringSendsBirthdayNotification() {

        // The birthday is closed!
        firefox.setBirthday(LocalDate.now().plusDays(3));
        // Given a deleted idea with a booking by a person
        Idee idee = IdeesRepository.saveTheIdea(Idee.builder().withText("Une nouvelle idée !").withOwner(firefox));
        IdeesRepository.reserver(idee, friendOfFirefox);
        final TestServiceDeleteIdea deleteService = new TestServiceDeleteIdea();
        bindRequestParam(ServiceDeleteIdea.IDEE_ID_PARAM, idee.getId());
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
        bindRequestParam(ServiceRestoreIdea.IDEE_ID_PARAM, String.valueOf(idee.getId()));
        bindRequestParam(ServiceRestoreIdea.RESTORE_BOOKING, "false");
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
    public void multipleDeleteRestoreShouldNotStackNotifications() {

        // Given a deleted idea with a booking by a person
        Idee idee = IdeesRepository.saveTheIdea(Idee.builder().withText("Une nouvelle idée !").withOwner(firefox));
        IdeesRepository.reserver(idee, friendOfFirefox);
        final TestServiceDeleteIdea deleteService = new TestServiceDeleteIdea();
        bindRequestParam(ServiceDeleteIdea.IDEE_ID_PARAM, idee.getId());
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
        bindRequestParam(ServiceRestoreIdea.IDEE_ID_PARAM, String.valueOf(idee.getId()));
        bindRequestParam(ServiceRestoreIdea.RESTORE_BOOKING, "true");
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
        Idee idee = IdeesRepository.saveTheIdea(Idee.builder()
                                                    .withText("Une nouvelle idée !")
                                                    .withOwner(firefox)
                                                    .withSurpriseOwner(friendOfFirefox));
        IdeesRepository.remove(idee);

        // The surprise is really deleted... It cannot be retrieved anymore.
        assertEquals(Optional.empty(), IdeesRepository.getIdea(idee.getId()));
        assertEquals(Optional.empty(), IdeesRepository.getDeletedIdea(idee.getId()));
        assertEquals(0, ds.selectCountStar("select count(*) from IDEES where id = ?", idee.getId()));
    }

}