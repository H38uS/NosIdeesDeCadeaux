package com.mosioj.ideescadeaux.servlets.service;

import java.io.IOException;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;

import com.google.gson.annotations.Expose;
import com.mosioj.ideescadeaux.model.entities.User;
import com.mosioj.ideescadeaux.servlets.securitypolicy.NameServicePolicy;
import com.mosioj.ideescadeaux.utils.GsonFactory;
import com.mosioj.ideescadeaux.utils.ParametersUtils;

@WebServlet("/protected/service/name_resolver")
public class NameService extends AbstractServiceGet<NameServicePolicy> {

	private static final Logger logger = LogManager.getLogger(NameService.class);
	private static final long serialVersionUID = 9147880158497428623L;
	private static final String NAME_OR_EMAIL = "term";
	private static final String OF_USER_ID = "userId";

	public NameService() {
		super(new NameServicePolicy(OF_USER_ID));
	}

	@Override
	public void ideesKDoGET(HttpServletRequest request, HttpServletResponse response) throws ServletException, SQLException {

		// FIXME : 1 faire un test où y'a pas de paramètre + un test où c'est pas dans le réseau + quand ça trouve
		// personne
		User current = policy.getRootNetwork();
		String param = ParametersUtils.readAndEscape(request, NAME_OR_EMAIL).toLowerCase();

		List<User> res = new ArrayList<User>();
		int MAX = 5;
		if (current.getEmail().toLowerCase().contains(param)
				|| (StringEscapeUtils.unescapeHtml4(current.getName().toLowerCase()).contains(param))
				|| "moi".equalsIgnoreCase(param)) {
			res.add(current);
			MAX--;
		}

		res.addAll(model.userRelations.getAllNamesOrEmailsInRelation(current.id, param, 0, MAX));

		// Building the JSON answer
		List<NameAnswer> users = res.stream().map(u -> new NameAnswer(u)).collect(Collectors.toList());

		// Do not user ServiceResponse because specific format is needed for JQuery
		String content = GsonFactory.getIt().toJson(users);
		try {
			content = new String(content.getBytes("UTF-8"), response.getCharacterEncoding());
			response.getOutputStream().print(content);
		} catch (IOException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
	}

	private class NameAnswer {

		@Expose
		private final String value;

		@Expose
		private final String email;

		@Expose
		private final String label;

		@Expose
		private final String imgsrc;

		public NameAnswer(User user) {
			value = StringEscapeUtils.unescapeHtml4(user.getLongNameEmail());
			email = StringEscapeUtils.unescapeHtml4(user.getEmail());
			label = StringEscapeUtils.unescapeHtml4(user.getEmail());
			imgsrc = "protected/files/uploaded_pictures/avatars/" + user.getAvatarSrcSmall();
		}
	}
}
