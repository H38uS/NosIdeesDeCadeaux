package com.mosioj.ideescadeaux.core.model.entities;

import com.mosioj.ideescadeaux.core.TemplateTest;
import com.mosioj.ideescadeaux.core.model.entities.notifications.NType;
import com.mosioj.ideescadeaux.core.model.entities.notifications.Notification;
import com.mosioj.ideescadeaux.core.model.entities.text.Idee;
import com.mosioj.ideescadeaux.core.utils.date.MyDateFormatViewer;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;

import java.time.LocalDateTime;

import static org.junit.Assert.assertEquals;

public class JSPAccessortTest extends TemplateTest {

    // TODO : à supprimer lorsque tout sera en JSON

    User u = firefox;
    public static final LocalDateTime LDT_20191212_0829 = LocalDateTime.of(2019, 12, 12, 8, 29);

    @Test
    public void testJSPOnlyUserMethods() {
        assertEquals(MyDateFormatViewer.formatOrElse(LocalDateTime.of(2017, 5, 21, 20, 42), StringUtils.EMPTY),
                     u.getCreationDate());
        assertEquals("", u.getLastLogin());
        Assert.assertFalse(u.hasSetUpAnAvatar());
        assertEquals("large/default.png", u.getAvatarSrcLarge());
    }

    @Test
    public void testJSPOnlyNotificationsMethod() {
        Notification n = new Notification();
        assertEquals("", n.getCreationTime());
    }

    @Test
    public void testJSPOnlyIdeeMethods() {
        Idee i = Idee.builder()
                     .withId(15)
                     .withOwner(u)
                     .withText("toto")
                     .withPicture("toto.png")
                     .withLastModificationDate(LDT_20191212_0829)
                     .build();
        assertEquals("small/toto.png", i.getImageSrcSmall());
        Assert.assertNull(i.getCategory());
    }

    @Test
    public void testJSPOnlyUserParameterMethods() {
        UserParameter up = new UserParameter(firefox, "NO_IDEA", "titi");
        assertEquals(NType.NO_IDEA.getDescription(), up.getParameterDescription());
        assertEquals("titi", up.getParameterValue());
        assertEquals(0, up.getId());
    }

}