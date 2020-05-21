package com.mosioj.ideescadeaux.webapp.entities;

import com.mosioj.ideescadeaux.core.model.entities.BookingInformation;
import com.mosioj.ideescadeaux.core.model.entities.Idee;
import com.mosioj.ideescadeaux.webapp.servlets.AbstractTestServletWebApp;
import com.mosioj.ideescadeaux.webapp.servlets.controllers.idees.AfficherListes;
import org.junit.Assert;
import org.junit.Test;

public class DecoratedWebAppIdeaTest extends AbstractTestServletWebApp {

    public DecoratedWebAppIdeaTest() {
        super(new AfficherListes()); // Osef, juste utile pour le mock device
    }

    @Test
    public void testEnrichissement() {

        BookingInformation bi = BookingInformation.fromASingleUser(friendOfFirefox, null);
        Idee idee = new Idee(1, firefox, "tutu", null, null, null, null, null, bi);

        DecoratedWebAppIdea decorated = new DecoratedWebAppIdea(idee, friendOfFirefox, device);
        Assert.assertEquals("booked_by_me_idea", decorated.getDisplayClass());
    }
}