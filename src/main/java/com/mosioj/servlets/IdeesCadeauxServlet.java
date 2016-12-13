package com.mosioj.servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mosioj.model.table.Categories;
import com.mosioj.model.table.GroupeJoinRequests;
import com.mosioj.model.table.Groupes;
import com.mosioj.model.table.Idees;
import com.mosioj.model.table.Priorites;
import com.mosioj.model.table.Users;
import com.mosioj.utils.database.DataSourceIdKDo;

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
	protected DataSourceIdKDo validatorConnection;

	/**
	 * The connections to the IDEES table.
	 */
	protected Idees idees;

	/**
	 * The connections to the CATEGORIES table.
	 */
	protected Categories categories;

	/**
	 * The connections to the PRIORITIES table.
	 */
	protected Priorites priorities;

	/**
	 * Class constructor.
	 */
	public IdeesCadeauxServlet() {
		groupes = new Groupes();
		groupesJoinRequest = new GroupeJoinRequests();
		validatorConnection = new DataSourceIdKDo();
		users = new Users();
		idees = new Idees();
		categories = new Categories();
		priorities = new Priorites();
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
	public void setValidatorConnection(DataSourceIdKDo manager) {
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

	/**
	 * For test purposes.
	 * 
	 * @param pIdees
	 */
	public void setIdees(Idees pIdees) {
		idees = pIdees;
	}

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		super.doGet(req, resp);
	};

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		super.doPost(request, response);
	}

	public void setCat(Categories cat) {
		categories = cat;
	}

	public void setPrio(Priorites prio) {
		priorities = prio;
	}

}
