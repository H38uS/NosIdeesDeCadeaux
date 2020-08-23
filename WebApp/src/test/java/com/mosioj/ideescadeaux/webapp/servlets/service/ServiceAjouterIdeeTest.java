package com.mosioj.ideescadeaux.webapp.servlets.service;

import com.mosioj.ideescadeaux.core.model.entities.Idee;
import com.mosioj.ideescadeaux.core.model.notifications.instance.NotifNoIdea;
import com.mosioj.ideescadeaux.core.model.repositories.IdeesRepository;
import com.mosioj.ideescadeaux.core.model.repositories.NotificationsRepository;
import com.mosioj.ideescadeaux.webapp.servlets.AbstractTestServletWebApp;
import com.mosioj.ideescadeaux.webapp.servlets.controllers.idees.modification.AjouterIdeeAmi;
import org.junit.Test;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

public class ServiceAjouterIdeeTest extends AbstractTestServletWebApp {

    public ServiceAjouterIdeeTest() {
        super(new ServiceAjouterIdee());
    }

    @Test
    public void testAjouterIdeeAmisSuccess() throws IOException {

        int noIdea = NotificationsRepository.addNotification(_FRIEND_ID_, new NotifNoIdea());
        assertNotifDoesExists(noIdea);

        Map<String, String> param = new HashMap<>();
        param.put("text", "Ajouté par un ami");
        param.put("type", "");
        param.put("priority", 2 + "");
        createMultiPartRequest(param);

        when(request.getParameter(ServiceAjouterIdee.USER_PARAMETER)).thenReturn(_FRIEND_ID_ + "");
        StringServiceResponse resp = doTestServicePost();

        assertTrue(resp.isOK());
        assertNotifDoesNotExists(noIdea);
    }

    @Test
    public void testAjouterSurpriseAUnAmisSuccess() throws IOException {

        int noIdea = NotificationsRepository.addNotification(_FRIEND_ID_, new NotifNoIdea());
        assertNotifDoesExists(noIdea);

        Map<String, String> param = new HashMap<>();
        param.put("text", "Ajouté par un ami");
        param.put("type", "");
        param.put("priority", 2 + "");
        param.put("est_surprise", "on");
        createMultiPartRequest(param);

        when(request.getRequestDispatcher(AjouterIdeeAmi.VIEW_PAGE_URL)).thenReturn(dispatcher);
        when(request.getParameter(AjouterIdeeAmi.USER_PARAMETER)).thenReturn(_FRIEND_ID_ + "");
        StringServiceResponse resp = doTestServicePost();

        assertTrue(resp.isOK());
        assertNotifDoesExists(noIdea);
    }

    @Test
    public void testPostSuccess() throws IOException {

        int noIdea = NotificationsRepository.addNotification(_OWNER_ID_, new NotifNoIdea());
        assertNotifDoesExists(noIdea);

        Map<String, String> param = new HashMap<>();
        param.put("text", "Ma super idée wouhouuuu");
        param.put("priority", "1");
        createMultiPartRequest(param);
        when(request.getParameter(ServiceAjouterIdee.USER_PARAMETER)).thenReturn(_OWNER_ID_ + "");
        StringServiceResponse resp = doTestServicePost();

        assertTrue(resp.isOK());
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
        when(request.getParameter(ServiceAjouterIdee.USER_PARAMETER)).thenReturn(_OWNER_ID_ + "");
        StringServiceResponse resp = doTestServicePost();

        assertTrue(resp.isOK());
        int id = ds.selectInt("select max(id) from IDEES where owner = ?", _OWNER_ID_).orElseThrow(SQLException::new);
        Idee idee = IdeesRepository.getIdea(id).orElseThrow(SQLException::new);
        assertEquals(
                "<p>un lien <a rel=\"nofollow\" href=\"https://www.liveffn.com/cgi-bin/resultats.php?competition=62933&amp;langue=fra\">https://www.liveffn.com/cgi-bin/resultats.php?competition=62933&amp;langue=fra</a> et voilà</p>",
                idee.getHtml().trim());
    }
}