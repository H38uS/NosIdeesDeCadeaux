package com.mosioj.servlets.controllers.idees.reservation;

import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mosioj.model.Idee;
import com.mosioj.servlets.controllers.idees.AbstractIdea;
import com.mosioj.servlets.controllers.idees.MesListes;
import com.mosioj.servlets.securitypolicy.IdeaInteractionBookingUpToDate;
import com.mosioj.utils.ParametersUtils;
import com.mosioj.utils.RootingsUtils;
import com.mosioj.utils.validators.ParameterValidator;
import com.mosioj.utils.validators.ValidatorFactory;

@WebServlet("/protected/create_a_group")
public class CreateGroup extends AbstractIdea {

	private static final long serialVersionUID = -1774633803227715931L;
	private static final Logger logger = LogManager.getLogger(CreateGroup.class);

	private static final String IDEE_FIELD_PARAMETER = "idee";
	public static final String VIEW_PAGE_URL = "/protected/create_a_group.jsp";

	/**
	 * Class contructor
	 */
	public CreateGroup() {
		super(new IdeaInteractionBookingUpToDate(userRelations, idees, IDEE_FIELD_PARAMETER));
	}

	@Override
	public void ideesKDoGET(HttpServletRequest req, HttpServletResponse resp) throws ServletException, SQLException {
		Idee idea = getIdeeFromSecurityChecks();
		req.setAttribute("idee", idea);
		RootingsUtils.rootToPage(VIEW_PAGE_URL, req, resp);
	}

	@Override
	public void ideesKDoPOST(HttpServletRequest request, HttpServletResponse response) throws ServletException, SQLException {

		Integer id = ParametersUtils.readInt(request, IDEE_FIELD_PARAMETER);
		Idee idea = getIdeeFromSecurityChecks();

		logger.debug("Create a new group for idea : " + idea.getId());

		ParameterValidator valTot = ValidatorFactory.getMascValidator(ParametersUtils.readIt(request, "total"), "total");
		valTot.checkEmpty();
		valTot.checkIfAmount();
		valTot.checkIntegerGreaterThan(0);

		String amountString = ParametersUtils.readIt(request, "amount");
		ParameterValidator valAmount = ValidatorFactory.getFemValidator(amountString, "participation");
		valAmount.checkEmpty();
		valAmount.checkIfAmount();

		Double total = ParametersUtils.readDouble(request, "total");
		valAmount.checkDoubleAmount(0, total == null ? 0 : total);

		List<String> errors = valTot.getErrors();
		errors.addAll(valAmount.getErrors());

		if (!errors.isEmpty()) {
			request.setAttribute("errors", errors);
			request.setAttribute("idee", idea);
			request.setAttribute("total", total);
			request.setAttribute("amount", amountString);
			RootingsUtils.rootToPage(VIEW_PAGE_URL, request, response);
			return;
		}

		Double amount = ParametersUtils.readDouble(request, "amount");

		int userId = ParametersUtils.getUserId(request);
		Integer groupId = null;
		if (idees.canBook(idea.getId(), userId)) {
			groupId = groupForIdea.createAGroup(total, amount, userId);
			idees.bookByGroup(id, groupId);
		}

		if (groupId == null) {
			RootingsUtils.redirectToPage(MesListes.PROTECTED_MES_LISTES, request, response);
			return;
		}

		logger.debug(MessageFormat.format("Total: {0}, amount: {1}", total, amount));
		RootingsUtils.rootToPage(SuggestGroupIdea.VIEW_URL + "?" + SuggestGroupIdea.GROUP_ID_PARAM + "=" + groupId, request, response);
	}
}
