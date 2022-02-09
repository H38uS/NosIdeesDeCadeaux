package com.mosioj.ideescadeaux.webapp.servlets.service;

import com.mosioj.ideescadeaux.core.model.entities.User;
import com.mosioj.ideescadeaux.core.model.notifications.NType;
import com.mosioj.ideescadeaux.core.model.repositories.NotificationsRepository;
import com.mosioj.ideescadeaux.core.model.repositories.UserRelationRequestsRepository;
import com.mosioj.ideescadeaux.core.model.repositories.UserRelationsRepository;
import com.mosioj.ideescadeaux.webapp.servlets.rootservlet.ServicePost;
import com.mosioj.ideescadeaux.webapp.servlets.securitypolicy.PeutDemanderARejoindreLeReseau;
import com.mosioj.ideescadeaux.webapp.servlets.service.response.ServiceResponse;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.SQLException;
import java.text.MessageFormat;

import static com.mosioj.ideescadeaux.core.model.notifications.NType.NEW_FRIENSHIP_REQUEST;

@WebServlet("/protected/service/demande_rejoindre_reseau")
public class ServiceDemandeRejoindreReseau extends ServicePost<PeutDemanderARejoindreLeReseau> {

    private static final long serialVersionUID = 3683476983071872342L;
    public static final String USER_ID_PARAM = "user_id";

    public ServiceDemandeRejoindreReseau() {
        super(new PeutDemanderARejoindreLeReseau(USER_ID_PARAM));
    }

    @Override
    public void servicePost(HttpServletRequest request, HttpServletResponse response) throws SQLException {

        User userToSendInvitation = policy.getUser();
        request.setAttribute("name", userToSendInvitation.getName());

        if (UserRelationRequestsRepository.associationExists(thisOne, userToSendInvitation)) {
            buildResponse(response,
                          ServiceResponse.ko(MessageFormat.format("Vous avez déjà envoyé une demande à {0}.",
                                                                  userToSendInvitation.getName()),
                                             isAdmin(request), thisOne));
            return;
        }

        if (UserRelationsRepository.associationExists(thisOne, userToSendInvitation)) {
            buildResponse(response,
                          ServiceResponse.ko(MessageFormat.format("Vous faites déjà parti du réseau de {0}.",
                                                                  userToSendInvitation.getName()),
                                             isAdmin(request), thisOne));
            return;
        }

        // Suppression des notifications
        NotificationsRepository.terminator()
                               .whereOwner(thisOne)
                               .whereType(NType.NEW_RELATION_SUGGESTION)
                               .whereUser(userToSendInvitation)
                               .terminates();
        NotificationsRepository.terminator()
                               .whereOwner(userToSendInvitation)
                               .whereType(NType.NEW_RELATION_SUGGESTION)
                               .whereUser(thisOne)
                               .terminates();

        // On ajoute l'association
        UserRelationRequestsRepository.insert(thisOne, userToSendInvitation);
        NEW_FRIENSHIP_REQUEST.with(thisOne).sendItTo(userToSendInvitation);

        buildResponse(response, ServiceResponse.ok(StringUtils.EMPTY, isAdmin(request), thisOne));
    }

}
