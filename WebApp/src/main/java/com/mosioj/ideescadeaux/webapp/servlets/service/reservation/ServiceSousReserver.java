package com.mosioj.ideescadeaux.webapp.servlets.service.reservation;

import com.mosioj.ideescadeaux.core.model.entities.text.Idee;
import com.mosioj.ideescadeaux.core.model.repositories.IdeesRepository;
import com.mosioj.ideescadeaux.core.model.repositories.booking.SousReservationRepository;
import com.mosioj.ideescadeaux.webapp.servlets.rootservlet.ServiceGetAndPost;
import com.mosioj.ideescadeaux.webapp.servlets.securitypolicy.IdeaInteraction;
import com.mosioj.ideescadeaux.webapp.servlets.service.response.ServiceResponse;
import com.mosioj.ideescadeaux.webapp.utils.ParametersUtils;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/protected/service/sous_reserver")
public class ServiceSousReserver extends ServiceGetAndPost<IdeaInteraction> {

    /** Class constructor. */
    public ServiceSousReserver() {
        super(new IdeaInteraction("idee"));
    }

    @Override
    public void serviceGet(HttpServletRequest request, HttpServletResponse response) {
        Idee idea = policy.getIdea();
        buildResponse(response, ServiceResponse.ok(SousReservationRepository.getSousReservation(idea), thisOne));
    }

    // FIXME : Pouvoir suggérer de sous réserver cette idée

    @Override
    public void servicePost(HttpServletRequest request, HttpServletResponse response) {

        String comment = ParametersUtils.getPOSTParameterAsString(request, "comment");
        if (StringUtils.isBlank(comment)) {
            final String message = "Le commentaire ne peut pas être vide !";
            buildResponse(response, ServiceResponse.ko(message, thisOne));
            return;
        }

        Idee idea = policy.getIdea();
        if (!IdeesRepository.canBook(idea, thisOne) && !"Y".equals(idea.isSubBooked)) {
            final String message = "L'idée a déjà été réservée.";
            buildResponse(response, ServiceResponse.ko(message, thisOne));
            return;
        }

        // succès
        IdeesRepository.sousReserver(idea);
        SousReservationRepository.sousReserver(idea, thisOne, comment);
        final String message = "La réservation a bien été créée.";
        buildResponse(response, ServiceResponse.ok(message, thisOne));
    }

}
