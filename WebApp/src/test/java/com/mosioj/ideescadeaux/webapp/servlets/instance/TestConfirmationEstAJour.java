package com.mosioj.ideescadeaux.webapp.servlets.instance;

import com.mosioj.ideescadeaux.core.model.database.NoRowsException;
import com.mosioj.ideescadeaux.core.model.entities.Idee;
import com.mosioj.ideescadeaux.core.model.notifications.AbstractNotification;
import com.mosioj.ideescadeaux.core.model.notifications.instance.NotifAskIfIsUpToDate;
import com.mosioj.ideescadeaux.core.model.repositories.IdeesRepository;
import com.mosioj.ideescadeaux.core.model.repositories.NotificationsRepository;
import com.mosioj.ideescadeaux.webapp.servlets.AbstractTestServlet;
import com.mosioj.ideescadeaux.webapp.servlets.controllers.compte.MesNotifications;
import com.mosioj.ideescadeaux.webapp.servlets.controllers.idees.ConfirmationEstAJour;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.sql.SQLException;

import static org.mockito.Mockito.when;

public class TestConfirmationEstAJour extends AbstractTestServlet {

    public TestConfirmationEstAJour() {
        super(new ConfirmationEstAJour());
    }

    @Test
    public void testAskAndAnswerYes() throws NoRowsException, SQLException {

        int id = ds.selectInt("select max(id) from IDEES where owner = ?", _OWNER_ID_);
        Idee idee = IdeesRepository.getIdeaWithoutEnrichment(id).orElseThrow(SQLException::new);

        int notifId = NotificationsRepository.addNotification(_OWNER_ID_, new NotifAskIfIsUpToDate(friendOfFirefox, idee));
        assertNotifDoesExists(notifId);

        when(request.getRequestDispatcher(MesNotifications.URL)).thenReturn(dispatcher);
        when(request.getParameter(ConfirmationEstAJour.IDEE_FIELD_PARAMETER)).thenReturn(id + "");
        doTestGet();
        // doTestPost(request, response);

        assertNotifDoesNotExists(notifId);
    }

    @Test
    public void testOnANewIdea() throws NoRowsException, SQLException {

        when(session.getAttribute("connected_user")).thenReturn(friendOfFirefox);
        int id = IdeesRepository.addIdea(friendOfFirefox, "ma nouvelle idée", "", 1, null, null, null);
        Idee idee = IdeesRepository.getIdeaWithoutEnrichment(id).orElseThrow(SQLException::new);
        int notifId = NotificationsRepository.addNotification(_FRIEND_ID_, new NotifAskIfIsUpToDate(firefox, idee));

        AbstractNotification n = NotificationsRepository.getNotification(notifId);
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