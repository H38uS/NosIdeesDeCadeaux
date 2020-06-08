package com.mosioj.ideescadeaux.webapp.servlets.controllers;

import com.mosioj.ideescadeaux.webapp.servlets.controllers.relations.Page;
import com.mosioj.ideescadeaux.webapp.servlets.rootservlet.IdeesCadeauxGetServlet;
import com.mosioj.ideescadeaux.webapp.servlets.securitypolicy.root.SecurityPolicy;
import com.mosioj.ideescadeaux.webapp.utils.RootingsUtils;
import com.mosioj.ideescadeaux.webapp.viewhelper.ListResultWithPagesHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.List;

/**
 * Defines a generic base class that handle multiple pages based on the maximum rows returned.
 *
 * @param <T> The entity types displayed.
 * @author Jordan Mosio
 */
public abstract class AbstractListes<T, P extends SecurityPolicy> extends IdeesCadeauxGetServlet<P> {

    /** Serial id. */
    private static final long serialVersionUID = -3557546858990933563L;

    /** Class logger. */
    private static final Logger LOGGER = LogManager.getLogger(AbstractListes.class);

    /** The helper used to handle pages. */
    protected final ListResultWithPagesHelper helper;

    /**
     * @param policy The security policy.
     * @param helper The page manager.
     */
    public AbstractListes(P policy, ListResultWithPagesHelper helper) {
        super(policy);
        this.helper = helper;
    }

    // FIXME : ne plus utiliser les listes quand le service est migré

    /**
     * @return The URL argument to pass to the RootingsUtils.rootToPage method.
     */
    protected abstract String getViewPageURL();

    /**
     * @return The calling URL, starting with no slash (e.g. protected/mes_listes).
     */
    protected abstract String getCallingURL();

    protected abstract String getSpecificParameters(HttpServletRequest req);

    protected abstract int getTotalNumberOfRecords(HttpServletRequest req);

    protected abstract List<T> getDisplayedEntities(int firstRow, HttpServletRequest req) throws SQLException;

    @Override
    public void ideesKDoGET(HttpServletRequest req, HttpServletResponse resp) throws ServletException, SQLException {

        int pageNumber = helper.getPageNumber(req);
        int firstRow = helper.getFirstRow(req);

        LOGGER.info(MessageFormat.format("Getting page {0} information from URL {1}...", pageNumber, getCallingURL()));

        List<T> ids = getDisplayedEntities(firstRow, req);
        req.setAttribute("entities", ids);
        LOGGER.debug("Entities: " + ids);

        List<Page> pages = helper.getPages(req, ids.size(), this::getTotalNumberOfRecords);
        if (pages.size() > 1) {
            req.setAttribute("pages", pages);
            req.setAttribute("last", pages.size());
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

        RootingsUtils.rootToPage(getViewPageURL(), req, resp);
    }
}
