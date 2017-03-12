package com.mosioj.servlets.controllers;

import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

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

@WebServlet("/protected/mes_listes")
public class MesListes extends IdeesCadeauxServlet {
	/**
	 * Class logger.
	 */
	private static final Logger LOGGER = LogManager.getLogger(MesListes.class);
	private static final long serialVersionUID = -1774633803227715931L;

	public static final String PROTECTED_MES_LISTES = "/protected/mes_listes";
	public static final String VIEW_PAGE_URL = "/protected/mes_listes.jsp";

	/**
	 * Class constructor.
	 * 
	 */
	public MesListes() {
		super(new AllAccessToPostAndGet());
	}

	@Override
	public void ideesKDoGET(HttpServletRequest req, HttpServletResponse resp) throws ServletException, SQLException {

		LOGGER.info(MessageFormat.format("Gets the lists for {0}", ParametersUtils.getUserName(req)));

		int userId = ParametersUtils.getUserId(req);

		List<User> ids = new ArrayList<User>();
		ids.add(users.getUser(userId));
		ids.addAll(userRelations.getAllUsersInRelation(userId));
		LOGGER.trace("Getting all ideas for all users...");
		for (User user : ids) {
			user.addIdeas(idees.getOwnerIdeas(user.id));
		}
		req.setAttribute("users", ids);

		RootingsUtils.rootToPage(VIEW_PAGE_URL, req, resp);
	}

	@Override
	public void ideesKDoPOST(HttpServletRequest request, HttpServletResponse response) throws ServletException {
		RootingsUtils.redirectToPage(PROTECTED_MES_LISTES, request, response); // Rien de spécifique pour le moment
		// TODO : pouvoir demander des informations et/ou discuter avec d'autres membres
	}

}
