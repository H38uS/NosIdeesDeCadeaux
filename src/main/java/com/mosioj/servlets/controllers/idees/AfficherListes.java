package com.mosioj.servlets.controllers.idees;

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

@WebServlet("/protected/afficher_listes")
public class AfficherListes extends IdeesCadeauxServlet {

	/**
	 * Class logger.
	 */
	private static final Logger LOGGER = LogManager.getLogger(AfficherListes.class);
	private static final long serialVersionUID = 1209953017190072617L;

	public static final String AFFICHER_LISTES = "/protected/afficher_listes";
	public static final String VIEW_PAGE_URL = "/protected/mes_listes.jsp";

	private static final String NAME_OR_EMAIL = "name";

	/**
	 * Class constructor.
	 * 
	 */
	public AfficherListes() {
		super(new AllAccessToPostAndGet());
	}

	@Override
	public void ideesKDoGET(HttpServletRequest req, HttpServletResponse resp) throws ServletException, SQLException {

		String nameOrEmail = ParametersUtils.readAndEscape(req, NAME_OR_EMAIL);
		int userId = ParametersUtils.getUserId(req);

		LOGGER.info(MessageFormat.format("Gets the lists for {0}, with token {1}", ParametersUtils.getUserName(req), nameOrEmail));

		List<User> ids = new ArrayList<User>();
		User connected = users.getUser(userId);
		if (connected.matchNameOrEmail(nameOrEmail)) {
			ids.add(connected);
		}
		ids.addAll(userRelations.getAllUsersInRelation(userId, nameOrEmail));
		for (User user : ids) {
			user.addIdeas(idees.getOwnerIdeas(user.id));
		}
		req.setAttribute("users", ids);

		RootingsUtils.rootToPage(VIEW_PAGE_URL, req, resp);
	}

	@Override
	public void ideesKDoPOST(HttpServletRequest request, HttpServletResponse response) throws ServletException {
		RootingsUtils.redirectToPage(	MessageFormat.format(	"{0}?{1}={2}",
																AFFICHER_LISTES,
																NAME_OR_EMAIL,
																ParametersUtils.readIt(request, NAME_OR_EMAIL)),
										request,
										response); // Rien de spécifique pour le moment
	}

}
