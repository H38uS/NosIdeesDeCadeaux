package com.mosioj.servlets.controllers.idees;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;

import com.mosioj.model.User;
import com.mosioj.servlets.securitypolicy.NetworkAccess;
import com.mosioj.utils.ParametersUtils;

@WebServlet("/protected/voir_liste")
public class VoirListe extends MesListes {

	private static final long serialVersionUID = -5233551522645668356L;
	private static final String USER_ID_PARAM = "id";
	private static final String PROTECTED_VOIR_LIST = "/protected/voir_liste";
	public static final String VIEW_PAGE_URL = "/protected/mes_listes.jsp";

	/**
	 * Class constructor.
	 * 
	 */
	public VoirListe() {
		super(new NetworkAccess(userRelations, USER_ID_PARAM));
	}

	@Override
	protected List<User> getDisplayedEntities(int firstRow, HttpServletRequest req) throws SQLException {
		List<User> ids = new ArrayList<User>();
		User user = users.getUser(ParametersUtils.readInt(req, USER_ID_PARAM));
		ids.add(user);
		fillsUserIdeas(ParametersUtils.getUserId(req), ids);
		return ids;
	}

	@Override
	protected int getTotalNumberOfRecords(HttpServletRequest req) throws SQLException {
		return 1;
	}

	@Override
	protected String getCallingURL() {
		return PROTECTED_VOIR_LIST.substring(1);
	}

	@Override
	protected String getSpecificParameters(HttpServletRequest req) {
		return "";
	}

}
