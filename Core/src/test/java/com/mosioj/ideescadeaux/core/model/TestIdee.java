package com.mosioj.ideescadeaux.core.model;

import com.mosioj.ideescadeaux.core.TemplateTest;
import com.mosioj.ideescadeaux.core.model.entities.Idee;
import com.mosioj.ideescadeaux.core.model.entities.User;
import com.mosioj.ideescadeaux.core.model.repositories.IdeesRepository;
import org.junit.Assert;
import org.junit.Test;

import java.sql.SQLException;

public class TestIdee extends TemplateTest {

    @Test
    public void testGetSummary() {

        Idee idee = ideaFactory("toto");
        Assert.assertEquals("toto", idee.getTextSummary(4));
        Assert.assertEquals("...", idee.getTextSummary(3));

        idee = ideaFactory("totototo");
        Assert.assertEquals("totototo", idee.getTextSummary(8));
        Assert.assertEquals("toto...", idee.getTextSummary(7));

        idee = ideaFactory("toto &lt; &quot;6&quot;");
        Assert.assertEquals("toto...", idee.getTextSummary(7));
        Assert.assertEquals("toto &lt;...", idee.getTextSummary(10));

    }

    @Test
    public void testEnrichissement() throws SQLException {
        User owner = new User(1, "toto", "tutu@fneihfe.com", null);
        User booker = new User(32, "moi", "moi", null);
        Idee idee = new Idee(1, owner, "tutu", booker, null, null, null, null, null, null);
        IdeesRepository.fillAUserIdea(booker, idee, false);
        Assert.assertEquals("booked_by_me_idea", idee.displayClass);
    }

    private Idee ideaFactory(String text) {
        return new Idee(1, null, text, null, null, null, null, null, null, null);
    }

}
