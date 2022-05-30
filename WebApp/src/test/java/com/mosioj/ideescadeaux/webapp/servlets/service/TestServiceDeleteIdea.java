package com.mosioj.ideescadeaux.webapp.servlets.service;

import com.mosioj.ideescadeaux.core.model.entities.BookingInformation;
import com.mosioj.ideescadeaux.core.model.entities.IdeaGroup;
import com.mosioj.ideescadeaux.core.model.entities.Idee;
import com.mosioj.ideescadeaux.core.model.entities.Priority;
import com.mosioj.ideescadeaux.core.model.notifications.NType;
import com.mosioj.ideescadeaux.core.model.repositories.GroupIdeaRepository;
import com.mosioj.ideescadeaux.core.model.repositories.IdeesRepository;
import com.mosioj.ideescadeaux.core.model.repositories.IsUpToDateQuestionsRepository;
import com.mosioj.ideescadeaux.core.model.repositories.PrioritiesRepository;
import com.mosioj.ideescadeaux.webapp.servlets.AbstractTestServletWebApp;
import org.junit.Test;

import java.sql.SQLException;

import static com.mosioj.ideescadeaux.core.model.notifications.NType.IDEA_ADDED_BY_FRIEND;
import static com.mosioj.ideescadeaux.core.model.notifications.NType.MODIFIED_IDEA_BIRTHDAY_SOON;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

public class TestServiceDeleteIdea extends AbstractTestServletWebApp {

    public TestServiceDeleteIdea() {
        super(new ServiceDeleteIdea());
    }

    @Test
    public void testDelete() throws SQLException {

        Priority p = PrioritiesRepository.getPriority(5).orElseThrow(SQLException::new);
        Idee idee = IdeesRepository.saveTheIdea(Idee.builder()
                                                    .withOwner(firefox)
                                                    .withText("generated")
                                                    .withPriority(p));
        assertFalse(IdeesRepository.getDeletedIdea(idee.getId()).isPresent());

        when(request.getParameter(ServiceDeleteIdea.IDEE_ID_PARAM)).thenReturn(String.valueOf(idee.getId()));
        StringServiceResponse resp = doTestServicePost();

        assertTrue(resp.isOK());
        assertFalse(IdeesRepository.getIdea(idee.getId()).isPresent());
        assertTrue(IdeesRepository.getDeletedIdea(idee.getId()).isPresent());
    }

    @Test
    public void testDeleteWithGroupBooking() throws SQLException {

        // Creation de l'idée
        Priority p = PrioritiesRepository.getPriority(5).orElseThrow(SQLException::new);
        Idee idee = IdeesRepository.saveTheIdea(Idee.builder()
                                                    .withOwner(firefox)
                                                    .withText("generated")
                                                    .withPriority(p));
        assertEquals(1, ds.selectCountStar("select count(*) from IDEES where id = ?", idee.getId()));

        // Creation du groupe
        IdeaGroup group = GroupIdeaRepository.createAGroup(200, 10, moiAutre);
        IdeesRepository.bookByGroup(idee, group);
        // rafraichissement de l'idée
        idee = IdeesRepository.getIdea(idee.getId()).orElseThrow(SQLException::new);
        int notifId = NType.JOIN_GROUP.with(moiAutre, idee, group).sendItTo(friendOfFirefox);
        assertNotifDoesExists(notifId);
        assertEquals(GroupIdeaRepository.getGroupDetails(group.getId()),
                     idee.getBookingInformation().flatMap(BookingInformation::getBookingGroup));
        assertEquals(1, ds.selectCountStar("select count(*) from GROUP_IDEA where id = ?", group.getId()));
        assertEquals(1,
                     ds.selectCountStar("select count(*) from GROUP_IDEA_CONTENT where group_id = ?", group.getId()));

        // Suppression
        when(request.getParameter(ServiceDeleteIdea.IDEE_ID_PARAM)).thenReturn(String.valueOf(idee.getId()));
        StringServiceResponse resp = doTestServicePost();

        // Validation que cela supprime tout
        assertTrue(resp.isOK());
        assertNotifDoesNotExists(notifId);
        assertFalse(IdeesRepository.getIdea(idee.getId()).isPresent());
        // On conserve le groupe pour l'historique
        assertEquals(1, ds.selectCountStar("select count(*) from GROUP_IDEA where id = ?", group.getId()));
        assertEquals(1,
                     ds.selectCountStar("select count(*) from GROUP_IDEA_CONTENT where group_id = ?", group.getId()));
    }

    @Test
    public void testUnderlyingNotificationAreWellRemoved() throws SQLException {

        Priority p = PrioritiesRepository.getPriority(5).orElseThrow(SQLException::new);
        Idee idea = IdeesRepository.saveTheIdea(Idee.builder()
                                                    .withOwner(firefox)
                                                    .withText("generated")
                                                    .withPriority(p));
        assertEquals(1, ds.selectCountStar("select count(*) from IDEES where id = ?", idea.getId()));

        int isUpToDate = NType.IS_IDEA_UP_TO_DATE.with(friendOfFirefox, idea).sendItTo(firefox);
        IsUpToDateQuestionsRepository.addAssociation(idea.getId(), friendOfFirefox.getId());
        assertTrue(IsUpToDateQuestionsRepository.associationExists(idea, friendOfFirefox));
        int confirmedUpToDate = NType.CONFIRMED_UP_TO_DATE.with(firefox, idea).sendItTo(friendOfFirefox);
        int groupSuggestion = NType.GROUP_IDEA_SUGGESTION.with(firefox, idea, new IdeaGroup(0, 32))
                                                         .sendItTo(friendOfFirefox);
        int addByFriend = IDEA_ADDED_BY_FRIEND.with(moiAutre, idea).sendItTo(firefox);
        int modifiedWhenBDSoon = MODIFIED_IDEA_BIRTHDAY_SOON.with(firefox, idea).sendItTo(friendOfFirefox);
        int newComment = NType.NEW_COMMENT_ON_IDEA.with(firefox, idea).sendItTo(firefox);
        int newQuestion = NType.NEW_QUESTION_TO_OWNER.with(friendOfFirefox, idea).sendItTo(firefox);
        int recurentUnbook = NType.RECURENT_IDEA_UNBOOK.with(firefox, idea).sendItTo(friendOfFirefox);
        assertTrue(isUpToDate > -1);
        assertNotifDoesExists(isUpToDate);
        assertNotifDoesExists(confirmedUpToDate);
        assertNotifDoesExists(groupSuggestion);
        assertNotifDoesExists(addByFriend);
        assertNotifDoesExists(modifiedWhenBDSoon);
        assertNotifDoesExists(newComment);
        assertNotifDoesExists(newQuestion);
        assertNotifDoesExists(recurentUnbook);/* Suppression*/
        when(request.getParameter(ServiceDeleteIdea.IDEE_ID_PARAM)).thenReturn(String.valueOf(idea.getId()));
        StringServiceResponse resp = doTestServicePost();
        assertTrue(resp.isOK());
        assertNotifDoesNotExists(isUpToDate);
        assertNotifDoesNotExists(confirmedUpToDate);
        assertNotifDoesNotExists(groupSuggestion);
        assertNotifDoesNotExists(addByFriend);
        assertNotifDoesNotExists(modifiedWhenBDSoon);
        assertNotifDoesNotExists(newComment);
        assertNotifDoesNotExists(newQuestion);
        assertNotifDoesNotExists(recurentUnbook);
        assertFalse(IsUpToDateQuestionsRepository.associationExists(idea, friendOfFirefox));
    }

    @Test
    public void shouldNotBePossibleToDeleteOurSurprise() throws SQLException {

        // Given
        Priority p = PrioritiesRepository.getPriority(5).orElseThrow(SQLException::new);
        Idee idee = IdeesRepository.saveTheIdea(Idee.builder()
                                                    .withOwner(firefox)
                                                    .withText("une surprise")
                                                    .withPriority(p)
                                                    .withSurpriseOwner(friendOfFirefox)
                                                    .withCreatedBy(friendOfFirefox));

        // Trying to delete it
        when(request.getParameter(ServiceDeleteIdea.IDEE_ID_PARAM)).thenReturn(String.valueOf(idee.getId()));
        StringServiceResponse resp = doTestServicePost();

        // Check
        assertFalse(resp.isOK());
        assertEquals("Impossible de modifier une surprise si ce n'est pas vous qui l'avez créée.", resp.getMessage());
        assertTrue(IdeesRepository.getIdea(idee.getId()).isPresent());

        // Delete it
        IdeesRepository.remove(idee);
    }

    @Test
    public void shouldBePossibleToDeleteOurSurprise() throws SQLException {

        // Given
        Priority p = PrioritiesRepository.getPriority(5).orElseThrow(SQLException::new);
        Idee idee = IdeesRepository.saveTheIdea(Idee.builder()
                                                    .withOwner(friendOfFirefox)
                                                    .withText("une surprise")
                                                    .withPriority(p)
                                                    .withSurpriseOwner(firefox)
                                                    .withCreatedBy(firefox));

        // Trying to delete it
        when(request.getParameter(ServiceDeleteIdea.IDEE_ID_PARAM)).thenReturn(String.valueOf(idee.getId()));
        StringServiceResponse resp = doTestServicePost();

        // Check
        assertTrue(resp.isOK());
        assertFalse(IdeesRepository.getIdea(idee.getId()).isPresent());
    }
}
