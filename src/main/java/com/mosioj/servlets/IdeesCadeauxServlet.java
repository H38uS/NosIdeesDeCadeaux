package com.mosioj.servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mosioj.model.table.GroupeJoinRequests;
import com.mosioj.model.table.Groupes;

/**
 * An intermediate servlet for test purpose.
 * Increase the visibility of tested method.
 * 
 * @author Jordan Mosio
 *
 */
@SuppressWarnings("serial")
public abstract class IdeesCadeauxServlet  extends HttpServlet {

	public final Groupes groupes;
	public final GroupeJoinRequests groupesJoinRequest;
	
	// TODO voir avec les logs si instancié plusieurs fois ou pas : si non : passer la DB !

	public IdeesCadeauxServlet(Groupes groupesManager, GroupeJoinRequests groupesJoinRequestManager) {
		groupes = groupesManager;
		groupesJoinRequest = groupesJoinRequestManager;
	}
	
	public IdeesCadeauxServlet() {
		this(Groupes.getGroupesManager(), GroupeJoinRequests.getManager());
	}
	
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		super.doGet(req, resp);
	};

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		super.doPost(request, response);
	};
}
