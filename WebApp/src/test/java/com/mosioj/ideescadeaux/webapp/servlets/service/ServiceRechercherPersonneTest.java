package com.mosioj.ideescadeaux.webapp.servlets.service;

import com.mosioj.ideescadeaux.core.model.entities.User;
import com.mosioj.ideescadeaux.webapp.entities.DecoratedWebAppUser;
import com.mosioj.ideescadeaux.webapp.servlets.AbstractTestServletWebApp;
import com.mosioj.ideescadeaux.webapp.servlets.service.response.PagedResponse;
import com.mosioj.ideescadeaux.webapp.servlets.service.response.ServiceResponse;
import org.junit.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class ServiceRechercherPersonneTest extends AbstractTestServletWebApp {

    public ServiceRechercherPersonneTest() {
        super(new ServiceRechercherPersonne());
    }

    @Test
    public void nonFriendsShouldAlwaysCome() {

        final DecoratedWebAppUser decoratedMoiAutre = new DecoratedWebAppUser(moiAutre, firefox);
        when(request.getParameter("name")).thenReturn("iautre@toto.co");

        // We get it when looking in all users
        when(request.getParameter("only_non_friend")).thenReturn("nop");
        RechercherPersonneResponse resp = doTestServiceGet(RechercherPersonneResponse.class);
        assertTrue(resp.isOK());
        assertEquals(Collections.singletonList(decoratedMoiAutre), resp.getMessage().getTheContent());

        // And also when looking only for friends
        when(request.getParameter("only_non_friend")).thenReturn("on");
        resp = doTestServiceGet(RechercherPersonneResponse.class);
        System.out.println(resp.getMessage().getTheContent());
        assertEquals(Collections.singletonList(decoratedMoiAutre), resp.getMessage().getTheContent());
    }

    @Test
    public void onlyNonFriendShouldFilterFriends() {

        final DecoratedWebAppUser decoratedFriend = new DecoratedWebAppUser(friendOfFirefox, firefox);
        when(request.getParameter("name")).thenReturn("est@toto.co");

        // We get it when looking in all users
        when(request.getParameter("only_non_friend")).thenReturn("nop");
        RechercherPersonneResponse resp = doTestServiceGet(RechercherPersonneResponse.class);
        assertTrue(resp.isOK());
        assertEquals(Collections.singletonList(decoratedFriend), resp.getMessage().getTheContent());

        // And not when filtering on non-friends
        when(request.getParameter("only_non_friend")).thenReturn("on");
        resp = doTestServiceGet(RechercherPersonneResponse.class);
        assertTrue(resp.isOK());
        assertEquals(Collections.emptyList(), resp.getMessage().getTheContent());
    }

    private static class RechercherPersonneResponse extends ServiceResponse<PagedResponse<List<DecoratedWebAppUser>>> {
        /**
         * Class constructor.
         *
         * @param isOK          True if there is no error.
         * @param message       The JSon response message.
         * @param isAdmin       Whether the user is an admin.
         * @param connectedUser The connected user or null if none.
         */
        public RechercherPersonneResponse(boolean isOK,
                                          PagedResponse<List<DecoratedWebAppUser>> message,
                                          boolean isAdmin,
                                          User connectedUser) {
            super(isOK, message, connectedUser);
        }
    }
}