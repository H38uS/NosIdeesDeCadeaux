package com.mosioj.ideescadeaux.core.model.repositories;

import com.mosioj.ideescadeaux.core.TemplateTest;
import org.junit.Test;

import java.sql.SQLException;

public class IdeesRepositoryTest extends TemplateTest {

    @Test
    public void testAddSmiley() throws SQLException {
        String text = "😀😑😎😣😣😲😩😨🤢🤮🚛🚑🛫✈";
        int id = IdeesRepository.addIdea(firefox, text, "", 0, "", null, null);
        IdeesRepository.remove(id);
    }
}