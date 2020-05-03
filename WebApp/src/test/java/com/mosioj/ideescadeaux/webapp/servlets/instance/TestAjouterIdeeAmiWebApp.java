package com.mosioj.ideescadeaux.webapp.servlets.instance;

import com.mosioj.ideescadeaux.core.model.notifications.instance.NotifNoIdea;
import com.mosioj.ideescadeaux.core.model.repositories.NotificationsRepository;
import com.mosioj.ideescadeaux.webapp.servlets.AbstractTestServletWebApp;
import com.mosioj.ideescadeaux.webapp.servlets.controllers.idees.modification.AjouterIdeeAmi;
import org.junit.Test;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.when;

public class TestAjouterIdeeAmiWebApp extends AbstractTestServletWebApp {

    public TestAjouterIdeeAmiWebApp() {
        super(new AjouterIdeeAmi());
    }

    @Test
    public void testSuccess() throws IOException, SQLException {

        int noIdea = NotificationsRepository.addNotification(_FRIEND_ID_, new NotifNoIdea());
        assertNotifDoesExists(noIdea);

        Map<String, String> param = new HashMap<>();
        param.put("text", "Ajouté par un ami");
        param.put("type", "");
        param.put("priority", 2 + "");
        createMultiPartRequest(param);

        when(request.getRequestDispatcher(AjouterIdeeAmi.VIEW_PAGE_URL)).thenReturn(dispatcher);
        when(request.getParameter(AjouterIdeeAmi.USER_PARAMETER)).thenReturn(_FRIEND_ID_ + "");
        doTestPost();

        assertNotifDoesNotExists(noIdea);
    }

    @Test
    public void testSuccessSurprise() throws IOException, SQLException {

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
        doTestPost();

        assertNotifDoesExists(noIdea);
    }

}
