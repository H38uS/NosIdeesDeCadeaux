package com.mosioj.ideescadeaux.servlets.instance;

import com.mosioj.ideescadeaux.model.entities.User;
import com.mosioj.ideescadeaux.servlets.AbstractTestServlet;
import com.mosioj.ideescadeaux.servlets.controllers.relations.RechercherPersonne;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.ServletException;
import java.io.IOException;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class TestRecherchePersonne extends AbstractTestServlet {

    public TestRecherchePersonne() {
        super(new RechercherPersonne());
    }


    @Before
    public void before() {
        when(request.getRequestDispatcher(RechercherPersonne.DEFAULT_FORM_URL)).thenReturn(dispatcher);
    }

    @Test
    public void testPost() throws ServletException, IOException {
        when(session.getAttribute("connected_user")).thenReturn(new User(-1, "", "", ""));
        when(request.getParameter("name")).thenReturn("monGroupe");
        instance.doGet(request, response);
        verify(request).getRequestDispatcher(eq(RechercherPersonne.DEFAULT_FORM_URL));
    }

}
