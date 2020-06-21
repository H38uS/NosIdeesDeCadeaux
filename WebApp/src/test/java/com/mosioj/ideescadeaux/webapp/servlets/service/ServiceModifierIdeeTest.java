package com.mosioj.ideescadeaux.webapp.servlets.service;

import com.mosioj.ideescadeaux.core.model.entities.Idee;
import com.mosioj.ideescadeaux.core.model.repositories.IdeesRepository;
import com.mosioj.ideescadeaux.webapp.servlets.AbstractTestServletWebApp;
import org.junit.Test;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

public class ServiceModifierIdeeTest extends AbstractTestServletWebApp {

    public ServiceModifierIdeeTest() {
        super(new ServiceModifierIdee());
    }

    @Test
    public void modifyingOurIdeaIsAllowed() throws SQLException, IOException {

        Idee idee = IdeesRepository.getIdeasOf(_OWNER_ID_).stream().findFirst().orElseThrow(SQLException::new);
        final String initialText = idee.getText();

        Map<String, String> param = new HashMap<>();
        param.put("text", initialText + "aa");
        param.put("type", "");
        param.put("priority", String.valueOf(idee.getPriorite().getId()));
        createMultiPartRequest(param);

        when(request.getParameter(ServiceModifierIdee.IDEE_ID_PARAM)).thenReturn(String.valueOf(idee.getId()));
        StringServiceResponse resp = doTestServicePost();

        assertTrue(resp.isOK());
        idee = IdeesRepository.getIdea(idee.getId()).orElseThrow(SQLException::new);
        assertEquals(initialText + "aa", idee.getText());
    }

    @Test
    public void shouldNotBePossibleToModifySomeonesElseIdea() throws SQLException, IOException {

        Idee idee = IdeesRepository.getIdeasOf(_FRIEND_ID_)
                                   .stream()
                                   .filter(i -> !i.isASurprise())
                                   .findFirst()
                                   .orElseThrow(SQLException::new);
        final String initialText = idee.getText();

        Map<String, String> param = new HashMap<>();
        param.put("text", initialText + "aa");
        param.put("type", "");
        param.put("priority", String.valueOf(idee.getPriorite().getId()));
        createMultiPartRequest(param);

        when(request.getParameter(ServiceModifierIdee.IDEE_ID_PARAM)).thenReturn(String.valueOf(idee.getId()));
        StringServiceResponse resp = doTestServicePost();

        assertFalse(resp.isOK());
        assertEquals("Vous ne pouvez modifier que vos idées ou celles de vos enfants.", resp.getMessage());
        idee = IdeesRepository.getIdea(idee.getId()).orElseThrow(SQLException::new);
        assertEquals(initialText, idee.getText());
    }

    @Test
    public void shouldNotBePossibleToModifyUnexistingIdea() throws IOException {

        Map<String, String> param = new HashMap<>();
        param.put("text", "aa");
        param.put("type", "");
        param.put("priority", String.valueOf(2));
        createMultiPartRequest(param);

        when(request.getParameter(ServiceModifierIdee.IDEE_ID_PARAM)).thenReturn(String.valueOf(-42));
        StringServiceResponse resp = doTestServicePost();

        assertFalse(resp.isOK());
        assertEquals("Aucune idée trouvée en paramètre.", resp.getMessage());
        assertFalse(IdeesRepository.getIdea(-42).isPresent());
    }

}