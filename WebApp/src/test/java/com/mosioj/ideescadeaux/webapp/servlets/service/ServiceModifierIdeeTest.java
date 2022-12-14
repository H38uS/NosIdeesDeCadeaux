package com.mosioj.ideescadeaux.webapp.servlets.service;

import com.mosioj.ideescadeaux.core.model.entities.Priority;
import com.mosioj.ideescadeaux.core.model.entities.notifications.NType;
import com.mosioj.ideescadeaux.core.model.entities.notifications.Notification;
import com.mosioj.ideescadeaux.core.model.entities.text.Idee;
import com.mosioj.ideescadeaux.core.model.repositories.IdeesRepository;
import com.mosioj.ideescadeaux.core.model.repositories.IsUpToDateQuestionsRepository;
import com.mosioj.ideescadeaux.core.model.repositories.NotificationsRepository;
import com.mosioj.ideescadeaux.core.model.repositories.PrioritiesRepository;
import com.mosioj.ideescadeaux.core.utils.db.HibernateUtil;
import com.mosioj.ideescadeaux.webapp.servlets.AbstractTestServletWebApp;
import com.mosioj.ideescadeaux.webapp.servlets.StringServiceResponse;
import com.mosioj.ideescadeaux.webapp.servlets.controllers.idees.modification.ModifyIdea;
import org.junit.Test;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.mosioj.ideescadeaux.core.model.entities.notifications.NType.IDEA_ADDED_BY_FRIEND;
import static com.mosioj.ideescadeaux.core.model.entities.notifications.NType.IS_IDEA_UP_TO_DATE;
import static org.junit.Assert.*;

public class ServiceModifierIdeeTest extends AbstractTestServletWebApp {

    public ServiceModifierIdeeTest() {
        super(new ServiceModifierIdee());
    }

    @Test
    public void modifyingOurIdeaIsAllowed() throws SQLException, IOException {

        Idee idee = IdeesRepository.getIdeasOf(firefox).stream().findFirst().orElseThrow(SQLException::new);
        Priority prio = PrioritiesRepository.getPriorities().stream().findFirst().orElse(null);
        assert prio != null;
        final String initialText = idee.getText();

        createMultiPartRequest(Map.of("text",
                                      initialText + "aa",
                                      "type",
                                      "",
                                      "priority",
                                      String.valueOf(prio.getId())));

        bindPostRequestParam(ServiceModifierIdee.IDEE_ID_PARAM, String.valueOf(idee.getId()));
        StringServiceResponse resp = doTestServicePost();

        assertTrue(resp.isOK());
        idee = IdeesRepository.getIdea(idee.getId()).orElseThrow(SQLException::new);
        assertEquals(initialText + "aa", idee.getText());
    }

    @Test
    public void shouldNotBePossibleToModifySomeonesElseIdea() throws SQLException, IOException {

        Idee idee = IdeesRepository.getIdeasOf(friendOfFirefox)
                                   .stream()
                                   .filter(i -> !i.isASurprise())
                                   .findFirst()
                                   .orElseThrow(SQLException::new);
        final String initialText = idee.getText();

        Map<String, String> param = new HashMap<>();
        param.put("text", initialText + "aa");
        param.put("type", "");
        param.put("priority", String.valueOf(idee.getPriority().getId()));
        createMultiPartRequest(param);

        bindPostRequestParam(ServiceModifierIdee.IDEE_ID_PARAM, String.valueOf(idee.getId()));
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

        bindPostRequestParam(ServiceModifierIdee.IDEE_ID_PARAM, String.valueOf(-42));
        StringServiceResponse resp = doTestServicePost();

        assertFalse(resp.isOK());
        assertEquals("Aucune idée trouvée en paramètre.", resp.getMessage());
        assertFalse(IdeesRepository.getIdea(-42).isPresent());
    }

    @Test
    public void testModifyRemovesCorrectNotification() throws SQLException, IOException {

        int id = ds.selectInt("select max(id) from IDEES where owner = ? and status <> 'DELETED'", _OWNER_ID_).orElseThrow(SQLException::new);
        String newText = "Idee modifiee le " + new Date();
        Idee idee = IdeesRepository.getIdea(id).orElseThrow(SQLException::new);
        assertNotEquals(newText, idee.getText());

        Notification notifId = IS_IDEA_UP_TO_DATE.with(friendOfFirefox, idee).sendItTo(firefox);
        IsUpToDateQuestionsRepository.addAssociation(idee.getId(), friendOfFirefox.getId());
        assertTrue(IsUpToDateQuestionsRepository.associationExists(idee, friendOfFirefox));
        Notification addByFriend = IDEA_ADDED_BY_FRIEND.with(moiAutre, idee).sendItTo(firefox);
        assertNotifDoesExists(notifId);
        assertNotifDoesExists(addByFriend);

        Map<String, String> param = new HashMap<>();
        param.put("text", newText);
        param.put("type", "");
        param.put("priority", 2 + "");
        createMultiPartRequest(param);
        bindPostRequestParam(ModifyIdea.IDEE_ID_PARAM, id + "");
        StringServiceResponse resp = doTestServicePost();

        assertTrue(resp.isOK());
        idee = IdeesRepository.getIdea(id).orElseThrow(SQLException::new);
        assertEquals(newText, idee.getText());
        assertNotifDoesNotExists(notifId);
        assertNotifDoesNotExists(addByFriend);
        assertFalse(IsUpToDateQuestionsRepository.associationExists(idee, friendOfFirefox));
    }

    @Test
    public void testModifyIdeaTwiceWithBirthdaySoonShouldSendOnlyOneNotification() throws SQLException, IOException {

        // Given the users birthday is in 4 days...
        firefox.setBirthday(LocalDate.now().plusDays(4));
        HibernateUtil.update(firefox);

        // ... and the friend has no notifications yet, and notification activated
        NotificationsRepository.terminator().whereOwner(friendOfFirefox).terminates();
        assertFalse(NotificationsRepository.fetcher()
                                           .whereOwner(friendOfFirefox)
                                           .whereType(NType.MODIFIED_IDEA_BIRTHDAY_SOON)
                                           .whereUser(firefox)
                                           .hasAny());

        // ... and the user has an idea and a modification form
        int id = ds.selectInt("select max(id) from IDEES where owner = ? and status <> 'DELETED'", _OWNER_ID_).orElseThrow(SQLException::new);
        Map<String, String> param = new HashMap<>();
        param.put("text", "test notif when birthday is close");
        param.put("type", "");
        param.put("priority", 2 + "");
        bindPostRequestParam(ModifyIdea.IDEE_ID_PARAM, id + "");

        // Then a first modification creates a notification
        createMultiPartRequest(param);
        StringServiceResponse resp = doTestServicePost();

        assertTrue(resp.isOK());
        assertEquals(1,
                     NotificationsRepository.fetcher()
                                            .whereOwner(friendOfFirefox)
                                            .whereType(NType.MODIFIED_IDEA_BIRTHDAY_SOON)
                                            .whereUser(firefox)
                                            .fetch()
                                            .size());

        // A second does not
        createMultiPartRequest(param);
        doTestPost();
        assertEquals(1,
                     NotificationsRepository.fetcher()
                                            .whereOwner(friendOfFirefox)
                                            .whereType(NType.MODIFIED_IDEA_BIRTHDAY_SOON)
                                            .whereUser(firefox)
                                            .fetch()
                                            .size());
    }

}