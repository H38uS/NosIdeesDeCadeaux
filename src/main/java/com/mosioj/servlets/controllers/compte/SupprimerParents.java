package com.mosioj.servlets.controllers.compte;

import java.sql.SQLException;
import java.text.MessageFormat;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mosioj.model.User;
import com.mosioj.servlets.IdeesCadeauxServlet;
import com.mosioj.servlets.securitypolicy.AllAccessToPostAndGet;
import com.mosioj.utils.ParametersUtils;
import com.mosioj.utils.RootingsUtils;

@WebServlet("/protected/supprimer_parents")
public class SupprimerParents extends IdeesCadeauxServlet<AllAccessToPostAndGet> {

	private static final long serialVersionUID = 7598797241503497392L;
	private static final Logger logger = LogManager.getLogger(SupprimerParents.class);

	public SupprimerParents() {
		super(new AllAccessToPostAndGet());
	}

	@Override
	public void ideesKDoGET(HttpServletRequest request, HttpServletResponse response) throws ServletException, SQLException {
		RootingsUtils.redirectToPage(MonCompte.URL, request, response);
	}

	@Override
	public void ideesKDoPOST(HttpServletRequest request, HttpServletResponse response) throws ServletException, SQLException {

		User user = ParametersUtils.getConnectedUser(request);
		logger.debug(MessageFormat.format("Suppression des parents de {0}.", user));
		model.parentRelationship.deleteParents(user);

		RootingsUtils.redirectToPage(MonCompte.URL, request, response);
		}

}
