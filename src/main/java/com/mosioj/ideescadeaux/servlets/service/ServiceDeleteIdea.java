package com.mosioj.ideescadeaux.servlets.service;

import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mosioj.ideescadeaux.servlets.rootservlet.IdeesCadeauxPostServlet;
import com.mosioj.ideescadeaux.servlets.service.response.ServiceResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mosioj.ideescadeaux.model.entities.Idee;
import com.mosioj.ideescadeaux.model.entities.User;
import com.mosioj.ideescadeaux.notifications.AbstractNotification;
import com.mosioj.ideescadeaux.notifications.ParameterName;
import com.mosioj.ideescadeaux.notifications.instance.NotifBookedRemove;
import com.mosioj.ideescadeaux.notifications.instance.NotifNoIdea;
import com.mosioj.ideescadeaux.notifications.instance.param.NotifUserIdParam;
import com.mosioj.ideescadeaux.servlets.logichelpers.IdeaInteractions;
import com.mosioj.ideescadeaux.servlets.securitypolicy.IdeaModification;

@WebServlet("/protected/service/delete_idea")
public class ServiceDeleteIdea extends IdeesCadeauxPostServlet<IdeaModification> {

    private static final Logger logger = LogManager.getLogger(ServiceDeleteIdea.class);
    private static final long serialVersionUID = 2642366164643542379L;
    public static final String IDEE_ID_PARAM = "ideeId";

    public ServiceDeleteIdea() {
        super(new IdeaModification(IDEE_ID_PARAM));
    }

    @Override
    public void ideesKDoPOST(HttpServletRequest request, HttpServletResponse response) throws ServletException, SQLException {

        IdeaInteractions logic = new IdeaInteractions();
        Idee idea = policy.getIdea();
        // Reading parameters
        logger.debug(MessageFormat.format("Deleting idea {0}.", idea.getId()));

        Set<Integer> notified = new HashSet<>();
        List<User> bookers = idea.getBookers(model.sousReservation);
        logger.debug(MessageFormat.format("Liste des personnes qui ont réservé au moment de la suppression: {0}",
                                          bookers));
        for (User user : bookers) {
            model.notif.addNotification(user.id, new NotifBookedRemove(idea, idea.owner.getName()));
            notified.add(user.id);
        }

        String image = idea.getImage();
        logger.debug(MessageFormat.format("Image: {0}.", image));
        logic.removeUploadedImage(getIdeaPicturePath(), image);

        List<AbstractNotification> notifications = model.notif.getNotification(ParameterName.IDEA_ID, idea.getId());
        // Pour chaque notification qui concerne cette idée
        for (AbstractNotification notification : notifications) {

            // Pour chaque notification qui a un user
            if (notification instanceof NotifUserIdParam) {

                NotifUserIdParam notifUserId = (NotifUserIdParam) notification;
                // Si la personne n'a pas déjà été notifié, et n'est pas le owner de l'idée
                // On lui envoie une notif
                if (!notified.contains(notifUserId.getUserIdParam()) && idea.owner.id != notifUserId.getUserIdParam()) {
                    model.notif.addNotification(notifUserId.getUserIdParam(),
                                                new NotifBookedRemove(idea, thisOne.getName()));
                    notified.add(notifUserId.getUserIdParam());
                }
            }

            model.notif.remove(notification.id);
        }

        int userId = thisOne.id;
        model.idees.remove(idea.getId());

        if (!model.idees.hasIdeas(userId)) {
            model.notif.addNotification(userId, new NotifNoIdea());
        }

        buildResponse(response, ServiceResponse.ok("", isAdmin(request)));
    }
}
