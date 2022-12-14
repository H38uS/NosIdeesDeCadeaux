package com.mosioj.ideescadeaux.webapp.servlets.service.reservation;

import com.mosioj.ideescadeaux.core.model.entities.text.Idee;
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

        // FIXME : ne plus faire ça, faire le markdown
        Idee idea = policy.getIdea();
        String comment = ParametersUtils.readAndEscape(request, "comment", true);

        if (StringUtils.isBlank(comment)) {
            final String message = "Le commentaire ne peut pas être vide !";
            buildResponse(response, ServiceResponse.ko(message, thisOne));
            return;
        }

        if (!IdeesRepository.canSubBook(idea, thisOne)) {
            final String message = "L'idée a déjà été réservée, ou vous en avez déjà réservé une sous partie.";
            buildResponse(response, ServiceResponse.ko(message, thisOne));
            return;
        }

        // succès
        IdeesRepository.sousReserver(idea);
        SousReservationRepository.sousReserver(idea.getId(), thisOne.id, comment);
        final String message = "La réservation a bien été créée.";
        buildResponse(response, ServiceResponse.ok(message, thisOne));
    }

}
