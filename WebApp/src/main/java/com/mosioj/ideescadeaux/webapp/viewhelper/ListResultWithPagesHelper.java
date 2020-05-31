package com.mosioj.ideescadeaux.webapp.viewhelper;

import com.mosioj.ideescadeaux.webapp.servlets.controllers.relations.Page;
import com.mosioj.ideescadeaux.webapp.utils.ParametersUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ListResultWithPagesHelper {

    /** Expected argument to compute which page is required. */
    public static final String PAGE_ARG = "page";

    /** Default maximum number of item to provide in the results. */
    private static final int DEFAULT_LIST_SIZE = 20;

    /** The actual maximum number of results to provide. */
    protected final int maxNumberOfResults;

    /**
     * @param maxNumberOfResults The actual maximum number of results to provide.
     */
    private ListResultWithPagesHelper(final int maxNumberOfResults) {
        this.maxNumberOfResults = maxNumberOfResults;
    }

    /**
     * @param maxNumberOfResults The actual maximum number of results to provide.
     * @return The new utility object.
     */
    public static ListResultWithPagesHelper with(final int maxNumberOfResults) {
        return new ListResultWithPagesHelper(maxNumberOfResults);
    }

    /**
     * @return The new utility object, with the ListResultWithPagesHelper.DEFAULT_LIST_SIZE.
     */
    public static ListResultWithPagesHelper withDefaultMax() {
        return new ListResultWithPagesHelper(DEFAULT_LIST_SIZE);
    }

    /**
     * @return The actual maximum number of results to provide.
     */
    public int getMaxNumberOfResults() {
        return maxNumberOfResults;
    }

    /**
     * @param request The http request.
     * @return The page number found from the request, 1 if not provided.
     */
    public int getPageNumber(HttpServletRequest request) {
        int pageNumber = ParametersUtils.readInt(request, PAGE_ARG).orElse(1);
        request.setAttribute("current", pageNumber);
        return pageNumber;
    }

    /**
     * @param request The http request.
     * @return The first row to use in SQL queries corresponding to this page.
     */
    public int getFirstRow(HttpServletRequest request) {
        int pageNumber = getPageNumber(request);
        return (pageNumber - 1) * maxNumberOfResults;
    }

    /**
     * @param total The total number of record.
     * @return The list of pages required to manage this quantity of items.
     */
    protected List<Page> getPages(int total) {
        List<Page> pages = IntStream.range(0, total / maxNumberOfResults)
                                    .map(i -> i + 1)
                                    .mapToObj(Page::new)
                                    .collect(Collectors.toList());
        if (total % maxNumberOfResults != 0) {
            pages.add(new Page(pages.size() + 1));
        }
        return pages;
    }

    /**
     * @param request         The http request. May contain parameters.
     * @param listSize        The number of items that will be sent as a response for this request.
     * @param numberOfRecords The utility to get the total number of records for this request.
     * @return The list of pages required to manage all the records.
     */
    public List<Page> getPages(final HttpServletRequest request,
                               final int listSize,
                               final TotalNumberOfRecords numberOfRecords) {
        int pageNumber = getPageNumber(request);
        int total = listSize;
        if (listSize >= maxNumberOfResults || pageNumber > 1) {
            // On regarde si y'en a pas d'autres
            int itemSize = numberOfRecords.getIt(request);
            if (itemSize > maxNumberOfResults) {
                total = itemSize;
            }
        }
        final List<Page> pages = getPages(total);
        if (pages.size() > 0) {
            pages.get(pageNumber - 1).setSelected(true);
        }
        return pages;
    }

    @FunctionalInterface
    public interface TotalNumberOfRecords {

        /**
         * @param request The http request. May contain parameters.
         * @return The total number of records that will be produced when fetching the entire list.
         */
        int getIt(HttpServletRequest request);
    }
}
