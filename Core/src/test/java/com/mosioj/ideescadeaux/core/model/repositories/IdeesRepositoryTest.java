package com.mosioj.ideescadeaux.core.model.repositories;

import com.mosioj.ideescadeaux.core.TemplateTest;
import com.mosioj.ideescadeaux.core.model.entities.Idee;
import org.junit.Test;

import java.sql.SQLException;

public class IdeesRepositoryTest extends TemplateTest {

    @Test
    public void testAddSmiley() throws SQLException {
        String text = "😀😑😎😣😣😲😩😨🤢🤮🚛🚑🛫✈";
        Idee idee = IdeesRepository.addIdea(firefox, text, "", 0, "", null, null);
        IdeesRepository.remove(idee);
    }
}