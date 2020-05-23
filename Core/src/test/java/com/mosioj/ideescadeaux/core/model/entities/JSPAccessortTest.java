package com.mosioj.ideescadeaux.core.model.entities;

import com.mosioj.ideescadeaux.core.TemplateTest;
import org.junit.Assert;
import org.junit.Test;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;

public class JSPAccessortTest extends TemplateTest {

    // TODO : à supprimer lorsque tout sera en JSON

    User u = new User(42, "toto", "toto@tutu.gmail.fr", "none.png");
    public static final Timestamp TS20191212_0829 = new Timestamp(1576135793001L);

    @Test
    public void testJSPOnlyUserMethods() {
        Assert.assertFalse(u.getIsInMyNetwork());
        Assert.assertFalse(u.getHasBookedOneOfItsIdeas());
        Assert.assertEquals("", u.getCreationDate());
        Assert.assertEquals("", u.getLastLogin());
        Assert.assertTrue(u.hasSetUpAnAvatar());
        Assert.assertEquals("large/none.png", u.getAvatarSrcLarge());
        Assert.assertEquals(0, u.getNbDaysBeforeBirthday());
        Assert.assertNull(u.getFreeComment());
        Assert.assertEquals("- on ne sait pas...", u.getBirthdayAsString());
    }

    @Test
    public void testJSPOnlyCommentMethods() {
        Comment c = new Comment(42, "aa", u, 35, TS20191212_0829);
        Assert.assertEquals("12 décembre 2019 à 08h29", c.getTime());
    }

    @Test
    public void testJSPOnlyIdeaGroupMethods() {
        IdeaGroup group = new IdeaGroup(30, 23.03);
        Assert.assertEquals("23,03", group.getTotalAmount());
    }

    @Test
    public void testJSPOnlyIdeeMethods() {
        Idee i = new Idee(15,
                          u,
                          "toto",
                          null,
                          "toto.png",
                          null,
                          TS20191212_0829,
                          null,
                          null);
        Assert.assertEquals("small/toto.png", i.getImageSrcSmall());
        Assert.assertNull(i.getCategory());
    }

    @Test
    public void testJSPOnlyRelationMethods() {
        Relation r = new Relation(u, u);
        Assert.assertEquals(u, r.getFirst());
        Assert.assertFalse(r.getSecondIsInMyNetwork());
    }

    @Test
    public void testJSPOnlyRelationRequestMethods() {
        RelationRequest rr = new RelationRequest(u, u, new Date(1576135793001L));
        Assert.assertEquals(u, rr.getSent_by());
        Assert.assertEquals(u, rr.getSent_to());
        Assert.assertEquals(new Date(1576135793001L), rr.getRequest_date());
    }

    @Test
    public void testJSPOnlyRelationSuggestionMethods() {
        RelationSuggestion rs = new RelationSuggestion(u, u, new ArrayList<>(), new Time(1576135793001L));
        Assert.assertEquals(u, rs.getSuggestedBy());
        Assert.assertEquals(new Time(1576135793001L), rs.getSuggestedDate());
        Assert.assertEquals(u, rs.getSuggestedTo());
        Assert.assertEquals(new ArrayList<>(), rs.getSuggestions());
    }

    @Test
    public void testJSPOnlyShareMethods() {
        Share s = new Share(u, 23.14, null);
        Assert.assertEquals("23,14", s.getShareAmount());
        Assert.assertEquals("", s.getJoinDate());
    }

    @Test
    public void testJSPOnlySousReservationEntityMethods() {
        SousReservationEntity sre = new SousReservationEntity(56, 14, u, "toto", TS20191212_0829);
        Assert.assertEquals("12 décembre 2019 à 08h29", sre.getBookedOn());
        Assert.assertEquals(14, sre.getIdeeId());
    }

    @Test
    public void testJSPOnlyUserParameterMethods() {
        UserParameter up = new UserParameter(23, 68, "param", "titi", "oh oh");
        Assert.assertEquals("oh oh", up.getParameterDescription());
        Assert.assertEquals("titi", up.getParameterValue());
    }

}