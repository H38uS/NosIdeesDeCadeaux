package com.mosioj.ideescadeaux.core.model;

import com.mosioj.ideescadeaux.core.TemplateTest;
import com.mosioj.ideescadeaux.core.model.entities.text.Idee;
import org.junit.Assert;
import org.junit.Test;

public class TestIdee extends TemplateTest {

    @Test
    public void testGetSummary() {

        Idee idee = ideaFactory("toto");
        Assert.assertEquals("toto", idee.getTextSummary(4));
        Assert.assertEquals("...", idee.getTextSummary(3));

        idee = ideaFactory("totototo");
        Assert.assertEquals("totototo", idee.getTextSummary(8));
        Assert.assertEquals("toto...", idee.getTextSummary(7));

        idee = ideaFactory("toto < '6'");
        Assert.assertEquals("toto...", idee.getTextSummary(7));
        Assert.assertEquals("toto <...", idee.getTextSummary(9));

    }

    private Idee ideaFactory(String text) {
        return Idee.builder().withId(1).withText(text).build();
    }

}
