package com.mosioj.ideescadeaux.webapp.servlets.service;

import com.mosioj.ideescadeaux.core.model.repositories.ParentRelationshipRepository;
import com.mosioj.ideescadeaux.core.model.repositories.UsersRepository;
import com.mosioj.ideescadeaux.webapp.servlets.rootservlet.IdeesCadeauxPostServlet;
import com.mosioj.ideescadeaux.webapp.servlets.securitypolicy.generic.AllAccessToPostAndGet;
import com.mosioj.ideescadeaux.webapp.servlets.service.response.ServiceResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.Optional;

@WebServlet("/protected/service/ajouter_parent")
public class AjouterParentService extends IdeesCadeauxPostServlet<AllAccessToPostAndGet> {

    private static final long serialVersionUID = 7598797241503497392L;
    private static final Logger logger = LogManager.getLogger(AjouterParentService.class);
    public static final String NAME_OR_EMAIL = "name";

    public AjouterParentService() {
        super(new AllAccessToPostAndGet());
    }

    @Override
    public void ideesKDoPOST(HttpServletRequest request, HttpServletResponse response) throws ServletException, SQLException {

        String nameOrEmail = readNameOrEmail(request, NAME_OR_EMAIL);
        logger.debug(MessageFormat.format("Name or email reçu: {0}.", nameOrEmail));

        String message;
        boolean status = true;

        Optional<Integer> parentId = UsersRepository.getIdFromNameOrEmail(nameOrEmail);
        if (parentId.isPresent()) {
            int userId = thisOne.id;
            if (ParentRelationshipRepository.noRelationExists(parentId.get(), userId) && parentId.get() != userId) {
                logger.debug(MessageFormat.format("Ajout du parent: {0}.", parentId));
                ParentRelationshipRepository.addProcuration(parentId.get(), userId);
                message = UsersRepository.getUser(parentId.get()).getName();
            } else {
                message = "L'ajout du parent a échoué : il existe déjà.";
                status = false;
            }
        } else {
            message = "L'ajout du parent a échoué : il n'existe pas de compte pour le nom ou l'email passé en paramètre.";
            status = false;
        }

        buildResponse(response, new ServiceResponse<>(status, message, isAdmin(request)));
    }
}
