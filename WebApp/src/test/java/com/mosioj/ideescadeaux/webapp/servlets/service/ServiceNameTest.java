package com.mosioj.ideescadeaux.webapp.servlets.service;

import com.mosioj.ideescadeaux.webapp.servlets.AbstractTestServletWebApp;
import com.mosioj.ideescadeaux.webapp.servlets.service.response.NameAnswer;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

public class ServiceNameTest extends AbstractTestServletWebApp {

    public ServiceNameTest() {
        super(new ServiceName());
    }

    @Test
    public void testShouldReturnNames() {

        bindRequestParam(ServiceName.NAME_OR_EMAIL, "jord");

        NameAnswers resp = doTestServiceGet(NameAnswers.class);

        assertEquals(2, resp.size());
        NameAnswer first = resp.get(0);
        assertEquals("Jordan.mosio@hotmail.fr (jordan.mosio@hotmailsafded.fr)", first.value);
    }

    @Test
    public void testNoParameterMatchesAll() {
        NameAnswers resp = doTestServiceGet(NameAnswers.class);
        assertEquals(5, resp.size()); // max size
    }

    @Test
    public void testSingleSizeMatchesAll() {

        bindRequestParam(ServiceName.NAME_OR_EMAIL, "z");

        NameAnswers resp = doTestServiceGet(NameAnswers.class);

        assertEquals(5, resp.size()); // max size
    }

    @Test
    public void testDoubleSizeMatchesAll() {

        bindRequestParam(ServiceName.NAME_OR_EMAIL, "zz");

        NameAnswers resp = doTestServiceGet(NameAnswers.class);

        assertEquals(5, resp.size()); // max size
    }

    @Test
    public void testPriority() {

        bindRequestParam(ServiceName.NAME_OR_EMAIL, "zmail.");

        NameAnswers resp = doTestServiceGet(NameAnswers.class);

        assertEquals(3, resp.size()); // max size
        NameAnswer first = resp.get(0);
        NameAnswer second = resp.get(1);
        NameAnswer third = resp.get(2);
        assertEquals("Iihi (tuuuuuut@hotzzzmail.fr)", first.value);
        assertEquals("Jordan.mosio@hotmail.fr (jordan.mosio@hotmailsafded.fr)", second.value);
        assertEquals("Djoeîéèôe (jo3@hotmadzzdzil.frtrr)", third.value);
    }

    @Test
    public void testAucunResultat() {

        bindRequestParam(ServiceName.NAME_OR_EMAIL, "totoàlaplage");

        NameAnswers resp = doTestServiceGet(NameAnswers.class);

        assertEquals(0, resp.size()); // max size
    }

    @Test
    public void testPasResultatCarPasMemeReseau() {

        bindRequestParam(ServiceName.NAME_OR_EMAIL, "pouet");

        NameAnswers resp = doTestServiceGet(NameAnswers.class);

        assertEquals(0, resp.size()); // max size
    }

    public static class NameAnswers extends ArrayList<NameAnswer> {
        // Wrapper for GSon
    }
}
