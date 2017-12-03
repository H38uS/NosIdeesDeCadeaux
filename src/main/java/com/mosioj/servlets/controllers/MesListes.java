package com.mosioj.servlets.controllers;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mosioj.model.User;
import com.mosioj.servlets.securitypolicy.AllAccessToPostAndGet;
import com.mosioj.utils.RootingsUtils;

@WebServlet("/protected/mes_listes")
public class MesListes extends AbstractListes {

	private static final long serialVersionUID = -1774633803227715931L;
	public static final String PROTECTED_MES_LISTES = "/protected/mes_listes";

	/**
	 * Class constructor.
	 * 
	 */
	public MesListes() {
		super(new AllAccessToPostAndGet());
	}

	@Override
	protected List<User> getDisplayedUsers(int userId, int firstRow, HttpServletRequest req) throws SQLException {
		List<User> ids = new ArrayList<User>();
		int MAX = MAX_NUMBER_OF_RESULT;
		if (firstRow == 0) {
			ids.add(users.getUser(userId));
			MAX--;
		}
		ids.addAll(userRelations.getAllUsersInRelation(userId, firstRow, MAX));
		return ids;
	}

	@Override
	protected int getTotalNumberOfUsers(int userId, HttpServletRequest req) throws SQLException {
		return userRelations.getAllUsersInRelation(userId).size() + 1; // On se compte
	}

	@Override
	public void ideesKDoPOST(HttpServletRequest request, HttpServletResponse response) throws ServletException {
		RootingsUtils.redirectToPage(PROTECTED_MES_LISTES, request, response); // Rien de spécifique pour le moment
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
