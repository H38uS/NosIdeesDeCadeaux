package com.mosioj.ideescadeaux.webapp.servlets.instance;

import com.mosioj.ideescadeaux.core.model.entities.Idee;
import com.mosioj.ideescadeaux.core.model.notifications.instance.NotifNoIdea;
import com.mosioj.ideescadeaux.core.model.repositories.IdeesRepository;
import com.mosioj.ideescadeaux.core.model.repositories.NotificationsRepository;
import com.mosioj.ideescadeaux.webapp.servlets.AbstractTestServlet;
import com.mosioj.ideescadeaux.webapp.servlets.controllers.idees.MaListe;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

public class TestMaListe extends AbstractTestServlet {

    public TestMaListe() {
        super(new MaListe());
    }

    @Before
    public void before() {
        when(request.getRequestDispatcher(MaListe.VIEW_PAGE_URL)).thenReturn(dispatcher);
    }

    @Test
    public void testGetSuccess() {
        doTestGet();
        verify(request).getRequestDispatcher(eq(MaListe.VIEW_PAGE_URL));
    }

    @Test
    public void testPostSuccess() throws IOException, SQLException {

        int noIdea = NotificationsRepository.addNotification(_OWNER_ID_, new NotifNoIdea());
        assertNotifDoesExists(noIdea);

        Map<String, String> param = new HashMap<>();
        param.put("text", "Ma super idée wouhouuuu");
        param.put("priority", "1");
        createMultiPartRequest(param);
        doTestPost();

        verify(session).setAttribute(eq("added_idea_id"), anyObject());
        verify(request, never()).setAttribute(eq("errors"), anyObject());
        assertNotifDoesNotExists(noIdea);
    }

    @Test
    public void testShouldAutoConvertLinks() throws IOException, SQLException {

        Map<String, String> param = new HashMap<>();
        param.put("text",
                  "un lien https://www.liveffn.com/cgi-bin/resultats.php?competition=62933&langue=fra et voilà");
        param.put("priority", "1");
        createMultiPartRequest(param);
        doTestPost();

        int id = ds.selectInt("select max(id) from IDEES where owner = ?", _OWNER_ID_).orElseThrow(SQLException::new);
        Idee idee = IdeesRepository.getIdeaWithoutEnrichment(id).orElseThrow(SQLException::new);
        assertEquals(
                "<p>un lien <a rel=\"nofollow\" href=\"https://www.liveffn.com/cgi-bin/resultats.php?competition=62933&amp;langue=fra\">https://www.liveffn.com/cgi-bin/resultats.php?competition=62933&amp;langue=fra</a> et voilà</p>",
                idee.getHtml().trim());
    }
}
