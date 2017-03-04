package com.mosioj.servlets.controllers.compte;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mosioj.model.User;
import com.mosioj.utils.ParametersUtils;
import com.mosioj.utils.RootingsUtils;
import com.mosioj.utils.validators.ParameterValidator;
import com.mosioj.utils.validators.ValidatorFactory;

@WebServlet("/protected/mon_compte")
public class MonCompte extends DefaultCompte {

	private static final long serialVersionUID = -101081965549681889L;
	public static final String VIEW_PAGE_URL = "/protected/mon_compte.jsp";
	private static final Logger logger = LogManager.getLogger(MonCompte.class);

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		int userId = ParametersUtils.getUserId(req);
		try {
			User current = users.getUser(userId);
			req.setAttribute("user", current);
		} catch (SQLException e) {
			RootingsUtils.rootToGenericSQLError(e, req, resp);
			return;
		}

		// FIXME afficher les relations
		// try {
		// List<Group> joined = groupes.getGroupsJoined(userId, -1);
		// req.setAttribute("joined", joined);
		//
		// Group owned = null;
		// try {
		// int groupId = groupes.getOwnerGroupId(userId);
		// owned = new Group(groupId, groupes.getName(groupId), -1, null);
		// } catch (NoRowsException e) {
		// // No groups created
		// }
		// req.setAttribute("owned", owned);
		//
		// } catch (SQLException e) {
		// e.printStackTrace();
		// RootingsUtils.rootToGenericSQLError(e, req, resp);
		// return;
		// }

		RootingsUtils.rootToPage(VIEW_PAGE_URL, req, resp);
	}

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String info = ParametersUtils.readIt(request, "modif_info_gen");
		if ("true".equals(info)) {
			String email = ParametersUtils.readIt(request, "email").trim();
			String name = ParametersUtils.readIt(request, "name").trim();

			int userId = ParametersUtils.getUserId(request);
			List<String> errors = checkEmail(getValidatorEmail(email), userId);
			request.setAttribute("errors_info_gen", errors);

			String birthday = ParametersUtils.readIt(request, "birthday");
			ParameterValidator val = ValidatorFactory.getFemValidator(birthday, "date d'anniversaire");
			val.checkDateFormat();
			errors.addAll(val.getErrors());

			try {
				User user = users.getUser(userId);
				user.email = email;
				user.name = name;
				user.birthday = getAsDate(birthday);
				request.setAttribute("user", user);

				if (errors.isEmpty()) {
					users.update(user);
				}
			} catch (SQLException e) {
				logger.error(e.getMessage());
				errors.add(e.getMessage());
				return;
			}
		}

		RootingsUtils.rootToPage(VIEW_PAGE_URL, request, response);
	}
}
