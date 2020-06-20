package com.mosioj.ideescadeaux.webapp.servlets.instance;

import com.mosioj.ideescadeaux.core.model.entities.Idee;
import com.mosioj.ideescadeaux.core.model.notifications.AbstractNotification;
import com.mosioj.ideescadeaux.core.model.notifications.instance.NotifAskIfIsUpToDate;
import com.mosioj.ideescadeaux.core.model.repositories.IdeesRepository;
import com.mosioj.ideescadeaux.core.model.repositories.IsUpToDateQuestionsRepository;
import com.mosioj.ideescadeaux.core.model.repositories.NotificationsRepository;
import com.mosioj.ideescadeaux.webapp.servlets.AbstractTestServletWebApp;
import com.mosioj.ideescadeaux.webapp.servlets.controllers.compte.MesNotifications;
import com.mosioj.ideescadeaux.webapp.servlets.controllers.idees.ConfirmationEstAJour;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.sql.SQLException;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

public class TestConfirmationEstAJourWebApp extends AbstractTestServletWebApp {

    public TestConfirmationEstAJourWebApp() {
        super(new ConfirmationEstAJour());
    }

    @Test
    public void testAskAndAnswerYes() throws SQLException {

        // Getting an idea of Firefox
        Idee idee = IdeesRepository.getIdeasOf(firefox.id).stream().findFirst().orElseThrow(SQLException::new);
        // Dropping former associations
        IsUpToDateQuestionsRepository.deleteAssociations(idee.getId());

        // His friend is asking if up to date
        final NotifAskIfIsUpToDate isUpToDate = new NotifAskIfIsUpToDate(friendOfFirefox, idee);
        int notifId = NotificationsRepository.addNotification(firefox.id, isUpToDate);
        assertNotifDoesExists(notifId);
        IsUpToDateQuestionsRepository.addAssociation(idee.getId(), friendOfFirefox.getId());
        assertTrue(IsUpToDateQuestionsRepository.associationExists(idee, friendOfFirefox));

        // Getting it from the DB to check the parameters insertion
        NotifAskIfIsUpToDate notif = (NotifAskIfIsUpToDate) NotificationsRepository.getNotification(notifId)
                                                                                   .orElseThrow(SQLException::new);
        assertEquals(friendOfFirefox.id, notif.getUserIdParam());

        when(request.getRequestDispatcher(MesNotifications.URL)).thenReturn(dispatcher);
        when(request.getParameter(ConfirmationEstAJour.IDEE_FIELD_PARAMETER)).thenReturn(idee.getId() + "");
        doTestGet();

        assertNotifDoesNotExists(notifId);
        assertFalse(IsUpToDateQuestionsRepository.associationExists(idee, friendOfFirefox));
    }

    @Test
    public void testOnANewIdea() throws SQLException {

        when(session.getAttribute("connected_user")).thenReturn(friendOfFirefox);
        int id = IdeesRepository.addIdea(friendOfFirefox, "ma nouvelle idée", "", 1, null, null, null);
        Idee idee = IdeesRepository.getIdea(id).orElseThrow(SQLException::new);
        int notifId = NotificationsRepository.addNotification(_FRIEND_ID_, new NotifAskIfIsUpToDate(firefox, idee));

        AbstractNotification n = NotificationsRepository.getNotification(notifId).orElseThrow(SQLException::new);
        String text = n.getText();
        String ideaId = text.substring(text.indexOf("nfirmation_est_a_jour?idee=") +
                                       "nfirmation_est_a_jour?idee=".length(),
                                       text.indexOf("\">Oui !</a></li><li>Non"));
        ideaId = new String(ideaId.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1);

        when(request.getRequestDispatcher(MesNotifications.URL)).thenReturn(dispatcher);
        when(request.getParameter(ConfirmationEstAJour.IDEE_FIELD_PARAMETER)).thenReturn(ideaId);

        doTestGet();

        // Ménage
        IdeesRepository.remove(id);
    }

}
