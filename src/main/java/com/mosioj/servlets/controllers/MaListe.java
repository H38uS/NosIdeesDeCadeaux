package com.mosioj.servlets.controllers;

import java.io.IOException;
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

import com.mosioj.model.Categorie;
import com.mosioj.model.Idee;
import com.mosioj.model.Priorite;
import com.mosioj.servlets.IdeesCadeauxServlet;
import com.mosioj.utils.ParametersUtils;
import com.mosioj.utils.RootingsUtils;
import com.mosioj.utils.validators.ParameterValidator;

@WebServlet("/protected/ma_liste")
public class MaListe extends IdeesCadeauxServlet {

	/**
	 * Class logger.
	 */
	private static final Logger logger = LogManager.getLogger(MaListe.class);

	/**
	 * 
	 */
	private static final long serialVersionUID = -1774633803227715931L;

	public static final String VIEW_PAGE_URL = "/protected/ma_liste.jsp";

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		List<Idee> ideas = null;
		List<Categorie> cat = null;
		List<Priorite> prio = null;
		try {
			ideas = idees.getOwnerIdeas(ParametersUtils.getUserId(req));
			cat = categories.getCategories();
			prio = priorities.getPriorities();
		} catch (SQLException e) {
			RootingsUtils.rootToGenericSQLError(e, req, resp);
			return;
		}
		req.setAttribute("idees", ideas);
		req.setAttribute("types", cat);
		req.setAttribute("priorites", prio);
		RootingsUtils.rootToPage(VIEW_PAGE_URL, req, resp);
	}

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		// TODO échapper comme avant (<span, les <br, etc.)

		// Reading parameters
		String text = ParametersUtils.readIt(request, "text").trim();
		String type = ParametersUtils.readIt(request, "type").trim();
		String priority = ParametersUtils.readIt(request, "priority").trim();

		ParameterValidator valText = new ParameterValidator(text, "text", "Le ");
		valText.checkEmpty();

		ParameterValidator valPrio = new ParameterValidator(priority, "priorité", "La ");
		valPrio.checkEmpty();
		valPrio.checkIfInteger();

		List<String> errors = new ArrayList<String>();
		errors.addAll(valText.getErrors());
		errors.addAll(valPrio.getErrors());

		if (!errors.isEmpty()) {
			request.setAttribute("errors", errors);
		} else {
			try {
				logger.info(MessageFormat.format(	"Adding a new idea [''{0}'' / ''{1}'' / ''{2}'']",
													text,
													type,
													priority));
				idees.addIdea(ParametersUtils.getUserId(request), text, type, priority);
			} catch (SQLException e) {
				RootingsUtils.rootToGenericSQLError(e, request, response);
				return;
			}
		}

		doGet(request, response);
	}

}
