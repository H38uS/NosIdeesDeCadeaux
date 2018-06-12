package com.mosioj.servlets.controllers.compte;

import java.sql.SQLException;
import java.text.MessageFormat;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mosioj.servlets.securitypolicy.AllAccessToPostAndGet;
import com.mosioj.utils.ParametersUtils;
import com.mosioj.utils.RootingsUtils;

@WebServlet("/protected/ajouter_parent")
public class AjouterParent extends DefaultCompte {

	private static final long serialVersionUID = 7598797241503497392L;
	private static final Logger logger = LogManager.getLogger(AjouterParent.class);
	private static final String NAME_OR_EMAIL = "name";

	public AjouterParent() {
		super(new AllAccessToPostAndGet());
	}

	@Override
	public void ideesKDoGET(HttpServletRequest request, HttpServletResponse response) throws ServletException, SQLException {
		RootingsUtils.redirectToPage(MonCompte.URL, request, response);
	}

	@Override
	public void ideesKDoPOST(HttpServletRequest request, HttpServletResponse response) throws ServletException, SQLException {

		String nameOrEmail = readNameOrEmail(request, NAME_OR_EMAIL);
		logger.debug(MessageFormat.format("Name or email reçu: {0}.", nameOrEmail));
		
		int parentId = users.getIdFromNameOrEmail(nameOrEmail);
		logger.debug(MessageFormat.format("Ajout du parent: {0}.", parentId));
		parentRelationship.addProcuration(parentId, ParametersUtils.getUserId(request));

		RootingsUtils.redirectToPage(MonCompte.URL, request, response);
	}

}