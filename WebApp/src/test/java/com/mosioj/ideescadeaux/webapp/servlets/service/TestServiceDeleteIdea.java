package com.mosioj.ideescadeaux.webapp.servlets.service;

import com.mosioj.ideescadeaux.core.model.entities.BookingInformation;
import com.mosioj.ideescadeaux.core.model.entities.Idee;
import com.mosioj.ideescadeaux.core.model.notifications.instance.*;
import com.mosioj.ideescadeaux.core.model.repositories.GroupIdeaRepository;
import com.mosioj.ideescadeaux.core.model.repositories.IdeesRepository;
import com.mosioj.ideescadeaux.core.model.repositories.IsUpToDateQuestionsRepository;
import com.mosioj.ideescadeaux.core.model.repositories.NotificationsRepository;
import com.mosioj.ideescadeaux.webapp.servlets.AbstractTestServletWebApp;
import org.junit.Test;

import java.sql.SQLException;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

public class TestServiceDeleteIdea extends AbstractTestServletWebApp {

    public TestServiceDeleteIdea() {
        super(new ServiceDeleteIdea());
    }

    @Test
    public void testDelete() throws SQLException {

        Idee idee = IdeesRepository.addIdea(firefox, "generated", "", 0, null, null, null);
        assertFalse(IdeesRepository.getDeletedIdea(idee.getId()).isPresent());

        when(request.getParameter(ServiceDeleteIdea.IDEE_ID_PARAM)).thenReturn(String.valueOf(idee.getId()));
        StringServiceResponse resp = doTestServicePost();

        assertTrue(resp.isOK());
        assertFalse(IdeesRepository.getIdea(idee.getId()).isPresent());
        assertTrue(IdeesRepository.getDeletedIdea(idee.getId()).isPresent());
    }

    @Test
    public void testDeleteWithGroupBooking() throws SQLException {

        // Creation de l'idée
        Idee idee = IdeesRepository.addIdea(firefox, "generated", "", 0, null, null, null);
        assertEquals(1, ds.selectCountStar("select count(*) from IDEES where id = ?", idee.getId()));

        // Creation du groupe
        int group = GroupIdeaRepository.createAGroup(200, 10, _MOI_AUTRE_);
        IdeesRepository.bookByGroup(idee.getId(), group);
        // rafraichissement de l'idée
        idee = IdeesRepository.getIdea(idee.getId()).orElseThrow(SQLException::new);
        int notifId = NotificationsRepository.addNotification(_FRIEND_ID_,
                                                              new NotifGroupEvolution(moiAutre, group, idee, true));
        assertNotifDoesExists(notifId);
        assertEquals(GroupIdeaRepository.getGroupDetails(group),
                     idee.getBookingInformation().flatMap(BookingInformation::getBookingGroup));
        assertEquals(1, ds.selectCountStar("select count(*) from GROUP_IDEA where id = ?", group));
        assertEquals(1, ds.selectCountStar("select count(*) from GROUP_IDEA_CONTENT where group_id = ?", group));

        // Suppression
        when(request.getParameter(ServiceDeleteIdea.IDEE_ID_PARAM)).thenReturn(String.valueOf(idee.getId()));
        StringServiceResponse resp = doTestServicePost();

        // Validation que cela supprime tout
        assertTrue(resp.isOK());
        assertNotifDoesNotExists(notifId);
        assertFalse(IdeesRepository.getIdea(idee.getId()).isPresent());
        // On conserve le groupe pour l'historique
        assertEquals(1, ds.selectCountStar("select count(*) from GROUP_IDEA where id = ?", group));
        assertEquals(1, ds.selectCountStar("select count(*) from GROUP_IDEA_CONTENT where group_id = ?", group));
    }

    @Test
    public void testUnderlyingNotificationAreWellRemoved() throws SQLException {

        Idee idee = IdeesRepository.addIdea(firefox, "generated", "", 0, null, null, null);
        assertEquals(1, ds.selectCountStar("select count(*) from IDEES where id = ?", idee.getId()));

        int isUpToDate = NotificationsRepository.addNotification(_OWNER_ID_,
                                                                 new NotifAskIfIsUpToDate(friendOfFirefox, idee));
        IsUpToDateQuestionsRepository.addAssociation(idee.getId(), friendOfFirefox.getId());
        assertTrue(IsUpToDateQuestionsRepository.associationExists(idee, friendOfFirefox));
        int confirmedUpToDate = NotificationsRepository.addNotification(_FRIEND_ID_,
                                                                        new NotifConfirmedUpToDate(firefox, idee));
        int groupSuggestion = NotificationsRepository.addNotification(_FRIEND_ID_,
                                                                      new NotifGroupSuggestion(firefox, 0, idee));
        int addByFriend = NotificationsRepository.addNotification(_OWNER_ID_,
                                                                  new NotifIdeaAddedByFriend(moiAutre, idee));
        int modifiedWhenBDSoon = NotificationsRepository.addNotification(_FRIEND_ID_,
                                                                         new NotifIdeaModifiedWhenBirthdayIsSoon(firefox,
                                                                                                                 idee,
                                                                                                                 false));
        int newComment = NotificationsRepository.addNotification(_OWNER_ID_, new NotifNewCommentOnIdea(firefox, idee));
        int newQuestion = NotificationsRepository.addNotification(_OWNER_ID_,
                                                                  new NotifNewQuestionOnIdea(friendOfFirefox,
                                                                                             idee,
                                                                                             true));
        int recurentUnbook = NotificationsRepository.addNotification(_FRIEND_ID_,
                                                                     new NotifRecurentIdeaUnbook(firefox, idee));

        assertTrue(isUpToDate > -1);
        assertNotifDoesExists(isUpToDate);
        assertNotifDoesExists(confirmedUpToDate);
        assertNotifDoesExists(groupSuggestion);
        assertNotifDoesExists(addByFriend);
        assertNotifDoesExists(modifiedWhenBDSoon);
        assertNotifDoesExists(newComment);
        assertNotifDoesExists(newQuestion);
        assertNotifDoesExists(recurentUnbook);

        // Suppression
        when(request.getParameter(ServiceDeleteIdea.IDEE_ID_PARAM)).thenReturn(String.valueOf(idee.getId()));
        StringServiceResponse resp = doTestServicePost();

        assertTrue(resp.isOK());
        assertNotifDoesNotExists(isUpToDate);
        assertNotifDoesNotExists(confirmedUpToDate);
        assertNotifDoesNotExists(groupSuggestion);
        assertNotifDoesNotExists(addByFriend);
        assertNotifDoesNotExists(modifiedWhenBDSoon);
        assertNotifDoesNotExists(newComment);
        assertNotifDoesNotExists(newQuestion);
        assertNotifDoesNotExists(recurentUnbook);
        assertFalse(IsUpToDateQuestionsRepository.associationExists(idee, friendOfFirefox));
    }

    @Test
    public void shouldNotBePossibleToDeleteOurSurprise() throws SQLException {

        // Given
        Idee idee = IdeesRepository.addIdea(firefox, "une surprise", null, 0, null, friendOfFirefox, friendOfFirefox);

        // Trying to delete it
        when(request.getParameter(ServiceDeleteIdea.IDEE_ID_PARAM)).thenReturn(String.valueOf(idee.getId()));
        StringServiceResponse resp = doTestServicePost();

        // Check
        assertFalse(resp.isOK());
        assertEquals("Impossible de modifier une surprise si ce n'est pas vous qui l'avez créée.", resp.getMessage());
        assertTrue(IdeesRepository.getIdea(idee.getId()).isPresent());

        // Delete it
        IdeesRepository.remove(idee);
    }

    @Test
    public void shouldBePossibleToDeleteOurSurprise() throws SQLException {

        // Given
        Idee idee = IdeesRepository.addIdea(friendOfFirefox, "une surprise", null, 0, null, firefox, firefox);

        // Trying to delete it
        when(request.getParameter(ServiceDeleteIdea.IDEE_ID_PARAM)).thenReturn(String.valueOf(idee.getId()));
        StringServiceResponse resp = doTestServicePost();

        // Check
        assertTrue(resp.isOK());
        assertFalse(IdeesRepository.getIdea(idee.getId()).isPresent());
    }
}
