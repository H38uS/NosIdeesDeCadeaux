package com.mosioj.ideescadeaux.webapp.servlets.service;

import com.mosioj.ideescadeaux.core.model.repositories.ParentRelationshipRepository;
import com.mosioj.ideescadeaux.core.model.repositories.UsersRepository;
import com.mosioj.ideescadeaux.webapp.servlets.rootservlet.IdeesCadeauxPostServlet;
import com.mosioj.ideescadeaux.webapp.servlets.securitypolicy.generic.AllAccessToPostAndGet;
import com.mosioj.ideescadeaux.webapp.servlets.service.response.ServiceResponse;
import com.mosioj.ideescadeaux.webapp.utils.ParametersUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.SQLException;
import java.text.MessageFormat;

@WebServlet("/protected/service/ajouter_parent")
public class AjouterParentService extends IdeesCadeauxPostServlet<AllAccessToPostAndGet> {

    private static final long serialVersionUID = 7598797241503497392L;
    private static final Logger logger = LogManager.getLogger(AjouterParentService.class);
    public static final String NAME_OR_EMAIL = "name";

    public AjouterParentService() {
        super(new AllAccessToPostAndGet());
    }

    @Override
    public void ideesKDoPOST(HttpServletRequest request,
                             HttpServletResponse response) throws ServletException, SQLException {

        String nameOrEmail = ParametersUtils.readNameOrEmail(request, NAME_OR_EMAIL);
        logger.debug(MessageFormat.format("Name or email reçu: {0}.", nameOrEmail));

        ServiceResponse<String> resp;

        Integer parentId = UsersRepository.getIdFromNameOrEmail(nameOrEmail).orElse(null);
        if (parentId != null) {
            int userId = thisOne.id;
            if (parentId == userId) {
                resp = ServiceResponse.ko("Vous ne pouvez pas vous ajouter vous-même...", isAdmin(request), thisOne);
            } else if (ParentRelationshipRepository.noRelationExists(parentId, userId)) {
                logger.debug(MessageFormat.format("Ajout du parent: {0}.", parentId));
                ParentRelationshipRepository.addProcuration(parentId, userId);
                resp = ServiceResponse.ok(UsersRepository.getUser(parentId)
                                                         .orElseThrow(SQLException::new)
                                                         .getName(), isAdmin(request), thisOne);
            } else {
                resp = ServiceResponse.ko("L'ajout du parent a échoué : il existe déjà.", isAdmin(request), thisOne);
            }
        } else {
            resp = ServiceResponse.ko(
                    "L'ajout du parent a échoué : il n'existe pas de compte pour le nom ou l'email passé en paramètre.",
                    isAdmin(request),
                    thisOne);
        }

        buildResponse(response, resp);
    }
}
