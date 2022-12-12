package com.mosioj.ideescadeaux.core.model.repositories;

import com.mosioj.ideescadeaux.core.TemplateTest;
import com.mosioj.ideescadeaux.core.model.entities.IdeaGroup;
import com.mosioj.ideescadeaux.core.model.entities.notifications.NType;
import com.mosioj.ideescadeaux.core.model.entities.notifications.Notification;
import com.mosioj.ideescadeaux.core.model.entities.text.Idee;
import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.assertEquals;

public class NotificationsRepositoryTest extends TemplateTest {

    @Test
    public void shouldRightlyDetectNotificationAssertion() {

        // Given this notification
        Idee idee = IdeesRepository.getIdeasOf(firefox).stream().findFirst().orElse(null);
        IdeaGroup group = GroupIdeaRepository.getGroupDetails(2740)
                                             .or(() -> Optional.of(GroupIdeaRepository.createAGroup(300, 50, firefox)))
                                             .get();
        Notification suggestion = NType.GROUP_IDEA_SUGGESTION.with(firefox, idee, group).setOwner(firefox);
        NotificationsRepository.findNotificationsMatching(suggestion)
                               .forEach(NotificationsRepository::remove);

        // Nothing before
        assertEquals(0, NotificationsRepository.findNotificationsMatching(suggestion).size());

        // Insert it
        suggestion.sendItTo(firefox);

        // Should be there!
        assertEquals(1, NotificationsRepository.findNotificationsMatching(suggestion).size());
    }
}