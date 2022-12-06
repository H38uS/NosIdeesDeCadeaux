package com.mosioj.ideescadeaux.core.model;

import com.mosioj.ideescadeaux.core.TemplateTest;
import com.mosioj.ideescadeaux.core.model.entities.BookingInformation;
import com.mosioj.ideescadeaux.core.model.entities.IdeaGroup;
import com.mosioj.ideescadeaux.core.model.entities.Idee;
import com.mosioj.ideescadeaux.core.model.entities.notifications.NType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import java.text.MessageFormat;

public class TestNotificationFactory extends TemplateTest {

    /** Class logger. */
    private final Logger logger = LogManager.getLogger(TestNotificationFactory.class);

    @Test
    public void test() {
        for (NType type : NType.values()) {
            logger.info(MessageFormat.format("Testing creation of type: {0}", type));
            final IdeaGroup group = new IdeaGroup(42, 300);
            type.with(firefox,
                      Idee.builder()
                          .withId(35)
                          .withText("Toto à la plage")
                          .withBookingInformation(BookingInformation.fromAGroup(group, null))
                          .build(),
                      group);
        }
    }

}
