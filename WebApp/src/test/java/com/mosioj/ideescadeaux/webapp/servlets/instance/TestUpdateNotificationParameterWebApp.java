package com.mosioj.ideescadeaux.webapp.servlets.instance;

import com.mosioj.ideescadeaux.webapp.servlets.AbstractTestServletWebApp;
import com.mosioj.ideescadeaux.webapp.servlets.controllers.compte.UpdateNotificationParameter;
import org.junit.Test;

public class TestUpdateNotificationParameterWebApp extends AbstractTestServletWebApp {

    public TestUpdateNotificationParameterWebApp() {
        super(new UpdateNotificationParameter());
    }

    @Test
    public void testPost() {
        bindRequestParam("name", "REJECTED_FRIENDSHIP");
        bindRequestParam("value", "EMAIL");

        doTestPost();
    }

}
