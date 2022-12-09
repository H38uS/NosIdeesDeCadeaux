package com.mosioj.ideescadeaux.webapp.servlets.service;

import com.mosioj.ideescadeaux.core.model.entities.User;
import com.mosioj.ideescadeaux.core.model.entities.text.Idee;
import com.mosioj.ideescadeaux.core.model.repositories.UsersRepository;
import com.mosioj.ideescadeaux.webapp.WebAppTemplateTest;
import com.mosioj.ideescadeaux.webapp.entities.DecoratedWebAppIdea;
import com.mosioj.ideescadeaux.webapp.entities.OwnerIdeas;
import com.mosioj.ideescadeaux.webapp.servlets.AbstractTestServletWebApp;
import com.mosioj.ideescadeaux.webapp.servlets.service.response.PagedResponse;
import com.mosioj.ideescadeaux.webapp.servlets.service.response.ServiceResponse;
import org.junit.Test;

import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class ServiceAfficherListesTest extends AbstractTestServletWebApp {

    public ServiceAfficherListesTest() {
        super(new ServiceAfficherListes());
    }

    @Test
    public void shouldBePossibleToViewMyIdeas() {

        setConnectedUserTo(WebAppTemplateTest.moiAutre);
        bindGetRequestParam(ServiceAfficherListes.NAME_OR_EMAIL, moiAutre.getEmail());

        // Act
        AfficherListeResponse answer = doTestServiceGet(AfficherListeResponse.class);

        // Check
        assertTrue(answer.isOK());
        assertEquals(1, answer.getMessage().getTheContent().size());
        assertTrue(answer.getMessage().getTheContent().get(0).getIdeas().size() > 0);
    }

    @Test
    public void shouldNotBePossibleToViewMySurprises() {

        setConnectedUserTo(WebAppTemplateTest.moiAutre);
        bindGetRequestParam(ServiceAfficherListes.NAME_OR_EMAIL, moiAutre.getEmail());

        // Act
        AfficherListeResponse answer = doTestServiceGet(AfficherListeResponse.class);

        // Check
        assertTrue(answer.isOK());
        assertEquals(1, answer.getMessage().getTheContent().size());
        final List<Idee> surprises = answer.getMessage()
                                           .getTheContent()
                                           .get(0)
                                           .getIdeas()
                                           .stream()
                                           .map(DecoratedWebAppIdea::getIdee)
                                           .filter(Idee::isASurprise)
                                           .toList();
        assertTrue(surprises.isEmpty());
    }

    @Test
    public void shouldBePossibleToSeeFriendIdeasIncludingSurprises() {

        setConnectedUserTo(WebAppTemplateTest.friendOfFirefox);
        bindGetRequestParam(ServiceAfficherListes.NAME_OR_EMAIL, moiAutre.getName());

        // Act
        AfficherListeResponse answer = doTestServiceGet(AfficherListeResponse.class);

        // Check
        assertTrue(answer.isOK());
        assertEquals(1, answer.getMessage().getTheContent().size());
        final List<Idee> surprises = answer.getMessage()
                                           .getTheContent()
                                           .get(0)
                                           .getIdeas()
                                           .stream()
                                           .map(DecoratedWebAppIdea::getIdee)
                                           .filter(Idee::isASurprise)
                                           .toList();
        assertFalse(surprises.isEmpty());
    }

    @Test
    public void shouldBeAbleToMatchMultiple() throws SQLException {

        setConnectedUserTo(WebAppTemplateTest.jo3);
        bindGetRequestParam(ServiceAfficherListes.NAME_OR_EMAIL, "trr");

        // Act
        AfficherListeResponse answer = doTestServiceGet(AfficherListeResponse.class);

        // Check
        assertTrue(answer.isOK());
        List<User> users = answer.getMessage()
                                 .getTheContent()
                                 .stream()
                                 .map(OwnerIdeas::getOwner)
                                 .collect(Collectors.toList());
        assertEquals(List.of(jo3, UsersRepository.getUser(6).orElseThrow(SQLException::new)), users);
    }

    @Test
    public void testSpecialCharacter() {
        // Given
        bindGetRequestParam(ServiceAfficherListes.NAME_OR_EMAIL, "Djoeîéèôe");

        // When
        AfficherListeResponse resp = doTestServiceGet(AfficherListeResponse.class);

        // Then
        assertTrue(resp.isOK());
        assertEquals(List.of(jo3), resp.getMessage().getTheContent().stream().map(OwnerIdeas::getOwner).toList());
    }

    private static class AfficherListeResponse extends ServiceResponse<PagedResponse<List<OwnerIdeas>>> {
        /**
         * Class constructor.
         *
         * @param isOK          True if there is no error.
         * @param message       The JSon response message.
         * @param connectedUser The connected user or null if none.
         */
        public AfficherListeResponse(boolean isOK,
                                     PagedResponse<List<OwnerIdeas>> message,
                                     User connectedUser) {
            super(isOK, message, connectedUser);
        }
    }
}