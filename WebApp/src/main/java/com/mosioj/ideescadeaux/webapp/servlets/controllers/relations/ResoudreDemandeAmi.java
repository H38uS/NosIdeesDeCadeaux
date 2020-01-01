package com.mosioj.ideescadeaux.webapp.servlets.controllers.relations;

import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.mosioj.ideescadeaux.core.model.notifications.AbstractNotification;
import com.mosioj.ideescadeaux.core.model.notifications.NotificationType;
import com.mosioj.ideescadeaux.core.model.notifications.ParameterName;
import com.mosioj.ideescadeaux.core.model.notifications.instance.NotifDemandeAcceptee;
import com.mosioj.ideescadeaux.core.model.notifications.instance.NotifDemandeRefusee;
import com.mosioj.ideescadeaux.webapp.servlets.rootservlet.IdeesCadeauxPostServlet;
import com.mosioj.ideescadeaux.webapp.servlets.securitypolicy.PeutResoudreDemandesAmis;
import com.mosioj.ideescadeaux.core.model.repositories.NotificationsRepository;
import com.mosioj.ideescadeaux.core.model.repositories.UserRelationRequestsRepository;
import com.mosioj.ideescadeaux.core.model.repositories.UserRelationsRepository;
import com.mosioj.ideescadeaux.core.model.repositories.UsersRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mosioj.ideescadeaux.core.model.entities.User;
import com.mosioj.ideescadeaux.webapp.utils.RootingsUtils;

@WebServlet("/protected/resoudre_demande_ami")
public class ResoudreDemandeAmi extends IdeesCadeauxPostServlet<PeutResoudreDemandesAmis> {

    private static final long serialVersionUID = 454017088023043164L;
    private static final Logger logger = LogManager.getLogger(ResoudreDemandeAmi.class);

    /**
     * Class constructor.
     */
    public ResoudreDemandeAmi() {
        super(new PeutResoudreDemandesAmis());
    }

    @Override
    public void ideesKDoPOST(HttpServletRequest request, HttpServletResponse response) throws ServletException, SQLException {

        List<User> accepted = new ArrayList<>();
        Set<AbstractNotification> toBeRemoved = new HashSet<>();

        // Parcours des réponses
        policy.getChoiceParameters()
              .forEach((fromUserId, accept) -> {
                  try {
                      processRequest(accepted, toBeRemoved, fromUserId, accept);
                  } catch (SQLException e) {
                      e.printStackTrace();
                      logger.error(e.getMessage());
                  }
              });

        // Suppression des notifications
        toBeRemoved.forEach(n -> NotificationsRepository.remove(n.id));
        NotificationsRepository.removeAllType(thisOne, NotificationType.NEW_FRIENSHIP_REQUEST);

        // Redirection à la page d'administration
        HttpSession session = request.getSession();
        session.setAttribute("accepted", accepted);
        RootingsUtils.redirectToPage(AfficherReseau.SELF_VIEW + "?" + AfficherReseau.USER_ID_PARAM + "=" + thisOne.id,
                                     request,
                                     response);
    }

    /**
     * Process a new friendship resolution request.
     *
     * @param accepted    The user's request already accepted.
     * @param toBeRemoved Notifications to drop.
     * @param fromUserId  This particular request user's id.
     * @param accept      Whether we do accept or not this friendship request.
     */
    protected void processRequest(List<User> accepted, Set<AbstractNotification> toBeRemoved, int fromUserId, boolean accept) throws SQLException {

        if (!UserRelationRequestsRepository.associationExists(fromUserId, thisOne.id)) {
            // On ne traite que les demandes réellement envoyées...
            return;
        }

        if (accept) {
            logger.info(MessageFormat.format("Approbation de la demande par {0} de l'utilisateur {1}.",
                                             thisOne.id,
                                             fromUserId));
            UserRelationsRepository.addAssociation(fromUserId, thisOne.id);
            UserRelationRequestsRepository.cancelRequest(fromUserId, thisOne.id);
            accepted.add(UsersRepository.getUser(fromUserId));
            NotificationsRepository.addNotification(fromUserId,
                                                    new NotifDemandeAcceptee(thisOne.id, thisOne.getName()));
        } else {
            logger.info(MessageFormat.format("Refus de la demande par {0} de l'utilisateur {1}.",
                                             thisOne.id,
                                             fromUserId));
            UserRelationRequestsRepository.cancelRequest(fromUserId, thisOne.id);
            NotificationsRepository.addNotification(fromUserId, new NotifDemandeRefusee(thisOne.id, thisOne.getName()));
        }

        // Si fromUserId avait supprimé sa relation avec userId
        toBeRemoved.addAll(NotificationsRepository.getNotifications(thisOne.id,
                                                                    NotificationType.FRIENDSHIP_DROPPED,
                                                                    ParameterName.USER_ID,
                                                                    fromUserId));
        // Si userId avait supprimé sa relation avec fromUserId
        toBeRemoved.addAll(NotificationsRepository.getNotifications(fromUserId,
                                                                    NotificationType.FRIENDSHIP_DROPPED,
                                                                    ParameterName.USER_ID,
                                                                    thisOne.id));
        // Si fromUserId avait refusé la demande de userId
        toBeRemoved.addAll(NotificationsRepository.getNotifications(thisOne.id,
                                                                    NotificationType.REJECTED_FRIENDSHIP,
                                                                    ParameterName.USER_ID,
                                                                    fromUserId));
        // Si userId avait supprimé sa relation avec fromUserId
        toBeRemoved.addAll(NotificationsRepository.getNotifications(fromUserId,
                                                                    NotificationType.REJECTED_FRIENDSHIP,
                                                                    ParameterName.USER_ID,
                                                                    thisOne.id));

        // Suppression des suggestions d'amitiés entre ces deux personnes
        toBeRemoved.addAll(NotificationsRepository.getNotifications(thisOne.id,
                                                                    NotificationType.NEW_RELATION_SUGGESTION,
                                                                    ParameterName.USER_ID,
                                                                    fromUserId));
        toBeRemoved.addAll(NotificationsRepository.getNotifications(fromUserId,
                                                                    NotificationType.NEW_RELATION_SUGGESTION,
                                                                    ParameterName.USER_ID,
                                                                    thisOne.id));

        // Suppression des demandes d'amis
        toBeRemoved.addAll(NotificationsRepository.getNotifications(fromUserId,
                                                                    NotificationType.NEW_FRIENSHIP_REQUEST,
                                                                    ParameterName.USER_ID,
                                                                    thisOne.id));
    }

}
