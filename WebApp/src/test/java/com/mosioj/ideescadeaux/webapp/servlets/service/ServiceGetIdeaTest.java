package com.mosioj.ideescadeaux.webapp.servlets.service;

import com.mosioj.ideescadeaux.core.model.entities.Idee;
import com.mosioj.ideescadeaux.core.model.entities.User;
import com.mosioj.ideescadeaux.core.model.repositories.IdeesRepository;
import com.mosioj.ideescadeaux.webapp.entities.DecoratedWebAppIdea;
import com.mosioj.ideescadeaux.webapp.servlets.AbstractTestServletWebApp;
import com.mosioj.ideescadeaux.webapp.servlets.StringServiceResponse;
import com.mosioj.ideescadeaux.webapp.servlets.service.response.ServiceResponse;
import org.junit.Test;

import java.sql.SQLException;

import static org.junit.Assert.*;

public class ServiceGetIdeaTest extends AbstractTestServletWebApp {

    public ServiceGetIdeaTest() {
        super(new ServiceGetIdea());
    }

    @Test
    public void itIsPossibleToGetAFriendIdea() throws SQLException {

        // Given
        Idee idee = IdeesRepository.getIdeasOf(friendOfFirefox)
                                   .stream()
                                   .filter(i -> !i.isASurprise())
                                   .findFirst()
                                   .orElseThrow(SQLException::new);
        bindRequestParam(ServiceGetIdea.IDEA_ID_PARAM, idee.getId() + "");

        // Act
        DecoratedWebAppIdeaServiceResponse answer = doTestServiceGet(DecoratedWebAppIdeaServiceResponse.class);

        // Check
        assertTrue(answer.isOK());
        assertEquals(idee, answer.getMessage().getIdee());
    }

    @Test
    public void itIsPossibleToGetAFriendSurpriseIdea() throws SQLException {

        // Given
        Idee idee = IdeesRepository.getIdeasOf(friendOfFirefox)
                                   .stream()
                                   .filter(Idee::isASurprise)
                                   .findFirst()
                                   .orElseThrow(SQLException::new);
        bindRequestParam(ServiceGetIdea.IDEA_ID_PARAM, idee.getId() + "");

        // Act
        DecoratedWebAppIdeaServiceResponse answer = doTestServiceGet(DecoratedWebAppIdeaServiceResponse.class);

        // Check
        assertTrue(answer.isOK());
        assertEquals(idee, answer.getMessage().getIdee());
    }

    @Test
    public void itIsPossibleToGetOurIdea() throws SQLException {

        // Given
        Idee idee = IdeesRepository.getIdeasOf(firefox)
                                   .stream()
                                   .filter(i -> !i.isASurprise())
                                   .findFirst()
                                   .orElseThrow(SQLException::new);
        bindRequestParam(ServiceGetIdea.IDEA_ID_PARAM, idee.getId() + "");

        // Act
        DecoratedWebAppIdeaServiceResponse answer = doTestServiceGet(DecoratedWebAppIdeaServiceResponse.class);

        // Check
        assertTrue(answer.isOK());
        assertEquals(idee, answer.getMessage().getIdee());
    }

    @Test
    public void itIsNotPossibleToGetOurOwnSurprise() throws SQLException {

        // Given
        Idee idee = IdeesRepository.getIdeasOf(firefox)
                                   .stream()
                                   .filter(Idee::isASurprise)
                                   .findFirst()
                                   .orElseThrow(SQLException::new);
        bindRequestParam(ServiceGetIdea.IDEA_ID_PARAM, idee.getId() + "");

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
         * @param isOK True if there is no error.
         * @param idea The JSon response message.
         */
        public DecoratedWebAppIdeaServiceResponse(boolean isOK, DecoratedWebAppIdea idea, User connectedUser) {
            super(isOK, idea, connectedUser);
        }
    }
}