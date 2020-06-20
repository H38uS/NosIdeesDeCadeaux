package com.mosioj.ideescadeaux.webapp.servlets.service;

import com.mosioj.ideescadeaux.core.model.entities.Idee;
import com.mosioj.ideescadeaux.core.model.entities.User;
import com.mosioj.ideescadeaux.core.model.repositories.IdeesRepository;
import com.mosioj.ideescadeaux.webapp.entities.DecoratedWebAppIdea;
import com.mosioj.ideescadeaux.webapp.servlets.AbstractTestServletWebApp;
import com.mosioj.ideescadeaux.webapp.servlets.service.response.ServiceResponse;
import org.junit.Test;

import java.sql.SQLException;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

public class ServiceGetIdeaTestWebApp extends AbstractTestServletWebApp {

    public ServiceGetIdeaTestWebApp() {
        super(new ServiceGetIdea());
    }

    @Test
    public void itIsPossibleToGetAFriendIdea() throws SQLException {

        // Given
        Idee idee = IdeesRepository.getIdeasOf(friendOfFirefox.id)
                                   .stream()
                                   .filter(i -> !i.isASurprise())
                                   .findFirst()
                                   .orElseThrow(SQLException::new);
        when(request.getParameter(ServiceGetIdea.IDEA_ID_PARAM)).thenReturn(idee.getId() + "");

        // Act
        DecoratedWebAppIdeaServiceResponse answer = doTestServiceGet(DecoratedWebAppIdeaServiceResponse.class);

        // Check
        assertTrue(answer.isOK());
        assertEquals(idee, answer.getMessage().getIdee());
    }

    @Test
    public void itIsPossibleToGetAFriendSurpriseIdea() throws SQLException {

        // Given
        Idee idee = IdeesRepository.getIdeasOf(friendOfFirefox.id)
                                   .stream()
                                   .filter(Idee::isASurprise)
                                   .findFirst()
                                   .orElseThrow(SQLException::new);
        when(request.getParameter(ServiceGetIdea.IDEA_ID_PARAM)).thenReturn(idee.getId() + "");

        // Act
        DecoratedWebAppIdeaServiceResponse answer = doTestServiceGet(DecoratedWebAppIdeaServiceResponse.class);

        // Check
        assertTrue(answer.isOK());
        assertEquals(idee, answer.getMessage().getIdee());
    }

    @Test
    public void itIsPossibleToGetOurIdea() throws SQLException {

        // Given
        Idee idee = IdeesRepository.getIdeasOf(firefox.id)
                                   .stream()
                                   .filter(i -> !i.isASurprise())
                                   .findFirst()
                                   .orElseThrow(SQLException::new);
        when(request.getParameter(ServiceGetIdea.IDEA_ID_PARAM)).thenReturn(idee.getId() + "");

        // Act
        DecoratedWebAppIdeaServiceResponse answer = doTestServiceGet(DecoratedWebAppIdeaServiceResponse.class);

        // Check
        assertTrue(answer.isOK());
        assertEquals(idee, answer.getMessage().getIdee());
    }

    @Test
    public void itIsNotPossibleToGetOurOwnSurprise() throws SQLException {

        // Given
        Idee idee = IdeesRepository.getIdeasOf(firefox.id)
                                   .stream()
                                   .filter(Idee::isASurprise)
                                   .findFirst()
                                   .orElseThrow(SQLException::new);
        when(request.getParameter(ServiceGetIdea.IDEA_ID_PARAM)).thenReturn(idee.getId() + "");

        // Act
        StringServiceResponse answer = doTestServiceGet(StringServiceResponse.class);

        // Check
        assertFalse(answer.isOK());
        assertEquals("Non mais non... Où est le suspens ?", answer.getMessage());
    }

    protected static class DecoratedWebAppIdeaServiceResponse extends ServiceResponse<DecoratedWebAppIdea> {
        /**
         * Class constructor.
         *
         * @param isOK    True if there is no error.
         * @param idea    The JSon response message.
         * @param isAdmin Whether the user is an admin.
         */
        public DecoratedWebAppIdeaServiceResponse(boolean isOK, DecoratedWebAppIdea idea, boolean isAdmin, User connectedUser) {
            super(isOK, idea, isAdmin, connectedUser);
        }
    }
}