package com.mosioj.ideescadeaux.servlets.service;

import java.sql.SQLException;
import java.text.MessageFormat;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mosioj.ideescadeaux.servlets.rootservlet.IdeesCadeauxPostServlet;
import com.mosioj.ideescadeaux.servlets.service.response.ServiceResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mosioj.ideescadeaux.servlets.securitypolicy.generic.AllAccessToPostAndGet;
import com.mosioj.ideescadeaux.utils.database.NoRowsException;

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

        int parentId;
        boolean status = true;
        String message;

        try {
            parentId = model.users.getIdFromNameOrEmail(nameOrEmail);
            int userId = thisOne.id;
            if (model.parentRelationship.noRelationExists(parentId, userId) && parentId != userId) {
                logger.debug(MessageFormat.format("Ajout du parent: {0}.", parentId));
                model.parentRelationship.addProcuration(parentId, userId);
                message = model.users.getUser(parentId).getName();
            } else {
                message = "L'ajout du parent a échoué : il existe déjà.";
                status = false;
            }
        } catch (NoRowsException e) {
            message = "L'ajout du parent a échoué : il n'existe pas de compte pour le nom ou l'email passé en paramètre.";
            logger.warn(message);
            status = false;
        }

        buildResponse(response, new ServiceResponse(status, message, true, isAdmin(request)));
    }
}
