package com.mosioj.ideescadeaux.webapp.servlets.service;

import com.mosioj.ideescadeaux.core.model.entities.User;
import com.mosioj.ideescadeaux.core.model.repositories.UsersRepository;
import com.mosioj.ideescadeaux.webapp.entities.DecoratedWebAppUser;
import com.mosioj.ideescadeaux.webapp.servlets.AbstractTestServletWebApp;
import com.mosioj.ideescadeaux.webapp.servlets.service.response.PagedResponse;
import com.mosioj.ideescadeaux.webapp.servlets.service.response.ServiceResponse;
import org.junit.Test;

import java.sql.SQLException;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ServiceRechercherReseauTest extends AbstractTestServletWebApp {

    public ServiceRechercherReseauTest() {
        super(new ServiceRechercherReseau());
    }

    @Test
    public void testMatchingEmail() throws SQLException {

        // Given an email to match
        setConnectedUserTo(UsersRepository.getUser(6).orElse(null));
        bindGetRequestParam(ServiceRechercherReseau.SEARCH_USER_PARAM, "ther@hdzdzdzotmail.fr");

        // When doing the fetch
        RechercherReseauResponse resp = doTestServiceGet(RechercherReseauResponse.class);

        // Then it founds the appropriate person
        assertTrue(resp.isOK());
        final User expected = UsersRepository.getUser("another@hdzdzdzotmail.fr").orElseThrow(SQLException::new);
        final List<DecoratedWebAppUser> userList = resp.getMessage().getTheContent();
        assertEquals(1, userList.size());
        assertEquals(expected, userList.get(0).getUser());
    }

    @Test
    public void testSpecialCharacter() {
        // Given a name to match
        bindGetRequestParam(ServiceRechercherReseau.SEARCH_USER_PARAM, "Djoeîéèôe");

        // When doing the fetch
        RechercherReseauResponse resp = doTestServiceGet(RechercherReseauResponse.class);

        // Then it founds the appropriate person
        assertTrue(resp.isOK());
        assertEquals(List.of(new DecoratedWebAppUser(jo3, firefox)), resp.getMessage().getTheContent());
    }

    private static class RechercherReseauResponse extends ServiceResponse<PagedResponse<List<DecoratedWebAppUser>>> {
        /**
         * Class constructor.
         *
         * @param isOK          True if there is no error.
         * @param message       The JSon response message.
         * @param connectedUser The connected user or null if none.
         */
        public RechercherReseauResponse(boolean isOK,
                                        PagedResponse<List<DecoratedWebAppUser>> message,
                                        User connectedUser) {
            super(isOK, message, connectedUser);
        }
    }
}