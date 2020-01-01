package com.mosioj.ideescadeaux.webapp.servlets.service;

import com.mosioj.ideescadeaux.core.model.database.NoRowsException;
import com.mosioj.ideescadeaux.core.model.entities.Idee;
import com.mosioj.ideescadeaux.core.model.notifications.instance.*;
import com.mosioj.ideescadeaux.core.model.repositories.GroupIdeaRepository;
import com.mosioj.ideescadeaux.core.model.repositories.IdeesRepository;
import com.mosioj.ideescadeaux.core.model.repositories.NotificationsRepository;
import com.mosioj.ideescadeaux.webapp.servlets.AbstractTestServlet;
import org.junit.Test;

import java.sql.SQLException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class TestDeleteIdea extends AbstractTestServlet {

    public TestDeleteIdea() {
        super(new ServiceDeleteIdea());
    }

    @Test
    public void testDelete() {

        int id = IdeesRepository.addIdea(firefox, "generated", "", 0, null, null, null);
        assertEquals(1, ds.selectCountStar("select count(*) from IDEES where id = ?", id));
        assertEquals(0, ds.selectCountStar("select count(*) from IDEES_HIST where id = ?", id));

        when(request.getParameter(ServiceDeleteIdea.IDEE_ID_PARAM)).thenReturn(id + "");
        StringServiceResponse resp = doTestServicePost();

        assertTrue(resp.isOK());
        assertEquals(0, ds.selectCountStar("select count(*) from IDEES where id = ?", id));
        assertEquals(1, ds.selectCountStar("select count(*) from IDEES_HIST where id = ?", id));
    }

    @Test
    public void testDeleteWithGroupBooking() throws SQLException, NoRowsException {

        // Creation de l'idée
        int id = IdeesRepository.addIdea(firefox, "generated", "", 0, null, null, null);
        assertEquals(1, ds.selectCountStar("select count(*) from IDEES where id = ?", id));

        // Creation du groupe
        int group = GroupIdeaRepository.createAGroup(200, 10, _MOI_AUTRE_);
        IdeesRepository.bookByGroup(id, group);
        Idee idee = IdeesRepository.getIdeaWithoutEnrichment(id);
        int notifId = NotificationsRepository.addNotification(_FRIEND_ID_, new NotifGroupEvolution(moiAutre, group, idee, true));
        assertNotifDoesExists(notifId);
        assertEquals(GroupIdeaRepository.getGroupDetails(group), idee.getGroupKDO());
        assertEquals(1, ds.selectCountStar("select count(*) from GROUP_IDEA where id = ?", group));
        assertEquals(1, ds.selectCountStar("select count(*) from GROUP_IDEA_CONTENT where group_id = ?", group));

        // Suppression
        when(request.getParameter(ServiceDeleteIdea.IDEE_ID_PARAM)).thenReturn(id + "");
        StringServiceResponse resp = doTestServicePost();

        // Validation que cela supprime tout
        assertTrue(resp.isOK());
        assertNotifDoesNotExists(notifId);
        assertEquals(0, ds.selectCountStar("select count(*) from IDEES where id = ?", id));
        assertEquals(0, ds.selectCountStar("select count(*) from GROUP_IDEA where id = ?", group));
        assertEquals(0, ds.selectCountStar("select count(*) from GROUP_IDEA_CONTENT where group_id = ?", group));
    }

    @Test
    public void testUnderlyingNotificationAreWellRemoved() throws NoRowsException {

        int id = IdeesRepository.addIdea(firefox, "generated", "", 0, null, null, null);
        assertEquals(1, ds.selectCountStar("select count(*) from IDEES where id = ?", id));

        Idee idee = IdeesRepository.getIdeaWithoutEnrichment(id);
        int isUpToDate = NotificationsRepository.addNotification(_OWNER_ID_, new NotifAskIfIsUpToDate(friendOfFirefox, idee));
        int confirmedUpToDate = NotificationsRepository.addNotification(_FRIEND_ID_, new NotifConfirmedUpToDate(firefox, idee));
        int groupSuggestion = NotificationsRepository.addNotification(_FRIEND_ID_, new NotifGroupSuggestion(firefox, 0, idee));
        int addByFriend = NotificationsRepository.addNotification(_OWNER_ID_, new NotifIdeaAddedByFriend(moiAutre, idee));
        int modifiedWhenBDSoon = NotificationsRepository.addNotification(_FRIEND_ID_,
                                                                         new NotifIdeaModifiedWhenBirthdayIsSoon(firefox, idee, false));
        int newComment = NotificationsRepository.addNotification(_OWNER_ID_, new NotifNewCommentOnIdea(firefox, idee));
        int newQuestion = NotificationsRepository.addNotification(_OWNER_ID_, new NotifNewQuestionOnIdea(friendOfFirefox, idee, true));
        int recurentUnbook = NotificationsRepository.addNotification(_FRIEND_ID_, new NotifRecurentIdeaUnbook(firefox, idee));

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
        when(request.getParameter(ServiceDeleteIdea.IDEE_ID_PARAM)).thenReturn(id + "");
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
    }

}
