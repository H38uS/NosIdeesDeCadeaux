package com.mosioj.ideescadeaux.webapp.viewhelper;

import com.mosioj.ideescadeaux.webapp.servlets.controllers.relations.Page;
import org.junit.Test;

import javax.servlet.http.HttpServletRequest;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ListResultWithPagesHelperTest {

    @Test
    public void testDefaultPageNumber() {
        ListResultWithPagesHelper helper = ListResultWithPagesHelper.withDefaultMax();
        assertEquals(new Page(1), helper.getPages(2).get(0));
        assertEquals(2, helper.getPages(32).size());
        assertEquals(1, helper.getPages(19).size());
        assertEquals(1, helper.getPages(20).size());
        assertEquals(2, helper.getPages(21).size());
        assertEquals(8, helper.getPages(144).size());
    }

    @Test
    public void shouldConsiderTheMaximumNumberSet() {
        final int maxNumberOfResults = 6;
        ListResultWithPagesHelper helper = ListResultWithPagesHelper.with(maxNumberOfResults);
        assertEquals(6, helper.getPages(maxNumberOfResults * 5 + 1).size());
        assertEquals(1, helper.getPages(maxNumberOfResults - 1).size());
        assertEquals(1, helper.getPages(maxNumberOfResults).size());
        assertEquals(2, helper.getPages(maxNumberOfResults + 3).size());
        assertEquals(15, helper.getPages(maxNumberOfResults * 14 + 2).size());
    }

    @Test
    public void testFirstRow() {

        // Mock
        HttpServletRequest request = mock(HttpServletRequest.class);

        // Deux résultats par page
        ListResultWithPagesHelper helper = ListResultWithPagesHelper.with(2);

        // Default is page one
        when(request.getParameter(ListResultWithPagesHelper.PAGE_ARG)).thenReturn("");
        assertEquals(0, helper.getFirstRow(request));

        // Get the first page
        when(request.getParameter(ListResultWithPagesHelper.PAGE_ARG)).thenReturn("1");
        assertEquals(0, helper.getFirstRow(request));

        // Get the a page, not the first
        when(request.getParameter(ListResultWithPagesHelper.PAGE_ARG)).thenReturn("3");
        assertEquals(4, helper.getFirstRow(request));
    }
}