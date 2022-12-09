package com.mosioj.ideescadeaux.webapp.servlets.instance;

import com.mosioj.ideescadeaux.core.model.repositories.UsersRepository;
import com.mosioj.ideescadeaux.webapp.servlets.AbstractTestServletWebApp;
import com.mosioj.ideescadeaux.webapp.servlets.controllers.compte.CreationCompte;
import org.junit.Test;

import java.sql.SQLException;

import static org.junit.Assert.assertEquals;

public class TestCreationCompteWebApp extends AbstractTestServletWebApp {

    public TestCreationCompteWebApp() {
        super(new CreationCompte());
    }

    @Test
    public void testEmptyParameters() {
        // Should not throw an exception
        doTestPost();
    }

    @Test
    public void testSuccess() {

        UsersRepository.getUser("tartenpiontoto@hotmaildzndqudn.fr").ifPresent(u -> {
            try {
                UsersRepository.deleteUser(u);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
        ds.executeUpdate("delete from USERS where email = ?", "tartenpiontoto@hotmaildzndqudn.fr");
        assertEquals(0,
                     ds.selectCountStar("select count(*) from USERS where email = ?",
                                        "tartenpiontoto@hotmaildzndqudn.fr"));

        bindPostRequestParam("email", "tartenpiontoto@hotmaildzndqudn.fr");
        bindPostRequestParam("pwd", "mydummypwd");

        // Should not throw an exception
        doTestPost();

        // Success
        assertEquals(1,
                     ds.selectCountStar("select count(*) from USERS where email = ?",
                                        "tartenpiontoto@hotmaildzndqudn.fr"));
    }
}
