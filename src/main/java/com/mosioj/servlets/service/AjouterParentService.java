package com.mosioj.servlets.service;

import java.sql.SQLException;
import java.text.MessageFormat;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mosioj.servlets.securitypolicy.generic.AllAccessToPostAndGet;
import com.mosioj.utils.database.NoRowsException;

@WebServlet("/protected/service/ajouter_parent")
public class AjouterParentService extends AbstractService<AllAccessToPostAndGet> {

	private static final long serialVersionUID = 7598797241503497392L;
	private static final Logger logger = LogManager.getLogger(AjouterParentService.class);
	private static final String NAME_OR_EMAIL = "name";

	public AjouterParentService() {
		super(new AllAccessToPostAndGet());
	}

	@Override
	public void ideesKDoGET(HttpServletRequest request, HttpServletResponse response) throws ServletException, SQLException {
		// RAS
	}

	@Override
	public void ideesKDoPOST(HttpServletRequest request, HttpServletResponse response) throws ServletException, SQLException {

		String nameOrEmail = readNameOrEmail(request, NAME_OR_EMAIL);
		logger.debug(MessageFormat.format("Name or email reçu: {0}.", nameOrEmail));
		
		int parentId;
		String status = "ok";
		String messageErreur = "";
		String name = "";

		try {
			parentId = model.users.getIdFromNameOrEmail(nameOrEmail);
			int userId = thisOne.id;
			if (!model.parentRelationship.doesRelationExists(parentId, userId) && parentId != userId) {
				logger.debug(MessageFormat.format("Ajout du parent: {0}.", parentId));
				model.parentRelationship.addProcuration(parentId, userId);
				name = model.users.getUser(parentId).getName();
			} else {
				messageErreur = "L'ajout du parent a échoué : il existe déjà.";
				status = "ko";
			}
		} catch (NoRowsException e) {
			messageErreur = "L'ajout du parent a échoué : il n'existe pas de compte pour le nom ou l'email passé en paramètre."; 
			logger.warn(messageErreur);
			status = "ko";
		}

		writter.writeJSonOutput(response, makeJSonPair("status", status), makeJSonPair("error_message", messageErreur), makeJSonPair("name", name));
	}

}
