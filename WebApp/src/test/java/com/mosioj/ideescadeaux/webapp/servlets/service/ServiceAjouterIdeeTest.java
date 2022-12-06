package com.mosioj.ideescadeaux.webapp.servlets.service;

import com.mosioj.ideescadeaux.core.model.entities.Idee;
import com.mosioj.ideescadeaux.core.model.notifications.NType;
import com.mosioj.ideescadeaux.core.model.repositories.IdeesRepository;
import com.mosioj.ideescadeaux.core.model.repositories.NotificationsRepository;
import com.mosioj.ideescadeaux.core.model.repositories.UserRelationsRepository;
import com.mosioj.ideescadeaux.webapp.servlets.AbstractTestServletWebApp;
import com.mosioj.ideescadeaux.webapp.servlets.StringServiceResponse;
import com.mosioj.ideescadeaux.webapp.servlets.controllers.idees.modification.AjouterIdeeAmi;
import org.junit.Test;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class ServiceAjouterIdeeTest extends AbstractTestServletWebApp {

    public ServiceAjouterIdeeTest() {
        super(new ServiceAjouterIdee());
    }

    @Test
    public void testAjouterIdeeAmisSuccess() throws IOException {

        int noIdea = NType.NO_IDEA.buildDefault().sendItTo(friendOfFirefox);
        assertNotifDoesExists(noIdea);

        Map<String, String> param = new HashMap<>();
        param.put("text", "Ajouté par un ami");
        param.put("type", "");
        param.put("priority", 2 + "");
        createMultiPartRequest(param);

        bindRequestParam(ServiceAjouterIdee.USER_PARAMETER, _FRIEND_ID_);
        StringServiceResponse resp = doTestServicePost();

        assertTrue(resp.isOK());
        assertNotifDoesNotExists(noIdea);
    }

    @Test
    public void testAjouterSurpriseAUnAmisSuccess() throws IOException {

        int noIdea = NType.NO_IDEA.buildDefault().sendItTo(friendOfFirefox);
        assertNotifDoesExists(noIdea);

        Map<String, String> param = new HashMap<>();
        param.put("text", "Ajouté par un ami");
        param.put("type", "");
        param.put("priority", 2 + "");
        param.put("est_surprise", "on");
        createMultiPartRequest(param);

        bindRequestParam(AjouterIdeeAmi.USER_PARAMETER, _FRIEND_ID_);
        StringServiceResponse resp = doTestServicePost();

        assertTrue(resp.isOK());
        assertNotifDoesExists(noIdea);
    }

    @Test
    public void testPostSuccess() throws IOException {

        int noIdea = NType.NO_IDEA.buildDefault().sendItTo(firefox);
        assertNotifDoesExists(noIdea);

        Map<String, String> param = new HashMap<>();
        param.put("text", "Ma super idée wouhouuuu");
        param.put("priority", "1");
        createMultiPartRequest(param);
        bindRequestParam(ServiceAjouterIdee.USER_PARAMETER, _OWNER_ID_);
        StringServiceResponse resp = doTestServicePost();

        assertTrue(resp.isOK());
        assertNotifDoesNotExists(noIdea);
    }

    @Test
    public void testShouldAutoConvertLinks() throws IOException, SQLException {

        Map<String, String> param = new HashMap<>();
        param.put("text",
                  "un lien https://www.liveffn.com/cgi-bin/resultats.php?competition=62933&langue=fra et voilà");
        param.put("priority", "1");
        createMultiPartRequest(param);
        bindRequestParam(ServiceAjouterIdee.USER_PARAMETER, _OWNER_ID_);
        StringServiceResponse resp = doTestServicePost();

        assertTrue(resp.isOK());
        int id = ds.selectInt("select max(id) from IDEES where owner = ?", _OWNER_ID_).orElseThrow(SQLException::new);
        Idee idee = IdeesRepository.getIdea(id).orElseThrow(SQLException::new);
        assertEquals(
                "<p>un lien <a rel=\"nofollow\" href=\"https://www.liveffn.com/cgi-bin/resultats.php?competition=62933&amp;langue=fra\">https://www.liveffn.com/cgi-bin/resultats.php?co[...]e=fra</a> et voilà</p>",
                idee.getHtml().trim());
    }

    @Test
    public void noBirthdaySetShouldNotNotifyFriends() throws IOException {

        // No birthday
        assertFalse(moiAutre.getBirthday().isPresent());
        assertFalse(UserRelationsRepository.getAllUsersInRelation(moiAutre).isEmpty());
        setConnectedUserTo(moiAutre);
        final int nbIdeas = IdeesRepository.getIdeasOf(moiAutre).size();

        // Removing previous notifications if any
        final NType birthdayIsSoon = NType.NEW_IDEA_BIRTHDAY_SOON;
        UserRelationsRepository.getAllUsersInRelation(moiAutre)
                               .stream()
                               .flatMap(u -> NotificationsRepository.getUserNotifications(u, birthdayIsSoon)
                                                                    .stream())
                               .forEach(NotificationsRepository::remove);

        Map<String, String> param = new HashMap<>();
        param.put("text", "Ma super idée wouhouuuu");
        param.put("priority", "1");
        createMultiPartRequest(param);
        bindRequestParam(ServiceAjouterIdee.USER_PARAMETER, moiAutre.getId());
        StringServiceResponse resp = doTestServicePost();

        assertTrue(resp.isOK());
        assertEquals(nbIdeas + 1, IdeesRepository.getIdeasOf(moiAutre).size());
        assertEquals(Collections.emptyList(), UserRelationsRepository.getAllUsersInRelation(moiAutre)
                                                                     .stream()
                                                                     .map(u -> NotificationsRepository.getUserNotifications(
                                                                             u,
                                                                             birthdayIsSoon))
                                                                     .filter(notifs -> !notifs.isEmpty())
                                                                     .collect(Collectors.toList()));
    }
}