package com.mosioj.servlets.controllers.groups;

import java.io.IOException;
import java.sql.SQLException;
import java.text.MessageFormat;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mosioj.servlets.IdeesCadeauxServlet;
import com.mosioj.utils.RootingsUtils;

public class DefaultGroupServlet extends IdeesCadeauxServlet {

	private static final long serialVersionUID = -6873565206075352242L;
	private static final Logger logger = LogManager.getLogger(DefaultGroupServlet.class);

	/**
	 * 
	 * @param request
	 * @param response
	 * @param groupId
	 * @param userId
	 * @return True if and only if the user is the true owner of this group.
	 * @throws IOException
	 * @throws ServletException
	 * @throws SQLException
	 */
	protected boolean isGroupOwner(HttpServletRequest request, HttpServletResponse response, int groupId, int userId)
			throws ServletException, IOException, SQLException {
		if (!groupes.isGroupOwner(userId, groupId)) {
			logger.error(MessageFormat.format(	"Essai de l''utilisateur {0} d''administrer le groupe {1}.",
												userId,
												groupId));
			request.setAttribute("error_message", "Vous ne pouvez administrer que vos groupes.");
			RootingsUtils.rootToPage(AdministrationGroupe.ERROR_URL, request, response);
			return false;
		}
		return true;
	}

	/**
	 * 
	 * @param request
	 * @param response
	 * @param groupId
	 * @param userId
	 * @return True if and only if the user is the true owner of this group.
	 * @throws IOException
	 * @throws ServletException
	 * @throws SQLException
	 */
	protected boolean isAdminOf(HttpServletRequest request, HttpServletResponse response, int groupId, int userId)
			throws ServletException, IOException, SQLException {
		if (!groupes.isAdminOf(groupId, userId)) {
			logger.error(MessageFormat.format(	"Essaie de l''utilisateur {0} d''administrer le groupe {1}.",
												userId,
												groupId));
			request.setAttribute("error_message", "Vous ne pouvez administrer que votre groupe.");
			RootingsUtils.rootToPage(AdministrationGroupe.ERROR_URL, request, response);
			return false;
		}
		return true;
	}

	/**
	 * Redirect to the admin page.
	 * 
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	protected void redirectToAdminPage(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		ServletContext context = getServletContext();
		RequestDispatcher rd = context.getRequestDispatcher("/protected/administration_groupe");
		rd.forward(request, response);
	}

}
