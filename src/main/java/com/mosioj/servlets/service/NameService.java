package com.mosioj.servlets.service;

import java.io.IOException;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringEscapeUtils;
import org.json.simple.JSONObject;

import com.mosioj.model.User;
import com.mosioj.servlets.securitypolicy.AllAccessToPostAndGet;
import com.mosioj.utils.ParametersUtils;

@WebServlet("/protected/service/name_resolver")
public class NameService extends AbstractService<AllAccessToPostAndGet> {

	private static final long serialVersionUID = 9147880158497428623L;
	private static final String NAME_OR_EMAIL = "term";

	public NameService() {
		super(new AllAccessToPostAndGet());
	}

	@Override
	public void ideesKDoGET(HttpServletRequest request, HttpServletResponse response) throws ServletException, SQLException {
		try {

			int userId = ParametersUtils.getUserId(request);
			User current = users.getUser(userId);
			String param = ParametersUtils.readAndEscape(request, NAME_OR_EMAIL).toLowerCase();

			List<User> res = new ArrayList<User>();
			int MAX = 5;
			if (current.getEmail().toLowerCase().contains(param)
					|| (current.name != null && current.name.toLowerCase().contains(param))) {
				res.add(current);
				MAX--;
			}

			res.addAll(userRelations.getAllNamesOrEmailsInRelation(userId, param, 0, MAX));

			// Building the JSON answer
			String[] resp = new String[res.size()];
			int i = 0;
			for (User user : res) {
				resp[i] = MessageFormat.format(	"{0},{1}",
												JSONObject.toString("value", StringEscapeUtils.unescapeHtml4(user.getLongNameEmail())),
												JSONObject.toString("email", StringEscapeUtils.unescapeHtml4(user.getEmail())));
				i++;
			}

			writter.writeJSonArrayOutput(response, resp);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void ideesKDoPOST(HttpServletRequest request, HttpServletResponse response) throws ServletException, SQLException {
		ideesKDoGET(request, response);
	}

}
