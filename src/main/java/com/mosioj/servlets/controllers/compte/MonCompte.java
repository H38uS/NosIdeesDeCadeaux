package com.mosioj.servlets.controllers.compte;

import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mosioj.model.User;
import com.mosioj.servlets.securitypolicy.AllAccessToPostAndGet;
import com.mosioj.utils.ParametersUtils;
import com.mosioj.utils.RootingsUtils;
import com.mosioj.utils.validators.ParameterValidator;
import com.mosioj.utils.validators.ValidatorFactory;

@WebServlet("/protected/mon_compte")
public class MonCompte extends DefaultCompte {

	private static final long serialVersionUID = -101081965549681889L;
	private static final Logger logger = LogManager.getLogger(MonCompte.class);
	
	public static final String VIEW_PAGE_URL = "/protected/mon_compte.jsp";

	public MonCompte() {
		super(new AllAccessToPostAndGet());
	}

	@Override
	public void ideesKDoGET(HttpServletRequest req, HttpServletResponse resp) throws ServletException, SQLException {

		int userId = ParametersUtils.getUserId(req);
		User current = users.getUser(userId);
		req.setAttribute("user", current);

		// TODO pouvoir décider pour chaque notification si on l'active où non

		RootingsUtils.rootToPage(VIEW_PAGE_URL, req, resp);
	}

	@Override
	public void ideesKDoPOST(HttpServletRequest request, HttpServletResponse response) throws ServletException, SQLException {

		String info = ParametersUtils.readIt(request, "modif_info_gen");
		if ("true".equals(info)) {
			String email = ParametersUtils.readAndEscape(request, "email").trim();
			String name = ParametersUtils.readAndEscape(request, "name").trim();

			int userId = ParametersUtils.getUserId(request);
			List<String> errors = checkEmail(getValidatorEmail(email), userId);
			request.setAttribute("errors_info_gen", errors);

			String birthday = ParametersUtils.readAndEscape(request, "birthday");
			logger.debug(MessageFormat.format("Date de naissance: {0}", birthday));
			ParameterValidator val = ValidatorFactory.getFemValidator(birthday, "date d'anniversaire");
			val.checkDateFormat();
			errors.addAll(val.getErrors());

			User user = users.getUser(userId);
			user.email = email;
			user.name = name;
			user.birthday = getAsDate(birthday);
			request.setAttribute("user", user);

			if (errors.isEmpty()) {
				users.update(user);
			}
		}

		RootingsUtils.rootToPage(VIEW_PAGE_URL, request, response);
	}
}
