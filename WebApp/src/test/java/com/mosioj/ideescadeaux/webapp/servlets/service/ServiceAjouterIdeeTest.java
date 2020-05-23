package com.mosioj.ideescadeaux.webapp.servlets.service;

import com.mosioj.ideescadeaux.core.model.notifications.instance.NotifNoIdea;
import com.mosioj.ideescadeaux.core.model.repositories.NotificationsRepository;
import com.mosioj.ideescadeaux.webapp.servlets.AbstractTestServletWebApp;
import org.junit.Test;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class ServiceAjouterIdeeTest extends AbstractTestServletWebApp {

    public ServiceAjouterIdeeTest() {
        super(new ServiceAjouterIdee());
    }

    @Test
    public void testAjouterIdeeAmisSuccess() throws IOException, SQLException {

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
}