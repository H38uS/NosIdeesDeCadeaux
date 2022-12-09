package com.mosioj.ideescadeaux.webapp.servlets.service;

import com.mosioj.ideescadeaux.core.model.entities.User;
import com.mosioj.ideescadeaux.core.model.repositories.ParentRelationshipRepository;
import com.mosioj.ideescadeaux.core.model.repositories.UsersRepository;
import com.mosioj.ideescadeaux.webapp.servlets.rootservlet.ServicePost;
import com.mosioj.ideescadeaux.webapp.servlets.securitypolicy.generic.AllAccessToPostAndGet;
import com.mosioj.ideescadeaux.webapp.servlets.service.response.ServiceResponse;
import com.mosioj.ideescadeaux.webapp.utils.ParametersUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.List;

@WebServlet("/protected/service/ajouter_parent")
public class ServiceAjouterParent extends ServicePost<AllAccessToPostAndGet> {

    private static final Logger logger = LogManager.getLogger(ServiceAjouterParent.class);
    public static final String NAME_OR_EMAIL = "name";

    public ServiceAjouterParent() {
        super(new AllAccessToPostAndGet());
    }

    @Override
    public void servicePost(HttpServletRequest request, HttpServletResponse response) throws SQLException {

        String nameOrEmail = ParametersUtils.readNameOrEmail(request, NAME_OR_EMAIL, true);
        logger.debug(MessageFormat.format("Name or email reçu: {0}.", nameOrEmail));

        ServiceResponse<String> resp;

        List<User> possibleParents = UsersRepository.getUserFromNameOrEmail(nameOrEmail);
        if (possibleParents.size() == 1) {
            int userId = thisOne.id;
            final int parentId = possibleParents.get(0).id;
            if (parentId == userId) {
                resp = ServiceResponse.ko("Vous ne pouvez pas vous ajouter vous-même...", thisOne);
            } else if (ParentRelationshipRepository.noRelationExists(parentId, userId)) {
                logger.debug(MessageFormat.format("Ajout du parent: {0}.", parentId));
                ParentRelationshipRepository.addProcuration(parentId, userId);
                resp = ServiceResponse.ok(UsersRepository.getUser(parentId)
                                                         .orElseThrow(SQLException::new)
                                                         .getName(), thisOne);
            } else {
                resp = ServiceResponse.ko("L'ajout du parent a échoué : il existe déjà.", thisOne);
            }
        } else {
            resp = ServiceResponse.ko(
                    "L'ajout du parent a échoué : il n'existe pas (ou trop) de compte pour le nom ou l'email passé en paramètre.",
                    thisOne);
        }

        buildResponse(response, resp);
    }
}
