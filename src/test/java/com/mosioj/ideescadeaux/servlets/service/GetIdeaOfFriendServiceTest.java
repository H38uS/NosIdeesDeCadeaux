package com.mosioj.ideescadeaux.servlets.service;

import com.mosioj.ideescadeaux.model.entities.Idee;
import com.mosioj.ideescadeaux.servlets.AbstractTestServlet;
import com.mosioj.ideescadeaux.servlets.rootservlet.IdeesCadeauxGetAndPostServlet;
import com.mosioj.ideescadeaux.servlets.securitypolicy.root.SecurityPolicy;
import org.junit.Test;

import java.sql.SQLException;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

public class GetIdeaOfFriendServiceTest extends AbstractTestServlet {

    public GetIdeaOfFriendServiceTest() {
        super(new GetIdeaOfFriendService());
    }

    @Test
    public void testSucces() throws SQLException {

        Idee idee = idees.getIdeasOf(friendOfFirefox.id).get(0);
        when(request.getParameter(GetIdeaOfFriendService.IDEA_ID_PARAM)).thenReturn(idee.getId() + "");
        when(request.getRequestDispatcher(GetIdeaOfFriendService.VIEW_PAGE_URL)).thenReturn(dispatcher);

        doTestGet();
    }
}