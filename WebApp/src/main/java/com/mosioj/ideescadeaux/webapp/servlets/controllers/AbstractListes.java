package com.mosioj.ideescadeaux.webapp.servlets.controllers;

import com.mosioj.ideescadeaux.webapp.servlets.controllers.relations.Page;
import com.mosioj.ideescadeaux.webapp.servlets.rootservlet.IdeesCadeauxGetAndPostServlet;
import com.mosioj.ideescadeaux.webapp.servlets.securitypolicy.root.SecurityPolicy;
import com.mosioj.ideescadeaux.webapp.utils.NotLoggedInException;
import com.mosioj.ideescadeaux.webapp.utils.ParametersUtils;
import com.mosioj.ideescadeaux.webapp.utils.RootingsUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Defines a generic base class that handle multiple pages based on the maximum rows returned.
 *
 * @param <T> The entity types displayed.
 * @author Jordan Mosio
 */
public abstract class AbstractListes<T, P extends SecurityPolicy> extends IdeesCadeauxGetAndPostServlet<P> {

    private static final long serialVersionUID = -3557546858990933563L;
    private static final Logger LOGGER = LogManager.getLogger(AbstractListes.class);

    private static final String PAGE_ARG = "page";
    protected final int maxNumberOfResults;

    public AbstractListes(P policy, int maxNumberOfResults) {
        super(policy);
        this.maxNumberOfResults = maxNumberOfResults;
    }

    public AbstractListes(P policy) {
        this(policy, 20); // Default max number
    }

    /**
     * @return The URL argument to pass to the RootingsUtils.rootToPage method.
     */
    protected abstract String getViewPageURL();

    /**
     * @return The calling URL, starting with no slash (e.g. protected/mes_listes).
     */
    protected abstract String getCallingURL();

    protected abstract String getSpecificParameters(HttpServletRequest req);

    protected abstract int getTotalNumberOfRecords(HttpServletRequest req) throws SQLException, NotLoggedInException;

    protected abstract List<T> getDisplayedEntities(int firstRow, HttpServletRequest req) throws SQLException, NotLoggedInException;

    /**
     * @param total The total number of record.
     * @return The list of pages.
     */
    protected List<Page> getPages(int total) {
        List<Page> pages = new ArrayList<>();
        for (int i = 0; i < total / maxNumberOfResults; i++) {
            pages.add(new Page(i + 1));
        }
        if (total % maxNumberOfResults != 0) {
            pages.add(new Page(pages.size() + 1));
        }
        return pages;
    }

    /**
     * @param request The http request.
     * @return The found page number from the request, 1 if not provided.
     */
    protected int getPageNumber(HttpServletRequest request) {
        int pageNumber = ParametersUtils.readInt(request, AbstractListes.PAGE_ARG).orElse(1);
        request.setAttribute("current", pageNumber);
        return pageNumber;
    }

    protected int getFirstRow(int pageNumber, final int MAX_NUMBER_OF_RESULT) {
        return (pageNumber - 1) * MAX_NUMBER_OF_RESULT;
    }

    protected int getFirstRow(HttpServletRequest req) {
        int pageNumber = getPageNumber(req);
        return getFirstRow(pageNumber, maxNumberOfResults);
    }

    @Override
    public void ideesKDoPOST(HttpServletRequest request, HttpServletResponse response) throws ServletException, SQLException {
        RootingsUtils.redirectToPage("/" +
                                     getCallingURL() +
                                     "?page=" +
                                     getPageNumber(request) +
                                     getSpecificParameters(request),
                                     request,
                                     response);
    }

    @Override
    public void ideesKDoGET(HttpServletRequest req, HttpServletResponse resp) throws ServletException, SQLException {

        int pageNumber = getPageNumber(req);
        int firstRow = getFirstRow(req);

        LOGGER.info(MessageFormat.format("Getting page {0} information from URL {1}...", pageNumber, getCallingURL()));

        List<T> ids = getDisplayedEntities(firstRow, req);
        req.setAttribute("entities", ids);
        LOGGER.debug("Entities: " + ids);

        int total = ids.size();
        if (total >= maxNumberOfResults || pageNumber > 1) {
            // On regarde si y'en a pas d'autres
            total = getTotalNumberOfRecords(req);
            if (total > maxNumberOfResults) {
                List<Page> pages = getPages(total);
                req.setAttribute("pages", pages);
                req.setAttribute("last", pages.size());
            }
        }

        req.setAttribute("spec_parameters", getSpecificParameters(req));
        req.setAttribute("call_back", getCallingURL());

        StringBuilder sb = new StringBuilder();
        sb.append(getCallingURL());
        sb.append("?");
        for (String param : req.getParameterMap().keySet()) {
            sb.append(param).append("=").append(req.getParameter(param)).append("&");
        }
        sb.deleteCharAt(sb.length() - 1);
        req.setAttribute("identic_call_back", sb);

        RootingsUtils.rootToPage(getViewPageURL(), req, resp); // FIXME : utiliser du json - fait pour les idées
    }
}
