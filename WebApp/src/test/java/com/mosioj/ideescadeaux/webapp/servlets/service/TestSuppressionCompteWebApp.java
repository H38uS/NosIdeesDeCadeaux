package com.mosioj.ideescadeaux.webapp.servlets.service;

import com.mosioj.ideescadeaux.core.model.entities.User;
import com.mosioj.ideescadeaux.core.model.repositories.UsersRepository;
import com.mosioj.ideescadeaux.webapp.servlets.AbstractTestServletWebApp;
import org.junit.Assert;
import org.junit.Test;

import java.sql.SQLException;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

public class TestSuppressionCompteWebApp extends AbstractTestServletWebApp {

    public TestSuppressionCompteWebApp() {
        super(new ServiceSuppressionCompte());
    }

    @Test
    public void testSuccess() {

        when(session.getAttribute("connected_user")).thenReturn(theAdmin);
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

        when(request.getParameter(ServiceSuppressionCompte.USER_ID_PARAM)).thenReturn(String.format("%d", userId));

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

        when(request.getParameter(ServiceSuppressionCompte.USER_ID_PARAM)).thenReturn(userId + "");
        doTestPost();
        assertEquals(1, ds.selectCountStar("select count(*) from USERS where id = ?", userId));
        UsersRepository.deleteUser(user);
        assertEquals(0, ds.selectCountStar("select count(*) from USERS where id = ?", userId));
    }

}
