package com.mosioj.ideescadeaux.core.model.repositories;

import com.mosioj.ideescadeaux.core.TemplateTest;
import com.mosioj.ideescadeaux.core.model.entities.IdeaGroup;
import com.mosioj.ideescadeaux.core.model.entities.Idee;
import com.mosioj.ideescadeaux.core.model.notifications.instance.NotifGroupSuggestion;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class NotificationsRepositoryTest extends TemplateTest {

    @Test
    public void shouldRightlyDetectNotificationAssertion() {

        // Given this notification
        Idee idee = IdeesRepository.getIdeasOf(firefox.id).get(0);
        IdeaGroup group = new IdeaGroup(35, 200);
        NotifGroupSuggestion suggestion = new NotifGroupSuggestion(firefox, group.getId(), idee);
        NotificationsRepository.findNotificationMatching(firefox.id, suggestion)
                               .forEach(NotificationsRepository::remove);

        // Nothing before
        assertEquals(0, NotificationsRepository.findNotificationMatching(firefox.id, suggestion).size());

        // Insert it
        NotificationsRepository.addNotification(firefox.id, suggestion);

        // Should be there!
        assertEquals(1, NotificationsRepository.findNotificationMatching(firefox.id, suggestion).size());
    }
}