package com.mosioj.ideescadeaux.webapp.servlets.service;

import com.mosioj.ideescadeaux.core.model.entities.Idee;
import com.mosioj.ideescadeaux.core.model.entities.User;
import com.mosioj.ideescadeaux.webapp.entities.DecoratedWebAppIdea;
import com.mosioj.ideescadeaux.webapp.entities.OwnerIdeas;
import com.mosioj.ideescadeaux.webapp.servlets.AbstractTestServletWebApp;
import com.mosioj.ideescadeaux.webapp.servlets.service.response.PagedResponse;
import com.mosioj.ideescadeaux.webapp.servlets.service.response.ServiceResponse;
import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

public class ServiceVoirListeTest extends AbstractTestServletWebApp {

    public ServiceVoirListeTest() {
        super(new ServiceVoirListe());
    }

    @Test
    public void shouldBePossibleToViewMyIdeas() {

        when(session.getAttribute("connected_user")).thenReturn(moiAutre);
        when(request.getParameter(ServiceVoirListe.USER_ID_PARAM)).thenReturn(moiAutre.id + "");

        // Act
        VoirListeResponse answer = doTestServiceGet(VoirListeResponse.class);

        // Check
        assertTrue(answer.isOK());
        assertEquals(1, answer.getMessage().getTheContent().size());
        assertTrue(answer.getMessage().getTheContent().get(0).getIdeas().size() > 0);
    }

    @Test
    public void shouldNotBePossibleToViewMySurprises() {

        when(session.getAttribute("connected_user")).thenReturn(moiAutre);
        when(request.getParameter(ServiceVoirListe.USER_ID_PARAM)).thenReturn(moiAutre.id + "");

        // Act
        VoirListeResponse answer = doTestServiceGet(VoirListeResponse.class);

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
                                           .collect(Collectors.toList());
        assertTrue(surprises.isEmpty());
    }

    @Test
    public void shouldBePossibleToSeeFriendIdeasIncludingSurprises() {

        when(session.getAttribute("connected_user")).thenReturn(friendOfFirefox);
        when(request.getParameter(ServiceVoirListe.USER_ID_PARAM)).thenReturn(moiAutre.id + "");

        // Act
        VoirListeResponse answer = doTestServiceGet(VoirListeResponse.class);

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
                                           .collect(Collectors.toList());
        assertFalse(surprises.isEmpty());
    }

    private static class VoirListeResponse extends ServiceResponse<PagedResponse<List<OwnerIdeas>>> {
        /**
         * Class constructor.
         *
         * @param isOK          True if there is no error.
         * @param message       The JSon response message.
         * @param connectedUser The connected user or null if none.
         */
        public VoirListeResponse(boolean isOK,
                                 PagedResponse<List<OwnerIdeas>> message,
                                 User connectedUser) {
            super(isOK, message, connectedUser);
        }
    }
}