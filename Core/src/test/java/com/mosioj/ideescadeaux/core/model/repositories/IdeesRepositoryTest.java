package com.mosioj.ideescadeaux.core.model.repositories;

import com.mosioj.ideescadeaux.core.TemplateTest;
import com.mosioj.ideescadeaux.core.model.entities.Categorie;
import com.mosioj.ideescadeaux.core.model.entities.Idee;
import com.mosioj.ideescadeaux.core.model.entities.Priority;
import org.junit.Test;

import java.sql.SQLException;

public class IdeesRepositoryTest extends TemplateTest {

    @Test
    public void testAddSmiley() throws SQLException {
        String text = "😀😑😎😣😣😲😩😨🤢🤮🚛🚑🛫✈";
        Priority p = PrioritiesRepository.getPriority(5).orElseThrow(SQLException::new);
        Categorie c = CategoriesRepository.getCategory("bd").orElseThrow(SQLException::new);
        Idee idee = IdeesRepository.saveTheIdea(Idee.builder()
                                                    .withOwner(firefox)
                                                    .withText(text)
                                                    .withCategory(c)
                                                    .withPriority(p));
        IdeesRepository.trueRemove(idee);
    }
}