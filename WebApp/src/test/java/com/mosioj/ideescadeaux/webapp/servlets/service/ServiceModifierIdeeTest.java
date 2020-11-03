package com.mosioj.ideescadeaux.webapp.servlets.service;

import com.mosioj.ideescadeaux.core.model.entities.Idee;
import com.mosioj.ideescadeaux.core.model.notifications.NotificationType;
import com.mosioj.ideescadeaux.core.model.notifications.ParameterName;
import com.mosioj.ideescadeaux.core.model.notifications.instance.NotifAskIfIsUpToDate;
import com.mosioj.ideescadeaux.core.model.notifications.instance.NotifIdeaAddedByFriend;
import com.mosioj.ideescadeaux.core.model.repositories.IdeesRepository;
import com.mosioj.ideescadeaux.core.model.repositories.IsUpToDateQuestionsRepository;
import com.mosioj.ideescadeaux.core.model.repositories.NotificationsRepository;
import com.mosioj.ideescadeaux.core.model.repositories.UsersRepository;
import com.mosioj.ideescadeaux.webapp.servlets.AbstractTestServletWebApp;
import com.mosioj.ideescadeaux.webapp.servlets.controllers.idees.modification.ModifyIdea;
import org.junit.Test;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
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

    @Test
    public void testModifyRemovesCorrectNotification() throws SQLException, IOException {

        int id = ds.selectInt("select max(id) from IDEES where owner = ?", _OWNER_ID_).orElseThrow(SQLException::new);
        String newText = "Idee modifiee le " + new Date();
        Idee idee = IdeesRepository.getIdea(id).orElseThrow(SQLException::new);
        assertNotEquals(newText, idee.getText());

        int notifId = NotificationsRepository.addNotification(_OWNER_ID_,
                                                              new NotifAskIfIsUpToDate(friendOfFirefox, idee));
        IsUpToDateQuestionsRepository.addAssociation(idee.getId(), friendOfFirefox.getId());
        assertTrue(IsUpToDateQuestionsRepository.associationExists(idee, friendOfFirefox));
        int addByFriend = NotificationsRepository.addNotification(_OWNER_ID_,
                                                                  new NotifIdeaAddedByFriend(moiAutre, idee));
        assertNotifDoesExists(notifId);
        assertNotifDoesExists(addByFriend);

        Map<String, String> param = new HashMap<>();
        param.put("text", newText);
        param.put("type", "");
        param.put("priority", 2 + "");
        createMultiPartRequest(param);
        when(request.getParameter(ModifyIdea.IDEE_ID_PARAM)).thenReturn(id + "");
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
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, 4);
        firefox.setBirthday(new java.sql.Date(cal.getTime().getTime()));

        UsersRepository.update(firefox);

        // ... and the friend has no notifications yet, and notification activated
        NotificationsRepository.removeAll(_FRIEND_ID_);
        assertEquals(0, NotificationsRepository.getNotifications(_FRIEND_ID_,
                                                                 NotificationType.IDEA_OF_FRIEND_MODIFIED_WHEN_BIRTHDAY_IS_SOON,
                                                                 ParameterName.USER_ID,
                                                                 _OWNER_ID_)
                                               .size());

        // ... and the user has an idea and a modification form
        int id = ds.selectInt("select max(id) from IDEES where owner = ?", _OWNER_ID_).orElseThrow(SQLException::new);
        Map<String, String> param = new HashMap<>();
        param.put("text", "test notif when birthday is close");
        param.put("type", "");
        param.put("priority", 2 + "");
        when(request.getParameter(ModifyIdea.IDEE_ID_PARAM)).thenReturn(id + "");

        // Then a first modification creates a notification
        createMultiPartRequest(param);
        StringServiceResponse resp = doTestServicePost();

        assertTrue(resp.isOK());
        assertEquals(1,
                     NotificationsRepository.getNotifications(_FRIEND_ID_,
                                                              NotificationType.IDEA_OF_FRIEND_MODIFIED_WHEN_BIRTHDAY_IS_SOON,
                                                              ParameterName.USER_ID,
                                                              _OWNER_ID_)
                                            .size());

        // A second does not
        createMultiPartRequest(param);
        doTestPost();
        assertEquals(1,
                     NotificationsRepository.getNotifications(_FRIEND_ID_,
                                                              NotificationType.IDEA_OF_FRIEND_MODIFIED_WHEN_BIRTHDAY_IS_SOON,
                                                              ParameterName.USER_ID,
                                                              _OWNER_ID_)
                                            .size());
    }

}