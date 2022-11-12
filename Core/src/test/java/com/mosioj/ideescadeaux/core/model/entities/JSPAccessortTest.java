package com.mosioj.ideescadeaux.core.model.entities;

import com.mosioj.ideescadeaux.core.TemplateTest;
import com.mosioj.ideescadeaux.core.model.notifications.NType;
import com.mosioj.ideescadeaux.core.utils.date.MyDateFormatViewer;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;

import java.sql.Timestamp;
import java.time.LocalDateTime;

public class JSPAccessortTest extends TemplateTest {

    // TODO : à supprimer lorsque tout sera en JSON

    User u = firefox;
    public static final Timestamp TS20191212_0829 = new Timestamp(1576135793001L);
    public static final LocalDateTime LDT_20191212_0829 = LocalDateTime.of(2019, 12, 12, 8, 29);

    @Test
    public void testJSPOnlyUserMethods() {
        Assert.assertEquals(MyDateFormatViewer.formatOrElse(LocalDateTime.of(2017, 5, 21, 20, 42), StringUtils.EMPTY),
                            u.getCreationDate());
        Assert.assertEquals("", u.getLastLogin());
        Assert.assertFalse(u.hasSetUpAnAvatar());
        Assert.assertEquals("large/default.png", u.getAvatarSrcLarge());
    }

    @Test
    public void testJSPOnlyCommentMethods() {
        Comment c = new Comment(42, "aa", u, 35, TS20191212_0829);
        Assert.assertEquals("12 décembre 2019 à 08h29", c.getTime());
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
        Assert.assertEquals("small/toto.png", i.getImageSrcSmall());
        Assert.assertNull(i.getCategory());
    }

    @Test
    public void testJSPOnlySousReservationEntityMethods() {
        SousReservationEntity sre = new SousReservationEntity(56, 14, u, "toto", TS20191212_0829);
        Assert.assertEquals("12 décembre 2019 à 08h29", sre.getBookedOn());
        Assert.assertEquals(14, sre.getIdeeId());
    }

    @Test
    public void testJSPOnlyUserParameterMethods() {
        UserParameter up = new UserParameter(firefox, "NO_IDEA", "titi");
        Assert.assertEquals(NType.NO_IDEA.getDescription(), up.getParameterDescription());
        Assert.assertEquals("titi", up.getParameterValue());
    }

}