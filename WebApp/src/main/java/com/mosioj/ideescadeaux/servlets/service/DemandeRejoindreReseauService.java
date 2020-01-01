package com.mosioj.ideescadeaux.servlets.service;

import com.mosioj.ideescadeaux.model.entities.User;
import com.mosioj.ideescadeaux.model.repositories.NotificationsRepository;
import com.mosioj.ideescadeaux.model.repositories.UserRelationRequestsRepository;
import com.mosioj.ideescadeaux.model.repositories.UserRelationsRepository;
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

        ServiceResponse<String> ans;
        try {
            User userToSendInvitation = policy.getUser();
            request.setAttribute("name", userToSendInvitation.getName());

            if (UserRelationRequestsRepository.associationExists(thisOne.id, userToSendInvitation.id)) {
                buildResponse(response,
                              ServiceResponse.ko(MessageFormat.format("Vous avez déjà envoyé une demande à {0}.",
                                                                      userToSendInvitation.getName()),
                                                 isAdmin(request)));
                return;
            }

            if (UserRelationsRepository.associationExists(thisOne.id, userToSendInvitation.id)) {
                buildResponse(response,
                              ServiceResponse.ko(MessageFormat.format("Vous faites déjà parti du réseau de {0}.",
                                                                      userToSendInvitation.getName()),
                                                 isAdmin(request)));
                return;
            }

            // Suppression des notifications
            NotificationsRepository.removeAllType(thisOne,
                                                  NotificationType.NEW_RELATION_SUGGESTION,
                                                  ParameterName.USER_ID,
                                                  userToSendInvitation.id);
            NotificationsRepository.removeAllType(userToSendInvitation,
                                                  NotificationType.NEW_RELATION_SUGGESTION,
                                                  ParameterName.USER_ID,
                                                  thisOne);

            // On ajoute l'association
            UserRelationRequestsRepository.insert(thisOne, userToSendInvitation);
            NotificationsRepository.addNotification(userToSendInvitation.id,
                                                    new NotifNouvelleDemandeAmi(thisOne,
                                                                      userToSendInvitation.id,
                                                                      thisOne.getName()));

            ans = ServiceResponse.ok("", isAdmin(request));
        } catch (SQLException e) {
            ans = ServiceResponse.ko(e.getMessage(), isAdmin(request));
            logger.warn(e);
        }

        buildResponse(response, ans);
    }

}
