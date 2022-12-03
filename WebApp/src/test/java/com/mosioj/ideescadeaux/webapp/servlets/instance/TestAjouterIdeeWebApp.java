package com.mosioj.ideescadeaux.webapp.servlets.instance;

import com.mosioj.ideescadeaux.webapp.servlets.AbstractTestServletWebApp;
import com.mosioj.ideescadeaux.webapp.servlets.controllers.idees.AjouterIdee;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class TestAjouterIdeeWebApp extends AbstractTestServletWebApp {

    public TestAjouterIdeeWebApp() {
        super(new AjouterIdee());
    }

    @Before
    public void before() {
        when(request.getRequestDispatcher(AjouterIdee.VIEW_PAGE_URL)).thenReturn(dispatcher);
    }

    @Test
    public void testGetSuccess() {
        doTestGet();
        verify(request).getRequestDispatcher(eq(AjouterIdee.VIEW_PAGE_URL));
    }
}
