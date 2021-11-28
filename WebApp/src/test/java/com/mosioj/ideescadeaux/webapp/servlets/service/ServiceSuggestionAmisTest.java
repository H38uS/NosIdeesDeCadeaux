package com.mosioj.ideescadeaux.webapp.servlets.service;

import com.mosioj.ideescadeaux.core.model.repositories.UserRelationsSuggestionRepository;
import com.mosioj.ideescadeaux.webapp.servlets.AbstractTestServletWebApp;
import org.junit.Test;

import java.sql.SQLException;

public class ServiceSuggestionAmisTest extends AbstractTestServletWebApp {

    public ServiceSuggestionAmisTest() {
        super(new ServiceSuggestionAmis());
    }

    @Test
    public void listSuggestionAmis() throws SQLException {
        // Should not throw
        UserRelationsSuggestionRepository.getUserSuggestions(firefox);
    }

    // FIXME continuer les tests pour valider qu'on les supprime bien, qu'on crée bien les demandes etc.
}