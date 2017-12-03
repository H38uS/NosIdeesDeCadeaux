package com.mosioj.servlets.controllers;

import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mosioj.model.User;
import com.mosioj.servlets.IdeesCadeauxServlet;
import com.mosioj.servlets.controllers.relations.Page;
import com.mosioj.servlets.securitypolicy.SecurityPolicy;
import com.mosioj.utils.ParametersUtils;
import com.mosioj.utils.RootingsUtils;

public abstract class AbstractListes extends IdeesCadeauxServlet {

	private static final long serialVersionUID = 1638868138216657989L;
	private static final Logger LOGGER = LogManager.getLogger(AbstractListes.class);
	
	public static final String PROTECTED_MES_LISTES = "/protected/mes_listes";
	public static final String VIEW_PAGE_URL = "/protected/mes_listes.jsp";
	private static final String PAGE_ARG = "page";
	public static final int MAX_NUMBER_OF_RESULT = 20;

	public AbstractListes(SecurityPolicy policy) {
		super(policy);
	}

	protected abstract List<User> getDisplayedUsers(int userId, int firstRow, HttpServletRequest req) throws SQLException;
	protected abstract int getTotalNumberOfUsers(int userId, HttpServletRequest req) throws SQLException;
	protected abstract String getCallingURL();
	protected abstract String getSpecificParameters(HttpServletRequest req);

	@Override
	public void ideesKDoGET(HttpServletRequest req, HttpServletResponse resp) throws ServletException, SQLException {

		LOGGER.info(MessageFormat.format("Gets the lists for {0}", ParametersUtils.getUserName(req)));

		int pageNumber = getPageNumber(req, PAGE_ARG);
		int firstRow = getFirstRow(pageNumber, MAX_NUMBER_OF_RESULT);

		int userId = ParametersUtils.getUserId(req);

		List<User> ids = getDisplayedUsers(userId, firstRow, req);
		LOGGER.trace("Getting all ideas for all users...");
		for (User user : ids) {
			user.addIdeas(idees.getOwnerIdeas(user.id));
		}
		req.setAttribute("users", ids);
		
		int total = ids.size();
		if (total == MAX_NUMBER_OF_RESULT || pageNumber > 1) {
			// On regarde si y'en a pas d'autres
			total = getTotalNumberOfUsers(userId, req);
			if (total > MAX_NUMBER_OF_RESULT) {
				List<Page> pages = getPages(MAX_NUMBER_OF_RESULT, total);
				req.setAttribute("pages", pages);
				req.setAttribute("last", pages.size());
				req.setAttribute("call_back", getCallingURL());
				req.setAttribute("spec_parameters", getSpecificParameters(req));
			}
		}

		RootingsUtils.rootToPage(VIEW_PAGE_URL, req, resp);
	}
}
