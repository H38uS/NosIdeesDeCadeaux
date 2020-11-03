package com.mosioj.ideescadeaux.webapp.servlets.service.reservation;

import com.mosioj.ideescadeaux.core.model.entities.Idee;
import com.mosioj.ideescadeaux.core.model.repositories.IdeesRepository;
import com.mosioj.ideescadeaux.core.model.repositories.SousReservationRepository;
import com.mosioj.ideescadeaux.webapp.servlets.rootservlet.ServicePost;
import com.mosioj.ideescadeaux.webapp.servlets.securitypolicy.IdeaInteraction;
import com.mosioj.ideescadeaux.webapp.servlets.service.response.ServiceResponse;
import com.mosioj.ideescadeaux.webapp.utils.ParametersUtils;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.SQLException;

@WebServlet("/protected/service/sous_reserver")
public class ServiceSousReserver extends ServicePost<IdeaInteraction> {

    private static final String IDEA_ID_PARAM = "idee";

    /** Class constructor. */
    public ServiceSousReserver() {
        super(new IdeaInteraction(IDEA_ID_PARAM));
    }

    @Override
    public void servicePost(HttpServletRequest request, HttpServletResponse response) throws SQLException {

        Idee idea = policy.getIdea();
        String comment = ParametersUtils.readAndEscape(request, "comment");

        if (StringUtils.isBlank(comment)) {
            final String message = "Le commentaire ne peut pas être vide !";
            buildResponse(response, ServiceResponse.ko(message, isAdmin(request), thisOne));
            return;
        }

        if (!IdeesRepository.canSubBook(idea.getId(), thisOne.id)) {
            final String message = "L'idée a déjà été réservée, ou vous en avez déjà réservé une sous partie.";
            buildResponse(response, ServiceResponse.ko(message, isAdmin(request), thisOne));
            return;
        }

        // succès
        IdeesRepository.sousReserver(idea.getId());
        SousReservationRepository.sousReserver(idea.getId(), thisOne.id, comment);
        final String message = "La réservation a bien été créée.";
        buildResponse(response, ServiceResponse.ok(message, isAdmin(request), thisOne));
    }

}
