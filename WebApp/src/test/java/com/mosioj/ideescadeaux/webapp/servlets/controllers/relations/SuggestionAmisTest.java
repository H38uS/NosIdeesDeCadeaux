package com.mosioj.ideescadeaux.webapp.servlets.controllers.relations;

import com.mosioj.ideescadeaux.core.model.entities.RelationSuggestion;
import com.mosioj.ideescadeaux.core.model.repositories.UserRelationsSuggestionRepository;
import com.mosioj.ideescadeaux.webapp.servlets.AbstractTestServletWebApp;
import org.junit.Test;

import java.sql.SQLException;
import java.util.List;

public class SuggestionAmisTest extends AbstractTestServletWebApp {

    public SuggestionAmisTest() {
        super(new SuggestionAmis());
    }

    @Test
    public void listSuggestionAmis() throws SQLException {
        // Should not throw
        List<RelationSuggestion> suggestion = UserRelationsSuggestionRepository.getUserSuggestions(firefox);
    }
}