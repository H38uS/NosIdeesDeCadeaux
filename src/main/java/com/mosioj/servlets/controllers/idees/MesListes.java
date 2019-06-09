package com.mosioj.servlets.controllers.idees;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;

import com.mosioj.model.User;
import com.mosioj.servlets.securitypolicy.NetworkAccess;
import com.mosioj.utils.NotLoggedInException;

@WebServlet("/protected/mes_listes")
public class MesListes extends AbstractUserListes<NetworkAccess> {

	private static final long serialVersionUID = -1774633803227715931L;
	public static final String PROTECTED_MES_LISTES = "/protected/mes_listes";

	/**
	 * Class constructor.
	 * 
	 */
	public MesListes(NetworkAccess policy) {
		super(policy);
	}

	@Override
	protected List<User> getDisplayedEntities(int firstRow, HttpServletRequest req) throws SQLException, NotLoggedInException {
		User user = thisOne;
		List<User> ids = new ArrayList<User>();
		if (firstRow == 0) {
			ids.add(user);
		}
		ids.addAll(model.userRelations.getAllUsersInRelation(user, firstRow, maxNumberOfResults));
		fillsUserIdeas(user, ids);
		return ids;
	}

	@Override
	protected int getTotalNumberOfRecords(HttpServletRequest req) throws SQLException, NotLoggedInException {
		// On ne se compte pas, car on apparait nécessairement dans la première page (et cela n'affecte pas le max)
		return model.userRelations.getRelationsCount(thisOne);
	}

	@Override
	protected String getCallingURL() {
		return PROTECTED_MES_LISTES.substring(1);
	}

	@Override
	protected String getSpecificParameters(HttpServletRequest req) {
		return "";
	}

}
