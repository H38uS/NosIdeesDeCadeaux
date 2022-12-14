package com.mosioj.ideescadeaux.webapp.entities;

import com.mosioj.ideescadeaux.core.model.entities.text.Idee;
import com.mosioj.ideescadeaux.core.model.repositories.IdeesRepository;
import com.mosioj.ideescadeaux.webapp.repositories.IdeasWithInfoRepository;
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

        // Given
        Idee idee = IdeesRepository.saveTheIdea(Idee.builder().withId(1).withOwner(firefox).withText("tutu"));
        IdeesRepository.reserver(idee, friendOfFirefox);
        idee = IdeasWithInfoRepository.getIdea(idee.getId()).orElse(null);
        assert idee != null;

        // When
        DecoratedWebAppIdea decorated = new DecoratedWebAppIdea(idee, friendOfFirefox, getDevice());

        // Then
        Assert.assertEquals("booked_by_me_idea", decorated.getDisplayClass());

        IdeesRepository.trueRemove(idee);
    }
}