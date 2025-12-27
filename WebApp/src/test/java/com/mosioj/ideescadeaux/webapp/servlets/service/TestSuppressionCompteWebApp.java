package com.mosioj.ideescadeaux.webapp.servlets.service;

import com.mosioj.ideescadeaux.core.model.entities.User;
import com.mosioj.ideescadeaux.core.model.entities.text.Idee;
import com.mosioj.ideescadeaux.core.model.repositories.IdeesRepository;
import com.mosioj.ideescadeaux.core.model.repositories.UserRelationsRepository;
import com.mosioj.ideescadeaux.core.model.repositories.UsersRepository;
import com.mosioj.ideescadeaux.webapp.WebAppTemplateTest;
import com.mosioj.ideescadeaux.webapp.servlets.AbstractTestServletWebApp;
import com.mosioj.ideescadeaux.webapp.servlets.StringServiceResponse;
import org.junit.Assert;
import org.junit.Test;

import java.sql.SQLException;

import static org.junit.Assert.*;

public class TestSuppressionCompteWebApp extends AbstractTestServletWebApp {

    public TestSuppressionCompteWebApp() {
        super(new ServiceSuppressionCompte());
    }

    @Test
    public void testSuccess() {

        setConnectedUserTo(WebAppTemplateTest.theAdmin);
        assertTrue(theAdmin.isAdmin());

        UsersRepository.getUser("to_be_deleted@djizjdz.cekj").ifPresent(u -> {
            try {
                UsersRepository.deleteUser(u);
            } catch (SQLException e) {
                Assert.fail();
            }
        });
        int userId = UsersRepository.addNewPersonne("to_be_deleted@djizjdz.cekj", "a", "to_be_deleted");
        assertEquals(1, ds.selectCountStar("select count(*) from USERS where id = ?", userId));

        bindPostRequestParam(ServiceSuppressionCompte.USER_ID_PARAM, String.format("%d", userId));

        StringServiceResponse resp = doTestServicePost();

        assertTrue(resp.isOK());
        assertEquals(0, ds.selectCountStar("select count(*) from USERS where id = ?", userId));
    }

    @Test
    public void testNotAdmin() throws SQLException {

        assertFalse(firefox.isAdmin());
        UsersRepository.getUser("to_be_deleted@djizjdz.cekj").ifPresent(u -> {
            try {
                UsersRepository.deleteUser(u);
            } catch (SQLException e) {
                Assert.fail();
            }
        });
        int userId = UsersRepository.addNewPersonne("to_be_deleted@djizjdz.cekj", "a", "to_be_deleted");
        assertEquals(1, ds.selectCountStar("select count(*) from USERS where id = ?", userId));
        User user = UsersRepository.getUser(userId).orElseThrow(SQLException::new);

        bindPostRequestParam(ServiceSuppressionCompte.USER_ID_PARAM, userId + "");
        doTestPost();
        assertEquals(1, ds.selectCountStar("select count(*) from USERS where id = ?", userId));
        UsersRepository.deleteUser(user);
        assertEquals(0, ds.selectCountStar("select count(*) from USERS where id = ?", userId));
    }

    @Test
    public void testUserRemovalAlsoRemovesBooking() throws SQLException {
        // Given
        String mail = "to_be_deleted@something.cekj";
        UsersRepository.getUser(mail).ifPresent(u -> {
            try {
                UsersRepository.deleteUser(u);
            } catch (SQLException e) {
                Assert.fail();
            }
        });
        int userId = UsersRepository.addNewPersonne(mail, "a", "to_be_deleted");
        User user = UsersRepository.getUser(userId).orElseThrow(SQLException::new);
        UserRelationsRepository.addAssociation(user, firefox);
        Idee idea = IdeesRepository.saveTheIdea(new Idee.IdeaBuilder().withText("aa").withOwner(firefox));
        IdeesRepository.reserver(idea, user);
        assertNotNull(idea.bookedBy);

        // When
        bindPostRequestParam(ServiceSuppressionCompte.USER_ID_PARAM, String.format("%d", userId));
        setConnectedUserTo(WebAppTemplateTest.theAdmin);
        assertTrue(theAdmin.isAdmin());
        StringServiceResponse resp = doTestServicePost();

        // Then
        assertTrue(resp.isOK());
        assertEquals(0, ds.selectCountStar("select count(*) from USERS where id = ?", userId));
        assertNull(IdeesRepository.getIdea(idea.getId()).orElseThrow(SQLException::new).bookedBy);
        IdeesRepository.remove(idea);
    }

}
