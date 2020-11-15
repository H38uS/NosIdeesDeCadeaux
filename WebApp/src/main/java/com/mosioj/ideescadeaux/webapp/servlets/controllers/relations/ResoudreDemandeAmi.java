package com.mosioj.ideescadeaux.webapp.servlets.controllers.relations;

import com.mosioj.ideescadeaux.core.model.entities.User;
import com.mosioj.ideescadeaux.core.model.notifications.NType;
import com.mosioj.ideescadeaux.core.model.repositories.NotificationsRepository;
import com.mosioj.ideescadeaux.core.model.repositories.UserRelationRequestsRepository;
import com.mosioj.ideescadeaux.core.model.repositories.UserRelationsRepository;
import com.mosioj.ideescadeaux.core.model.repositories.UsersRepository;
import com.mosioj.ideescadeaux.webapp.servlets.rootservlet.IdeesCadeauxPostServlet;
import com.mosioj.ideescadeaux.webapp.servlets.securitypolicy.PeutResoudreDemandesAmis;
import com.mosioj.ideescadeaux.webapp.utils.RootingsUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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
    public void ideesKDoPOST(HttpServletRequest request, HttpServletResponse response) throws ServletException {

        // TODO faire un service
        List<User> accepted = new ArrayList<>();

        // Parcours des réponses
        policy.getChoiceParameters().forEach((fromUserId, accept) -> processRequest(accepted, fromUserId, accept));

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
     * @param accepted   The user's request already accepted.
     * @param fromUserId This particular request user's id.
     * @param accept     Whether we do accept or not this friendship request.
     */
    protected void processRequest(List<User> accepted, int fromUserId, boolean accept) {

        User fromUser = UsersRepository.getUser(fromUserId).orElse(null);
        if (fromUser == null) {
            // L'utilisateur n'existe pas...
            return;
        }

        if (!UserRelationRequestsRepository.associationExists(fromUserId, thisOne.id)) {
            // On ne traite que les demandes réellement envoyées...
            return;
        }

        try {
            if (accept) {
                logger.info("Approbation de la demande par {} de l'utilisateur {}.", thisOne, fromUser);
                try {
                    UserRelationsRepository.addAssociation(fromUserId, thisOne.id);
                    UserRelationRequestsRepository.cancelRequest(fromUserId, thisOne.id);
                    accepted.add(fromUser);
                    NType.ACCEPTED_FRIENDSHIP.with(thisOne).sendItTo(fromUser);
                } catch (SQLException e) {
                    logger.error("Fail to accept {} with error {}.", fromUser, e);
                }
            } else {
                logger.info("Refus de la demande par {} de l'utilisateur {}.", thisOne.id, fromUserId);
                UserRelationRequestsRepository.cancelRequest(fromUserId, thisOne.id);
                NType.REJECTED_FRIENDSHIP.with(thisOne).sendItTo(fromUser);
            }
        } catch (SQLException e) {
            logger.error(e);
        }

        // Si fromUser avait supprimé sa relation avec nous
        NotificationsRepository.terminator()
                               .whereOwner(thisOne)
                               .whereType(NType.FRIENDSHIP_DROPPED)
                               .whereUser(fromUser)
                               .terminates();
        // Si nous avions supprimé notre relation avec fromUser
        NotificationsRepository.terminator()
                               .whereOwner(fromUser)
                               .whereType(NType.FRIENDSHIP_DROPPED)
                               .whereUser(thisOne)
                               .terminates();
        // Si fromUser avait refusé notre demande
        NotificationsRepository.terminator()
                               .whereOwner(thisOne)
                               .whereType(NType.REJECTED_FRIENDSHIP)
                               .whereUser(fromUser)
                               .terminates();
        // Si nous avions supprimé notre relation avec fromUser
        NotificationsRepository.terminator()
                               .whereOwner(fromUser)
                               .whereType(NType.REJECTED_FRIENDSHIP)
                               .whereUser(thisOne)
                               .terminates();

        // Suppression des suggestions d'amitiés entre ces deux personnes
        NotificationsRepository.terminator()
                               .whereOwner(thisOne)
                               .whereType(NType.NEW_RELATION_SUGGESTION)
                               .whereUser(fromUser)
                               .terminates();
        NotificationsRepository.terminator()
                               .whereOwner(fromUser)
                               .whereType(NType.NEW_RELATION_SUGGESTION)
                               .whereUser(thisOne)
                               .terminates();

        // Suppression des demandes d'amis qu'on lui a faites
        NotificationsRepository.terminator()
                               .whereOwner(fromUser)
                               .whereType(NType.NEW_FRIENSHIP_REQUEST)
                               .whereUser(thisOne)
                               .terminates();
        // ... Et de toutes celles qu'on a reçues.
        NotificationsRepository.terminator()
                               .whereOwner(thisOne)
                               .whereType(NType.NEW_FRIENSHIP_REQUEST)
                               .terminates();
    }

}
