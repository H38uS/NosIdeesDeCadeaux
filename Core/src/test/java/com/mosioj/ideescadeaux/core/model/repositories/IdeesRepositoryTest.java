package com.mosioj.ideescadeaux.core.model.repositories;

import com.mosioj.ideescadeaux.core.TemplateTest;
import com.mosioj.ideescadeaux.core.model.entities.Categorie;
import com.mosioj.ideescadeaux.core.model.entities.IdeaGroup;
import com.mosioj.ideescadeaux.core.model.entities.Priority;
import com.mosioj.ideescadeaux.core.model.entities.text.Idee;
import org.junit.Test;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;

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

    @Test
    public void testMultipleGroupParticipationMustStillReturnOneIdea() {
        // Given one idea with a group booked by 2 persons
        Idee idee = IdeesRepository.saveTheIdea(Idee.builder().withText("une super idée").withOwner(firefox));
        IdeaGroup group = GroupIdeaRepository.createAGroup(100, 50, friendOfFirefox);
        GroupIdeaContentRepository.addNewAmount(group, moiAutre, 20);
        IdeesRepository.bookByGroup(idee, group);
        assertEquals(2,
                     GroupIdeaRepository.getGroupDetails(group.getId())
                                        .map(IdeaGroup::getShares)
                                        .orElse(Collections.emptySet())
                                        .size());

        // When
        List<Idee> ideas = IdeesRepository.getIdeasOf(firefox).stream().filter(i -> idee.getId() == i.getId()).toList();

        // Then
        IdeesRepository.trueRemove(idee);
        assertEquals(1, ideas.size());
    }
}