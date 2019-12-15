package com.mosioj.ideescadeaux.servlets.service;

import com.mosioj.ideescadeaux.model.entities.Idee;
import com.mosioj.ideescadeaux.model.repositories.GroupIdea;
import com.mosioj.ideescadeaux.notifications.instance.*;
import com.mosioj.ideescadeaux.servlets.AbstractTestServlet;
import com.mosioj.ideescadeaux.servlets.service.response.ServiceResponse;
import org.junit.Test;

import java.sql.SQLException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class TestRemoveOneIdea extends AbstractTestServlet {

    public TestRemoveOneIdea() {
        super(new ServiceDeleteIdea());
    }

    @Test
    public void testDelete() throws SQLException {

        int id = idees.addIdea(firefox, "generated", "", 0, null, null, null);
        assertEquals(1, ds.selectCountStar("select count(*) from IDEES where id = ?", id));
        assertEquals(0, ds.selectCountStar("select count(*) from IDEES_HIST where id = ?", id));

        when(request.getParameter(ServiceDeleteIdea.IDEE_ID_PARAM)).thenReturn(id + "");
        ServiceResponse resp = doTestServicePost(request, response);

        assertTrue(resp.isOK());
        assertEquals(0, ds.selectCountStar("select count(*) from IDEES where id = ?", id));
        assertEquals(1, ds.selectCountStar("select count(*) from IDEES_HIST where id = ?", id));
    }

    @Test
    public void testDeleteWithGroupBooking() throws SQLException {

        // Creation de l'idée
        int id = idees.addIdea(firefox, "generated", "", 0, null, null, null);
        assertEquals(1, ds.selectCountStar("select count(*) from IDEES where id = ?", id));

        // Creation du groupe
        GroupIdea g = new GroupIdea();
        int group = g.createAGroup(200, 10, _MOI_AUTRE_);
        idees.bookByGroup(id, group);
        Idee idee = idees.getIdeaWithoutEnrichment(id);
        int notifId = notif.addNotification(_FRIEND_ID_, new NotifGroupEvolution(moiAutre, group, idee, true));
        assertNotifDoesExists(notifId);
        assertEquals(group, idee.getGroupKDO());
        assertEquals(1, ds.selectCountStar("select count(*) from GROUP_IDEA where id = ?", group));
        assertEquals(1, ds.selectCountStar("select count(*) from GROUP_IDEA_CONTENT where group_id = ?", group));

        // Suppression
        when(request.getParameter(ServiceDeleteIdea.IDEE_ID_PARAM)).thenReturn(id + "");
        ServiceResponse resp = doTestServicePost(request, response);

        // Validation que cela supprime tout
        assertTrue(resp.isOK());
        assertNotifDoesNotExists(notifId);
        assertEquals(0, ds.selectCountStar("select count(*) from IDEES where id = ?", id));
        assertEquals(0, ds.selectCountStar("select count(*) from GROUP_IDEA where id = ?", group));
        assertEquals(0, ds.selectCountStar("select count(*) from GROUP_IDEA_CONTENT where group_id = ?", group));
    }

    @Test
    public void testUnderlyingNotificationAreWellRemoved() throws SQLException {

        int id = idees.addIdea(firefox, "generated", "", 0, null, null, null);
        assertEquals(1, ds.selectCountStar("select count(*) from IDEES where id = ?", id));

        Idee idee = idees.getIdeaWithoutEnrichment(id);
        int isUpToDate = notif.addNotification(_OWNER_ID_, new NotifAskIfIsUpToDate(friendOfFirefox, idee));
        int confirmedUpToDate = notif.addNotification(_FRIEND_ID_, new NotifConfirmedUpToDate(firefox, idee));
        int groupSuggestion = notif.addNotification(_FRIEND_ID_, new NotifGroupSuggestion(firefox, 0, idee));
        int addByFriend = notif.addNotification(_OWNER_ID_, new NotifIdeaAddedByFriend(moiAutre, idee));
        int modifiedWhenBDSoon = notif.addNotification(_FRIEND_ID_,
                                                       new NotifIdeaModifiedWhenBirthdayIsSoon(firefox, idee, false));
        int newComment = notif.addNotification(_OWNER_ID_, new NotifNewCommentOnIdea(firefox, idee));
        int newQuestion = notif.addNotification(_OWNER_ID_, new NotifNewQuestionOnIdea(friendOfFirefox, idee, true));
        int recurentUnbook = notif.addNotification(_FRIEND_ID_, new NotifRecurentIdeaUnbook(firefox, idee));

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
        ServiceResponse resp = doTestServicePost(request, response);

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
