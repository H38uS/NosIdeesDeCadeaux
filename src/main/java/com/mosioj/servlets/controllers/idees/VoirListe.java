package com.mosioj.servlets.controllers.idees;

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
import com.mosioj.servlets.securitypolicy.NetworkAccess;
import com.mosioj.utils.ParametersUtils;
import com.mosioj.utils.RootingsUtils;

@WebServlet("/protected/voir_liste")
public class VoirListe extends IdeesCadeauxServlet {

	private static final Logger LOGGER = LogManager.getLogger(VoirListe.class);
	private static final long serialVersionUID = -5233551522645668356L;
	private static final String USER_ID_PARAM = "id";
	private static final String PROTECTED_VOIR_LIST = "/protected/voir_liste";
	public static final String VIEW_PAGE_URL = "/protected/voir_liste.jsp";

	/**
	 * Class constructor.
	 * 
	 */
	public VoirListe() {
		super(new NetworkAccess(userRelations, USER_ID_PARAM));
	}

	@Override
	public void ideesKDoGET(HttpServletRequest req, HttpServletResponse resp) throws ServletException, SQLException {

		User user = users.getUser(ParametersUtils.readInt(req, USER_ID_PARAM));
		LOGGER.info(MessageFormat.format("Gets the lists for {0}", user.getName()));

		LOGGER.trace("Getting all ideas for this user...");
		user.addIdeas(idees.getOwnerIdeas(user.id));
		req.setAttribute("user", user);

		RootingsUtils.rootToPage(VIEW_PAGE_URL, req, resp);
	}

	@Override
	public void ideesKDoPOST(HttpServletRequest request, HttpServletResponse response) throws ServletException, SQLException {
		RootingsUtils.redirectToPage(PROTECTED_VOIR_LIST, request, response); // Rien de spécifique pour le moment
		// TODO : pouvoir demander des informations et/ou discuter avec d'autres membres
	}

}
