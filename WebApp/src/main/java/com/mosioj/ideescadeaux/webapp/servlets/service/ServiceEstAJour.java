package com.mosioj.ideescadeaux.webapp.servlets.service;

import com.mosioj.ideescadeaux.core.model.entities.Idee;
import com.mosioj.ideescadeaux.core.model.notifications.NType;
import com.mosioj.ideescadeaux.core.model.notifications.Notification;
import com.mosioj.ideescadeaux.core.model.repositories.IsUpToDateQuestionsRepository;
import com.mosioj.ideescadeaux.webapp.servlets.rootservlet.ServicePost;
import com.mosioj.ideescadeaux.webapp.servlets.securitypolicy.IdeaInteractionBookingUpToDate;
import com.mosioj.ideescadeaux.webapp.servlets.service.response.ServiceResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.MessageFormat;

import static com.mosioj.ideescadeaux.core.model.repositories.NotificationsRepository.findNotificationsMatching;

@WebServlet("/protected/service/est_a_jour")
public class ServiceEstAJour extends ServicePost<IdeaInteractionBookingUpToDate> {

    private static final long serialVersionUID = 2642366164643542379L;
    public static final String IDEE_FIELD_PARAMETER = "idee";
    private static final Logger logger = LogManager.getLogger(ServiceEstAJour.class);

    public ServiceEstAJour() {
        super(new IdeaInteractionBookingUpToDate(IDEE_FIELD_PARAMETER));
    }

    @Override
    public void servicePost(HttpServletRequest request, HttpServletResponse response) {
        Idee idea = policy.getIdea();
        boolean status = askIfUpToDate(idea);
        buildResponse(response, new ServiceResponse<>(status, "", thisOne));
    }

    /**
     * @param idea The idea.
     * @return True if the notification has been added, false if already sent.
     */
    private boolean askIfUpToDate(Idee idea) {

        logger.debug(MessageFormat.format("Demande de validité sur l''idée {0} de {1}.", idea.getId(), thisOne.id));

        if (IsUpToDateQuestionsRepository.associationExists(idea, thisOne)) {
            return false;
        }

        if (IsUpToDateQuestionsRepository.addAssociation(idea.getId(), thisOne.id) == 1) {
            final Notification isUpToDate = NType.IS_IDEA_UP_TO_DATE.with(thisOne, idea).setOwner(idea.getOwner());
            if (findNotificationsMatching(isUpToDate).isEmpty()) {
                // seulement si la notification n'existe pas encore
                isUpToDate.sendItTo(idea.getOwner());
                return true;
            }
        }

        return false;
    }
}
