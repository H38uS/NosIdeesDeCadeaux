package com.mosioj.ideescadeaux.webapp.servlets.controllers.idees;

import com.mosioj.ideescadeaux.core.model.entities.Idee;
import com.mosioj.ideescadeaux.core.model.entities.User;
import com.mosioj.ideescadeaux.core.model.repositories.IdeesRepository;
import com.mosioj.ideescadeaux.core.model.repositories.SousReservationRepository;
import com.mosioj.ideescadeaux.webapp.servlets.rootservlet.IdeesCadeauxGetAndPostServlet;
import com.mosioj.ideescadeaux.webapp.servlets.securitypolicy.root.SecurityPolicy;
import com.mosioj.ideescadeaux.webapp.utils.ParametersUtils;
import com.mosioj.ideescadeaux.webapp.utils.RootingsUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class AbstractIdea<P extends SecurityPolicy> extends IdeesCadeauxGetAndPostServlet<P> {

    private static final long serialVersionUID = -1774633803227715931L;
    private static final Logger logger = LogManager.getLogger(AbstractIdea.class);

    private static final String FROM_URL = "from";

    /**
     * @param policy The security policy defining whether we can interact with the parameters, etc.
     */
    public AbstractIdea(P policy) {
        super(policy);
    }

    /**
     * Tries to read a from request parameters or from parameters map. If it looks coming from the web site, returns it.
     * Otherwise, returns the default value.
     *
     * @param request      Current request being processed.
     * @param defaultValue Default value for the next redirection.
     * @return The next page to be redirected to.
     */
    protected String getFrom(HttpServletRequest request, String defaultValue) {

        String from = ParametersUtils.readIt(request, FROM_URL);
        logger.debug(MessageFormat.format("Resolving request from: {0}", from));


        if (from.trim().isEmpty()) {
            // Trying to resolve it from parameters
            try {
                final File ideaPicturePath = ParametersUtils.getIdeaPicturePath();
                final Map<String, String> parameters = ParametersUtils.readMultiFormParameters(request, ideaPicturePath);
                from = parameters.get(FROM_URL);
            } catch (SQLException e) {
                logger.warn("Fail to read parameters to compute the from clause...", e);
            }
            if (from == null || from.trim().isEmpty()) {
                return defaultValue;
            }
        }

        if (!from.startsWith("/")) {
            // Looks like it is not coming from the website...
            return defaultValue;
        }

        return from;
    }

    /**
     * @param request    The http request.
     * @param response   The http response.
     * @param user       The user.
     * @param idea       The idea.
     * @param landingURL The url.
     * @return True in case of success, false otherwise.
     */
    protected boolean sousReserver(HttpServletRequest request,
                                   HttpServletResponse response,
                                   User user,
                                   Idee idea,
                                   String landingURL) throws SQLException, ServletException {

        List<String> errors = new ArrayList<>();
        String comment = ParametersUtils.readAndEscape(request, "comment");
        if (comment == null || comment.isEmpty()) {
            errors.add("Le commentaire ne peut pas être vide !");
        }

        if (!IdeesRepository.canSubBook(idea.getId(), user.id)) {
            errors.add("L'idée a déjà été réservée, ou vous en avez déjà réservé une sous partie.");
        }

        if (!errors.isEmpty()) {
            request.setAttribute("errors", errors);
            RootingsUtils.rootToPage(landingURL, request, response);
            return false;
        }

        IdeesRepository.sousReserver(idea.getId());
        SousReservationRepository.sousReserver(idea.getId(), user.id, comment);
        return true;
    }

}
