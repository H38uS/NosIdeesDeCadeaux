package com.mosioj.ideescadeaux.servlets.service;

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

import com.mosioj.ideescadeaux.model.User;
import com.mosioj.ideescadeaux.servlets.securitypolicy.NameServicePolicy;
import com.mosioj.ideescadeaux.utils.ParametersUtils;

@WebServlet("/protected/service/name_resolver")
public class NameService extends AbstractServiceGet<NameServicePolicy> {

	private static final long serialVersionUID = 9147880158497428623L;
	private static final String NAME_OR_EMAIL = "term";
	private static final String OF_USER_ID = "userId";

	public NameService() {
		super(new NameServicePolicy(OF_USER_ID));
	}

	@Override
	public void ideesKDoGET(HttpServletRequest request, HttpServletResponse response) throws ServletException, SQLException {
		try {
			// FIXME : 1 faire un test où y'a pas de paramètre + un test où c'est pas dans le réseau + quand ça trouve personne
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
			String[] resp = new String[res.size()];
			int i = 0;
			for (User user : res) {
				resp[i] = MessageFormat.format(	"{0},{1},{2},{3}",
												JSONObject.toString("value", StringEscapeUtils.unescapeHtml4(user.getLongNameEmail())),
												JSONObject.toString("email", StringEscapeUtils.unescapeHtml4(user.getEmail())),

												// nécessaire pour l'autocomplete...
												JSONObject.toString("label", StringEscapeUtils.unescapeHtml4(user.getEmail())),
												JSONObject.toString("imgsrc",
																	"protected/files/uploaded_pictures/avatars/"
																			+ user.getAvatarSrcSmall()));
				i++;
			}

			writter.writeJSonArrayOutput(response, resp);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
