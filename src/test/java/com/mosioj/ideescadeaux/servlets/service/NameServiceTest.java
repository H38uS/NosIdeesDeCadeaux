package com.mosioj.ideescadeaux.servlets.service;

import com.mosioj.ideescadeaux.servlets.AbstractTestServlet;
import com.mosioj.ideescadeaux.servlets.service.response.NameAnswer;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

public class NameServiceTest extends AbstractTestServlet {

    public NameServiceTest() {
        super(new NameService());
    }

    @Test
    public void testShouldReturnNames() {

        when(request.getParameter(NameService.NAME_OR_EMAIL)).thenReturn("jord");

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

        when(request.getParameter(NameService.NAME_OR_EMAIL)).thenReturn("z");

        NameAnswers resp = doTestServiceGet(NameAnswers.class);

        assertEquals(5, resp.size()); // max size
    }

    @Test
    public void testDoubleSizeMatchesAll() {

        when(request.getParameter(NameService.NAME_OR_EMAIL)).thenReturn("zz");

        NameAnswers resp = doTestServiceGet(NameAnswers.class);

        assertEquals(5, resp.size()); // max size
    }

    @Test
    public void testPriority() {

        when(request.getParameter(NameService.NAME_OR_EMAIL)).thenReturn("zmail.");

        NameAnswers resp = doTestServiceGet(NameAnswers.class);

        assertEquals(3, resp.size()); // max size
        NameAnswer first = resp.get(0);
        NameAnswer second = resp.get(1);
        NameAnswer third = resp.get(2);
        assertEquals("Iihi (tuuuuuut@hotzzzmail.fr)", first.value);
        assertEquals("Jordan.mosio@hotmail.fr (jordan.mosio@hotmailsafded.fr)", second.value);
        assertEquals("Djoeîéèôe (jo3@hotmadzzdzil.fr)", third.value);
    }

    @Test
    public void testAucunResultat() {

        when(request.getParameter(NameService.NAME_OR_EMAIL)).thenReturn("totoàlaplage");

        NameAnswers resp = doTestServiceGet(NameAnswers.class);

        assertEquals(0, resp.size()); // max size
    }

    @Test
    public void testPasResultatCarPasMemeReseau() {

        when(request.getParameter(NameService.NAME_OR_EMAIL)).thenReturn("pouet");

        NameAnswers resp = doTestServiceGet(NameAnswers.class);

        assertEquals(0, resp.size()); // max size
    }

    public static class NameAnswers extends ArrayList<NameAnswer> {
        // Wrapper for GSon
    }
}
