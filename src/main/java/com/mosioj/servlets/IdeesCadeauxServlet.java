package com.mosioj.servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mosioj.model.table.GroupeJoinRequests;
import com.mosioj.model.table.Groupes;
import com.mosioj.model.table.Users;
import com.mosioj.utils.database.ConnectionIdKDo;

/**
 * An intermediate servlet for test purpose. Increase the visibility of tested method.
 * 
 * @author Jordan Mosio
 *
 */
@SuppressWarnings("serial")
public abstract class IdeesCadeauxServlet extends HttpServlet {

	/**
	 * L'interface vers la table GROUPES.
	 */
	protected Groupes groupes;

	/**
	 * Interface vers la table GROUPE_JOIN_REQUESTS.
	 */
	protected GroupeJoinRequests groupesJoinRequest;

	/**
	 * Interface vers la table USERS.
	 */
	protected Users users;

	/**
	 * The connection to use for parameters.
	 */
	protected ConnectionIdKDo validatorConnection;

	/**
	 * Class constructor.
	 */
	public IdeesCadeauxServlet() {
		groupes = new Groupes();
		groupesJoinRequest = new GroupeJoinRequests();
		validatorConnection = new ConnectionIdKDo();
	}

	/**
	 * For test purposes.
	 * 
	 * @param pGroupes
	 */
	public void setGroupes(Groupes pGroupes) {
		groupes = pGroupes;
	}

	/**
	 * For test purposes.
	 * 
	 * @param pGroupeJoinRequests
	 */
	public void setGroupeJoinRequests(GroupeJoinRequests pGroupeJoinRequests) {
		groupesJoinRequest = pGroupeJoinRequests;
	}
	
	/**
	 * For test purposes.
	 * 
	 * @param manager
	 */
	public void setValidatorConnection(ConnectionIdKDo manager) {
		validatorConnection = manager;
	}

	/**
	 * For test purposes.
	 * 
	 * @param pUsers
	 */
	public void setUsers(Users pUsers) {
		users = pUsers;
	}

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		super.doGet(req, resp);
	};

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		super.doPost(request, response);
	}
}
