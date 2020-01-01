package com.mosioj.ideescadeaux.model.entities;

import com.mosioj.ideescadeaux.TemplateTest;
import org.junit.Test;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;

import static org.junit.Assert.*;

public class JSPAccessortTest extends TemplateTest {

    // FIXME : 4 voir si besoin de tout...

    User u = new User(42, "toto", "toto@tutu.gmail.fr", "none.png");
    public static final Timestamp TS20191212_0829 = new Timestamp(1576135793001L);

    @Test
    public void testJSPOnlyUserMethods() {
        assertFalse(u.getIsInMyNetwork());
        assertFalse(u.getHasBookedOneOfItsIdeas());
        assertEquals("", u.getCreationDate());
        assertEquals("", u.getLastLogin());
        assertTrue(u.hasSetUpAnAvatar());
        assertEquals("large/none.png", u.getAvatarSrcLarge());
        assertEquals(0, u.getNbDaysBeforeBirthday());
        assertNull(u.getFreeComment());
    }

    @Test
    public void testJSPOnlyCommentMethods() {
        Comment c = new Comment(42, "aa", u, 35, TS20191212_0829);
        assertEquals("12 décembre 2019 à 08h29", c.getTime());
    }

    @Test
    public void testJSPOnlyIdeaGroupMethods() {
        IdeaGroup group = new IdeaGroup(30, 23.03);
        assertEquals("23,03", group.getTotalAmount());
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
                          TS20191212_0829,
                          null,
                          null);
        assertFalse(i.hasAskedIfUpToDate());
        assertFalse(i.hasComment());
        assertFalse(i.hasQuestion());
        assertEquals("", i.getDisplayClass());
        assertEquals("12 décembre 2019 à 08h29", i.getBookingDate());
        assertEquals("12 décembre 2019 à 08h29", i.getModificationDate());
        assertEquals("small/toto.png", i.getImageSrcSmall());
        assertEquals("large/toto.png", i.getImageSrcLarge());
        assertNull(i.getCategory());
    }

    @Test
    public void testJSPOnlyRelationMethods() {
        Relation r = new Relation(u, u);
        assertEquals(u, r.getFirst());
        assertFalse(r.getSecondIsInMyNetwork());
    }

    @Test
    public void testJSPOnlyRelationRequestMethods() {
        RelationRequest rr = new RelationRequest(u, u, new Date(1576135793001L));
        assertEquals(u, rr.getSent_by());
        assertEquals(u, rr.getSent_to());
        assertEquals(new Date(1576135793001L), rr.getRequest_date());
    }

    @Test
    public void testJSPOnlyRelationSuggestionMethods() {
        RelationSuggestion rs = new RelationSuggestion(u, u, new ArrayList<>(), new Time(1576135793001L));
        assertEquals(u, rs.getSuggestedBy());
        assertEquals(new Time(1576135793001L), rs.getSuggestedDate());
        assertEquals(u, rs.getSuggestedTo());
        assertEquals(new ArrayList<>(), rs.getSuggestions());
    }

    @Test
    public void testJSPOnlyShareMethods() {
        Share s = new Share(u, 23.14);
        assertEquals("23,14", s.getShareAmount());
    }

    @Test
    public void testJSPOnlySousReservationEntityMethods() {
        SousReservationEntity sre = new SousReservationEntity(56, 14, u, "toto", TS20191212_0829);
        assertEquals(TS20191212_0829, sre.getBookedOn());
        assertEquals(14, sre.getIdeeId());
    }

    @Test
    public void testJSPOnlyUserParameterMethods() {
        UserParameter up = new UserParameter(23, 68, "param", "titi", "oh oh");
        assertEquals("oh oh", up.getParameterDescription());
        assertEquals("titi", up.getParameterValue());
    }

}