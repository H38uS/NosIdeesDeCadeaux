package com.mosioj.ideescadeaux.webapp.servlets.controllers.idees;

import com.mosioj.ideescadeaux.core.model.entities.Idee;
import com.mosioj.ideescadeaux.core.model.entities.User;
import com.mosioj.ideescadeaux.core.model.repositories.IdeesRepository;
import com.mosioj.ideescadeaux.core.model.repositories.SousReservationRepository;
import com.mosioj.ideescadeaux.webapp.servlets.rootservlet.IdeesCadeauxGetAndPostServlet;
import com.mosioj.ideescadeaux.webapp.servlets.securitypolicy.root.SecurityPolicy;
import com.mosioj.ideescadeaux.webapp.utils.ParametersUtils;
import com.mosioj.ideescadeaux.webapp.utils.RootingsUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractIdea<P extends SecurityPolicy> extends IdeesCadeauxGetAndPostServlet<P> {

    // FIXME : 0 faire une passe et supprimer tout ceux qui hérite et qui sont faits en service ?
    private static final long serialVersionUID = -1774633803227715931L;

    /**
     * @param policy The security policy defining whether we can interact with the parameters, etc.
     */
    public AbstractIdea(P policy) {
        super(policy);
    }

    /**
     * @param request    The http request.
     * @param response   The http response.
     * @param user       The user.
     * @param idea       The idea.
     * @param landingURL The url.
     * @return True in case of success, false otherwise.
     */
    // FIXME : 2 faire un service pour pouvoir supprimer cette méthode et les POST qui l'utilisent
    protected boolean sousReserver(HttpServletRequest request,
                                   HttpServletResponse response,
                                   User user,
                                   Idee idea,
                                   String landingURL) throws SQLException {

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
