package com.mosioj.ideescadeaux.webapp.servlets.service.response;

import com.google.gson.annotations.Expose;
import com.mosioj.ideescadeaux.webapp.servlets.controllers.relations.Page;

import java.util.List;

public class PagedResponse<T> {

    /** The list of page available for this request. */
    @Expose
    private final List<Page> pages;

    /** The response list content. */
    @Expose
    private final T theContent;

    /**
     * @param pages      The list of page available for this request.
     * @param theContent The response list content.
     */
    private PagedResponse(final List<Page> pages, final T theContent) {
        this.pages = pages;
        this.theContent = theContent;
    }

    /**
     * @return The response list content.
     */
    public T getTheContent() {
        return theContent;
    }

    /**
     * @param pages      The list of page available for this request.
     * @param theContent The response list content.
     * @param <T>        The response type.
     * @return The new response object.
     */
    public static <T> PagedResponse<T> from(final List<Page> pages, final T theContent) {
        return new PagedResponse<>(pages, theContent);
    }
}
