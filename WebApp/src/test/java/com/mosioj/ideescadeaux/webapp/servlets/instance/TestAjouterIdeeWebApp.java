package com.mosioj.ideescadeaux.webapp.servlets.instance;

import com.mosioj.ideescadeaux.webapp.servlets.AbstractTestServletWebApp;
import com.mosioj.ideescadeaux.webapp.servlets.controllers.idees.AjouterIdee;
import org.junit.Test;

public class TestAjouterIdeeWebApp extends AbstractTestServletWebApp {

    public TestAjouterIdeeWebApp() {
        super(new AjouterIdee());
    }

    @Test
    public void testGetSuccess() {
        doTestGet();
    }
}
