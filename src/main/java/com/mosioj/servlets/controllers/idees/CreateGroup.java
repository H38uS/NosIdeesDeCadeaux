package com.mosioj.servlets.controllers.idees;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mosioj.model.Idee;
import com.mosioj.servlets.controllers.MesListes;
import com.mosioj.utils.ParametersUtils;
import com.mosioj.utils.RootingsUtils;
import com.mosioj.utils.validators.ParameterValidator;
import com.mosioj.utils.validators.ValidatorFactory;

@WebServlet("/protected/create_a_group")
public class CreateGroup extends AbstractIdea {

	private static final long serialVersionUID = -1774633803227715931L;
	private static final Logger logger = LogManager.getLogger(CreateGroup.class);

	public static final String VIEW_PAGE_URL = "/protected/create_a_group.jsp";

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		Integer id = ParametersUtils.readInt(req, "idee");
		Idee idea = null;

		if (id != null) {
			try {
				idea = getIdeaWithAccessRightToInteract(req, id);
			} catch (SQLException e) {
				RootingsUtils.rootToGenericSQLError(e, req, resp);
				return;
			}
		} // FIXME faire une class abstraite qui gère cela... Et tous les niveaux de sécurité

		req.setAttribute("idea", idea);
		RootingsUtils.rootToPage(VIEW_PAGE_URL, req, resp);
	}

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		Integer id = ParametersUtils.readInt(request, "idee");
		Idee idea = null;

		if (id != null) {
			try {
				idea = getIdeaWithAccessRightToInteract(request, id);
			} catch (SQLException e) {
				RootingsUtils.rootToGenericSQLError(e, request, response);
				return;
			}
		}

		logger.debug("Create a new group for idea : " + idea.getId());

		ParameterValidator valTot = ValidatorFactory.getMascValidator(	ParametersUtils.readIt(request, "total"),
																		"total");
		valTot.checkEmpty();
		valTot.checkIfInteger();
		valTot.checkIntegerGreaterThan(-1);

		ParameterValidator valAmount = ValidatorFactory.getFemValidator(ParametersUtils.readIt(request, "amount"),
																		"participation");
		valAmount.checkEmpty();
		valAmount.checkIfInteger();

		Integer total = ParametersUtils.readInt(request, "total");
		valAmount.checkIntegerAmount(0, total == null ? 0 : total);

		List<String> errors = valTot.getErrors();
		errors.addAll(valAmount.getErrors());

		if (!errors.isEmpty()) {
			request.setAttribute("errors", errors);
			request.setAttribute("idea", idea);
			RootingsUtils.rootToPage(VIEW_PAGE_URL, request, response);
			return;
		}

		Integer amount = ParametersUtils.readInt(request, "amount");

		// FIXME : ne pas pouvoir créer de group sur ses idées...
		// FIXME : ne pas pouvoir créer de group si c'est déjà réservé...
		// FIXME : idem pour la résa
		
		try {
			int groupId = groupForIdea.createAGroup(total, amount, ParametersUtils.getUserId(request));
			idees.bookByGroup(id, groupId);
		} catch (SQLException e) {
			RootingsUtils.rootToGenericSQLError(e, request, response);
			return;
		}

		RootingsUtils.redirectToPage(MesListes.PROTECTED_MES_LISTES, request, response);
	}
}
