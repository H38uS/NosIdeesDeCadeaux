package com.mosioj.ideescadeaux.servlets.service;

import com.mosioj.ideescadeaux.model.entities.User;
import com.mosioj.ideescadeaux.notifications.NotificationType;
import com.mosioj.ideescadeaux.notifications.ParameterName;
import com.mosioj.ideescadeaux.notifications.instance.NotifNouvelleDemandeAmi;
import com.mosioj.ideescadeaux.servlets.rootservlet.IdeesCadeauxPostServlet;
import com.mosioj.ideescadeaux.servlets.securitypolicy.PeutDemanderARejoindreLeReseau;
import com.mosioj.ideescadeaux.servlets.service.response.ServiceResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.SQLException;
import java.text.MessageFormat;

@WebServlet("/protected/service/demande_rejoindre_reseau")
public class DemandeRejoindreReseauService extends IdeesCadeauxPostServlet<PeutDemanderARejoindreLeReseau> {

    private static final long serialVersionUID = 3683476983071872342L;
    private static final Logger logger = LogManager.getLogger(DemandeRejoindreReseauService.class);

    public static final String USER_ID_PARAM = "user_id";

    public DemandeRejoindreReseauService() {
        super(new PeutDemanderARejoindreLeReseau(USER_ID_PARAM));
    }

    @Override
    public void ideesKDoPOST(HttpServletRequest request, HttpServletResponse response) {

        ServiceResponse ans;
        try {
            User userToSendInvitation = policy.getUser();
            request.setAttribute("name", userToSendInvitation.getName());

            if (model.userRelationRequests.associationExists(thisOne.id, userToSendInvitation.id)) {
                throw new SQLException(MessageFormat.format("vous avez déjà envoyé une demande à {0}.",
                                                            userToSendInvitation.getName()));
            }

            if (model.userRelations.associationExists(thisOne.id, userToSendInvitation.id)) {
                throw new SQLException(MessageFormat.format("vous êtes déjà ami avec {0}.",
                                                            userToSendInvitation.getName()));
            }

            // Suppression des notifications
            model.notif.removeAllType(thisOne,
                                      NotificationType.NEW_RELATION_SUGGESTION,
                                      ParameterName.USER_ID,
                                      userToSendInvitation.id);
            model.notif.removeAllType(userToSendInvitation,
                                      NotificationType.NEW_RELATION_SUGGESTION,
                                      ParameterName.USER_ID,
                                      thisOne);

            // On ajoute l'association
            model.userRelationRequests.insert(thisOne, userToSendInvitation);
            model.notif.addNotification(userToSendInvitation.id,
                                        new NotifNouvelleDemandeAmi(thisOne,
                                                                    userToSendInvitation.id,
                                                                    thisOne.getName()));

            ans = ServiceResponse.ok("", true, isAdmin(request));
        } catch (SQLException e) {
            ans = ServiceResponse.ko(e.getMessage(), true, isAdmin(request));
            logger.warn(e);
        }

        buildResponse(response, ans);
    }

}
