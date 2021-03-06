package com.mosioj.ideescadeaux.webapp.servlets.instance;

import com.mosioj.ideescadeaux.core.model.entities.Idee;
import com.mosioj.ideescadeaux.core.model.entities.Priority;
import com.mosioj.ideescadeaux.core.model.notifications.NType;
import com.mosioj.ideescadeaux.core.model.notifications.Notification;
import com.mosioj.ideescadeaux.core.model.repositories.IdeesRepository;
import com.mosioj.ideescadeaux.core.model.repositories.IsUpToDateQuestionsRepository;
import com.mosioj.ideescadeaux.core.model.repositories.NotificationsRepository;
import com.mosioj.ideescadeaux.core.model.repositories.PrioritiesRepository;
import com.mosioj.ideescadeaux.webapp.servlets.AbstractTestServletWebApp;
import com.mosioj.ideescadeaux.webapp.servlets.controllers.compte.MesNotifications;
import com.mosioj.ideescadeaux.webapp.servlets.controllers.idees.ConfirmationEstAJour;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

public class TestConfirmationEstAJourWebApp extends AbstractTestServletWebApp {

    public TestConfirmationEstAJourWebApp() {
        super(new ConfirmationEstAJour());
    }

    @Test
    public void testAskAndAnswerYes() throws SQLException {

        // Getting an idea of Firefox
        Idee idee = IdeesRepository.getIdeasOf(firefox).stream().findFirst().orElseThrow(SQLException::new);
        // Dropping former associations
        IsUpToDateQuestionsRepository.deleteAssociations(idee.getId());

        // His friend is asking if up to date
        final Notification isUpToDate = NType.IS_IDEA_UP_TO_DATE.with(friendOfFirefox, idee);
        int notifId = isUpToDate.sendItTo(firefox);
        assertNotifDoesExists(notifId);
        IsUpToDateQuestionsRepository.addAssociation(idee.getId(), friendOfFirefox.getId());
        assertTrue(IsUpToDateQuestionsRepository.associationExists(idee, friendOfFirefox));

        // Getting it from the DB to check the parameters insertion
        Notification notif = NotificationsRepository.getNotification(notifId).orElseThrow(SQLException::new);
        assertEquals(Optional.of(friendOfFirefox), notif.getUserParameter());

        when(request.getRequestDispatcher(MesNotifications.URL)).thenReturn(dispatcher);
        when(request.getParameter(ConfirmationEstAJour.IDEE_FIELD_PARAMETER)).thenReturn(idee.getId() + "");
        doTestGet();

        assertNotifDoesNotExists(notifId);
        assertFalse(IsUpToDateQuestionsRepository.associationExists(idee, friendOfFirefox));
    }

    @Test
    public void testOnANewIdea() throws SQLException {

        when(session.getAttribute("connected_user")).thenReturn(friendOfFirefox);
        Priority p = PrioritiesRepository.getPriority(1).orElseThrow(SQLException::new);
        Idee idee = IdeesRepository.saveTheIdea(Idee.builder()
                                                    .withOwner(friendOfFirefox)
                                                    .withText("ma nouvelle id??e")
                                                    .withPriority(p));
        int notifId = NType.IS_IDEA_UP_TO_DATE.with(firefox, idee).sendItTo(friendOfFirefox);

        Notification n = NotificationsRepository.getNotification(notifId).orElseThrow(SQLException::new);
        String text = n.getText();
        String ideaId = text.substring(text.indexOf("nfirmation_est_a_jour?idee=") +
                                       "nfirmation_est_a_jour?idee=".length(),
                                       text.indexOf("\">Oui !</a></li><li>Non"));
        ideaId = new String(ideaId.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1);

        when(request.getRequestDispatcher(MesNotifications.URL)).thenReturn(dispatcher);
        when(request.getParameter(ConfirmationEstAJour.IDEE_FIELD_PARAMETER)).thenReturn(ideaId);

        doTestGet();

        // M??nage
        IdeesRepository.remove(idee);
    }

}
