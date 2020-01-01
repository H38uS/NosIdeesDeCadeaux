package com.mosioj.ideescadeaux.webapp.servlets.service;

import java.sql.SQLException;
import java.text.MessageFormat;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mosioj.ideescadeaux.core.model.notifications.instance.NotifAskIfIsUpToDate;
import com.mosioj.ideescadeaux.webapp.servlets.rootservlet.IdeesCadeauxPostServlet;
import com.mosioj.ideescadeaux.webapp.servlets.securitypolicy.IdeaInteractionBookingUpToDate;
import com.mosioj.ideescadeaux.webapp.servlets.service.response.ServiceResponse;
import com.mosioj.ideescadeaux.core.model.repositories.NotificationsRepository;
import com.mosioj.ideescadeaux.core.model.repositories.UsersRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mosioj.ideescadeaux.core.model.entities.Idee;
import com.mosioj.ideescadeaux.core.model.repositories.IsUpToDateQuestionsRepository;

@WebServlet("/protected/service/est_a_jour")
public class ServiceEstAJour extends IdeesCadeauxPostServlet<IdeaInteractionBookingUpToDate> {

    private static final long serialVersionUID = 2642366164643542379L;
    public static final String IDEE_FIELD_PARAMETER = "idee";
    private static final Logger logger = LogManager.getLogger(ServiceEstAJour.class);

    public ServiceEstAJour() {
        super(new IdeaInteractionBookingUpToDate(IDEE_FIELD_PARAMETER));
    }

    @Override
    public void ideesKDoPOST(HttpServletRequest request, HttpServletResponse response) throws ServletException, SQLException {
        Idee idea = policy.getIdea();
        boolean status = askIfUpToDate(idea);
        buildResponse(response, new ServiceResponse<>(status, "", isAdmin(request)));
    }

    /**
     * @param idea The idea.
     * @return True if the notification has been added, false if already sent.
     */
    private boolean askIfUpToDate(Idee idea) throws SQLException {

        int userId = thisOne.id;
        logger.debug(MessageFormat.format("Demande de validité sur l''idée {0} de {1}.", idea.getId(), userId));

        if (IsUpToDateQuestionsRepository.associationExists(idea, thisOne)) {
            return false;
        }

        if (IsUpToDateQuestionsRepository.addAssociation(idea.getId(), userId) == 1) {
            NotifAskIfIsUpToDate isUpToDateNotif = new NotifAskIfIsUpToDate(UsersRepository.getUser(userId), idea);
            if (!NotificationsRepository.hasNotification(idea.owner.id, isUpToDateNotif)) {
                NotificationsRepository.addNotification(idea.owner.id, isUpToDateNotif);
                return true;
            }
        }

        return false;
    }
}
