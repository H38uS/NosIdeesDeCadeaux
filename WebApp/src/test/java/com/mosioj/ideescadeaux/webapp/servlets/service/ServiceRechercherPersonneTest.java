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

public class ServiceRechercherPersonneTest extends AbstractTestServletWebApp {

    public ServiceRechercherPersonneTest() {
        super(new ServiceRechercherPersonne());
    }

    @Test
    public void nonFriendsShouldAlwaysCome() {

        final DecoratedWebAppUser decoratedMoiAutre = new DecoratedWebAppUser(moiAutre, firefox);
        bindGetRequestParam("name", "iautre@toto.co");

        // We get it when looking in all users
        bindGetRequestParam("only_non_friend", "nop");
        RechercherPersonneResponse resp = doTestServiceGet(RechercherPersonneResponse.class);
        assertTrue(resp.isOK());
        assertEquals(Collections.singletonList(decoratedMoiAutre), resp.getMessage().getTheContent());

        // And also when looking only for friends
        bindGetRequestParam("only_non_friend", "on");
        resp = doTestServiceGet(RechercherPersonneResponse.class);
        System.out.println(resp.getMessage().getTheContent());
        assertEquals(Collections.singletonList(decoratedMoiAutre), resp.getMessage().getTheContent());
    }

    @Test
    public void onlyNonFriendShouldFilterFriends() {

        final DecoratedWebAppUser decoratedFriend = new DecoratedWebAppUser(friendOfFirefox, firefox);
        bindGetRequestParam("name", "est@toto.co");

        // We get it when looking in all users
        bindGetRequestParam("only_non_friend", "nop");
        RechercherPersonneResponse resp = doTestServiceGet(RechercherPersonneResponse.class);
        assertTrue(resp.isOK());
        assertEquals(Collections.singletonList(decoratedFriend), resp.getMessage().getTheContent());

        // And not when filtering on non-friends
        bindGetRequestParam("only_non_friend", "on");
        resp = doTestServiceGet(RechercherPersonneResponse.class);
        assertTrue(resp.isOK());
        assertEquals(Collections.emptyList(), resp.getMessage().getTheContent());
    }

    @Test
    public void testSpecialCharacter() {
        // Given
        bindGetRequestParam("name", "Djoeîéèôe");

        // When
        RechercherPersonneResponse resp = doTestServiceGet(RechercherPersonneResponse.class);

        // Then
        assertTrue(resp.isOK());
        assertEquals(List.of(new DecoratedWebAppUser(jo3, firefox)), resp.getMessage().getTheContent());
    }

    private static class RechercherPersonneResponse extends ServiceResponse<PagedResponse<List<DecoratedWebAppUser>>> {
        /**
         * Class constructor.
         *
         * @param isOK          True if there is no error.
         * @param message       The JSon response message.
         * @param connectedUser The connected user or null if none.
         */
        public RechercherPersonneResponse(boolean isOK,
                                          PagedResponse<List<DecoratedWebAppUser>> message,
                                          User connectedUser) {
            super(isOK, message, connectedUser);
        }
    }
}