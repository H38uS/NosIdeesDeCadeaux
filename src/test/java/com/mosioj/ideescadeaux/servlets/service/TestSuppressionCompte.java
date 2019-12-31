package com.mosioj.ideescadeaux.servlets.service;

import com.mosioj.ideescadeaux.model.entities.User;
import com.mosioj.ideescadeaux.model.repositories.UsersRepository;
import com.mosioj.ideescadeaux.servlets.AbstractTestServlet;
import org.junit.Test;

import java.sql.SQLException;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

public class TestSuppressionCompte extends AbstractTestServlet {

    public TestSuppressionCompte() {
        super(new ServiceSuppressionCompte());
    }

    @Test
    public void testSuccess() throws SQLException {

        when(request.isUserInRole("ROLE_ADMIN")).thenReturn(true);
        assertTrue(request.isUserInRole("ROLE_ADMIN"));

        int userId = UsersRepository.addNewPersonne("to_be_deleted@djizjdz.cekj", "a", "to_be_deleted");
        assertEquals(1, ds.selectCountStar("select count(*) from USERS where id = ?", userId));

        when(request.getParameter(ServiceSuppressionCompte.USER_ID_PARAM)).thenReturn(userId + "");

        StringServiceResponse resp = doTestServicePost();

        assertTrue(resp.isOK());
        assertEquals(0, ds.selectCountStar("select count(*) from USERS where id = ?", userId));
    }

    @Test
    public void testNotAdmin() throws SQLException {

        assertFalse(request.isUserInRole("ROLE_ADMIN"));

        int userId = UsersRepository.addNewPersonne("to_be_deleted@djizjdz.cekj", "a", "to_be_deleted");
        assertEquals(1, ds.selectCountStar("select count(*) from USERS where id = ?", userId));
        User user = UsersRepository.getUser(userId);

        when(request.getParameter(ServiceSuppressionCompte.USER_ID_PARAM)).thenReturn(userId + "");
        doTestPost();
        assertEquals(1, ds.selectCountStar("select count(*) from USERS where id = ?", userId));
        UsersRepository.deleteUser(user);
        assertEquals(0, ds.selectCountStar("select count(*) from USERS where id = ?", userId));
    }

}
