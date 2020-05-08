package com.mosioj.ideescadeaux.webapp.servlets.service;

import com.mosioj.ideescadeaux.core.model.entities.User;
import com.mosioj.ideescadeaux.webapp.servlets.AbstractTestServletWebApp;
import com.mosioj.ideescadeaux.webapp.servlets.service.response.ServiceResponse;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertTrue;

public class ServiceEnregistrementMonCompteTestWebApp extends AbstractTestServletWebApp {

    public ServiceEnregistrementMonCompteTestWebApp() {
        super(new ServiceEnregistrementMonCompte());
    }

    @Test
    public void testSuccess() throws IOException {

        Map<String, String> param = new HashMap<>();
        param.put("modif_info_gen", "true");
        param.put("email", firefox.email);
        param.put("name", firefox.name);
        param.put("birthday", "");
        param.put("new_password", "aaaaaaaa");
        param.put("conf_password", "aaaaaaaa");
        param.put("image", "");
        param.put("old_picture", "default.png");
        createMultiPartRequest(param);

        MyServiceResp resp = doTestServicePost(MyServiceResp.class);

        assertTrue(resp.isOK());
    }

    private static class MyServiceResp extends ServiceResponse<User> {
        /**
         * Class constructor.
         *
         * @param isOK    True if there is no error.
         * @param message The JSon response message.
         * @param isAdmin Whether the user is an admin.
         */
        public MyServiceResp(boolean isOK, User message, boolean isAdmin, User connectedUser) {
            super(isOK, message, isAdmin, connectedUser);
        }
    }
}