package com.mosioj.servlets.service;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringEscapeUtils;
import org.json.simple.JSONObject;

import com.mosioj.model.User;
import com.mosioj.servlets.IdeesCadeauxServlet;
import com.mosioj.servlets.securitypolicy.AllAccessToPostAndGet;
import com.mosioj.utils.ParametersUtils;

@WebServlet("/protected/service/name_resolver")
public class NameService extends IdeesCadeauxServlet {

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

			// Post from Javascript are already performed in UTF-8
			String param = ParametersUtils.readAndEscapeService(request, NAME_OR_EMAIL).toLowerCase();

			List<User> res = new ArrayList<User>();
			int MAX = 5;
			if (current.getEmail().toLowerCase().contains(param)
					|| (current.name != null && current.name.toLowerCase().contains(param))) {
				res.add(current);
				MAX--;
			}

			res.addAll(userRelations.getAllNamesOrEmailsInRelation(userId, param, 0, MAX));

			// Building the JSON answer
			StringBuilder resp = new StringBuilder();
			resp.append("[");
			for (User user : res) {
				resp.append("{");
				resp.append(JSONObject.toString("value", StringEscapeUtils.unescapeHtml4(user.getLongNameEmail())));
				resp.append("},");
			}
			resp.deleteCharAt(resp.length() - 1);
			resp.append("]");

			String content = resp.toString();
			if (response.getCharacterEncoding() != null) {
				content = new String(resp.toString().getBytes("UTF-8"), response.getCharacterEncoding());
			}
			response.getOutputStream().println(content);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void ideesKDoPOST(HttpServletRequest request, HttpServletResponse response) throws ServletException, SQLException {
		ideesKDoGET(request, response);
	}

}
