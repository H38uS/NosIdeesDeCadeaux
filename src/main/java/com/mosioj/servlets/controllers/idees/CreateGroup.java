package com.mosioj.servlets.controllers.idees;

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
import com.mosioj.servlets.securitypolicy.IdeaInteraction;
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
		super(new IdeaInteraction(userRelations, idees, IDEE_FIELD_PARAMETER));
	}

	@Override
	public void ideesKDoGET(HttpServletRequest req, HttpServletResponse resp) throws ServletException, SQLException {

		Integer id = ParametersUtils.readInt(req, IDEE_FIELD_PARAMETER);

		Idee idea = idees.getIdea(id); // forcément valide grace au check de securite
		req.setAttribute("idea", idea);
		RootingsUtils.rootToPage(VIEW_PAGE_URL, req, resp);
	}

	@Override
	public void ideesKDoPOST(HttpServletRequest request, HttpServletResponse response) throws ServletException, SQLException {

		Integer id = ParametersUtils.readInt(request, IDEE_FIELD_PARAMETER);
		Idee idea = idees.getIdea(id); // forcément valide grace au check de securite

		logger.debug("Create a new group for idea : " + idea.getId());

		ParameterValidator valTot = ValidatorFactory.getMascValidator(ParametersUtils.readIt(request, "total"), "total");
		valTot.checkEmpty();
		valTot.checkIfInteger();
		valTot.checkIntegerGreaterThan(-1);

		ParameterValidator valAmount = ValidatorFactory.getFemValidator(ParametersUtils.readIt(request, "amount"), "participation");
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

		int userId = ParametersUtils.getUserId(request);
		if (idees.canBook(idea.getId(), userId)) {
			int groupId = groupForIdea.createAGroup(total, amount, userId);
			idees.bookByGroup(id, groupId);
		}

		RootingsUtils.redirectToPage(MesListes.PROTECTED_MES_LISTES, request, response);

	}
}
