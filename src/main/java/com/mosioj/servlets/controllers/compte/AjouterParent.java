package com.mosioj.servlets.controllers.compte;

import java.sql.SQLException;
import java.text.MessageFormat;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mosioj.servlets.rootservlet.IdeesCadeauxPostServlet;
import com.mosioj.servlets.securitypolicy.generic.AllAccessToPostAndGet;
import com.mosioj.utils.RootingsUtils;
import com.mosioj.utils.database.NoRowsException;

@WebServlet("/protected/ajouter_parent")
public class AjouterParent extends IdeesCadeauxPostServlet<AllAccessToPostAndGet> {

	private static final long serialVersionUID = 7598797241503497392L;
	private static final Logger logger = LogManager.getLogger(AjouterParent.class);
	private static final String NAME_OR_EMAIL = "name";

	public AjouterParent() {
		super(new AllAccessToPostAndGet());
	}

	@Override
	public void ideesKDoPOST(HttpServletRequest request, HttpServletResponse response) throws ServletException, SQLException {

		String nameOrEmail = readNameOrEmail(request, NAME_OR_EMAIL);
		logger.debug(MessageFormat.format("Name or email reçu: {0}.", nameOrEmail));
		
		int parentId;
		try {
			parentId = model.users.getIdFromNameOrEmail(nameOrEmail);
			int userId = thisOne.id;
			if (!model.parentRelationship.doesRelationExists(parentId, userId) && parentId != userId) {
				logger.debug(MessageFormat.format("Ajout du parent: {0}.", parentId));
				model.parentRelationship.addProcuration(parentId, userId);
			}
		} catch (NoRowsException e) {
			logger.warn("L'ajout du parent a échoué : il n'existe pas de compte pour le nom ou l'email passé en paramètre.");
		}

		RootingsUtils.redirectToPage(MonCompte.URL, request, response);
	}

}
