package com.mosioj.ideescadeaux.webapp.utils;

import com.mosioj.ideescadeaux.core.model.entities.Idee;
import com.mosioj.ideescadeaux.core.model.entities.User;
import com.mosioj.ideescadeaux.webapp.servlets.IdeesCadeauxServlet;
import org.junit.Assert;
import org.junit.Test;

import java.sql.SQLException;

public class TestIdeesCadeauxServletUtilities {

    @Test
    public void testEnrichissement() throws SQLException {
        User owner = new User(1, "toto", "tutu@fneihfe.com", null);
        User booker = new User(32, "moi", "moi", null);
        Idee idee = new Idee(1, owner, "tutu", booker, null, null, null, null, null, null);
        IdeesCadeauxServlet.fillAUserIdea(booker, idee, false);
        Assert.assertEquals("booked_by_me_idea", idee.displayClass);
    }
}
