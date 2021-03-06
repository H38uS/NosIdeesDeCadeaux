package com.mosioj.ideescadeaux.webapp.servlets.controllers.compte;

import java.sql.SQLException;
import java.text.MessageFormat;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mosioj.ideescadeaux.webapp.servlets.rootservlet.IdeesCadeauxPostServlet;
import com.mosioj.ideescadeaux.webapp.servlets.securitypolicy.generic.AllAccessToPostAndGet;
import com.mosioj.ideescadeaux.core.model.repositories.ParentRelationshipRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mosioj.ideescadeaux.core.model.entities.User;
import com.mosioj.ideescadeaux.webapp.utils.RootingsUtils;

@WebServlet("/protected/supprimer_parents")
public class SupprimerParents extends IdeesCadeauxPostServlet<AllAccessToPostAndGet> {

    private static final long serialVersionUID = 7598797241503497392L;
    private static final Logger logger = LogManager.getLogger(SupprimerParents.class);

    public SupprimerParents() {
        super(new AllAccessToPostAndGet());
    }

    @Override
    public void ideesKDoPOST(HttpServletRequest request, HttpServletResponse response) throws ServletException, SQLException {

        User user = thisOne;
        logger.debug(MessageFormat.format("Suppression des parents de {0}.", user));
        ParentRelationshipRepository.deleteParents(user);

        RootingsUtils.redirectToPage(MonCompte.URL, request, response);
    }

}
