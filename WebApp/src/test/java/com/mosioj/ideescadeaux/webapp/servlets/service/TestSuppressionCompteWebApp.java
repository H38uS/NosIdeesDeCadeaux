package com.mosioj.ideescadeaux.webapp.servlets.service;

import com.mosioj.ideescadeaux.core.model.entities.User;
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
                e.printStackTrace();
                Assert.fail();
            }
        });
        int userId = UsersRepository.addNewPersonne("to_be_deleted@djizjdz.cekj", "a", "to_be_deleted");
        assertEquals(1, ds.selectCountStar("select count(*) from USERS where id = ?", userId));

        bindRequestParam(ServiceSuppressionCompte.USER_ID_PARAM, String.format("%d", userId));

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
                e.printStackTrace();
                Assert.fail();
            }
        });
        int userId = UsersRepository.addNewPersonne("to_be_deleted@djizjdz.cekj", "a", "to_be_deleted");
        assertEquals(1, ds.selectCountStar("select count(*) from USERS where id = ?", userId));
        User user = UsersRepository.getUser(userId).orElseThrow(SQLException::new);

        bindRequestParam(ServiceSuppressionCompte.USER_ID_PARAM, userId + "");
        doTestPost();
        assertEquals(1, ds.selectCountStar("select count(*) from USERS where id = ?", userId));
        UsersRepository.deleteUser(user);
        assertEquals(0, ds.selectCountStar("select count(*) from USERS where id = ?", userId));
    }

}
