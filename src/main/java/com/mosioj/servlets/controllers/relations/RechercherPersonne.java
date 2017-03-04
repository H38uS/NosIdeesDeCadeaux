package com.mosioj.servlets.controllers.relations;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mosioj.model.User;
import com.mosioj.servlets.IdeesCadeauxServlet;
import com.mosioj.utils.ParametersUtils;
import com.mosioj.utils.RootingsUtils;

@WebServlet("/protected/rechercher_personne")
public class RechercherPersonne extends IdeesCadeauxServlet {

	private static final long serialVersionUID = 9147880158497428623L;
	public static final String FORM_URL = "/protected/rechercher_personne.jsp";

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String userNameOrEmail = ParametersUtils.readAndEscape(request, "name").trim();
		try {
			List<User> foundUsers = users.getUsers(userNameOrEmail);
			foundUsers.remove(users.getUser(ParametersUtils.getUserId(request)));
			request.setAttribute("users", foundUsers);
			RootingsUtils.rootToPage(FORM_URL, request, response);
		} catch (SQLException e) {
			RootingsUtils.rootToGenericSQLError(e, request, response);
		}
	}

	// FIXME : ne pas afficher le bouton si on est déjà amis
	// FIXME : faire une checkbox pour n'afficher que les personnes avec qui on est pas amis

	// TODO : ne pas afficher le bouton rejoindre personne si on a déjà envoyé une demande...
	// TODO limiter le nombre de résultat (à 20 ?)

}
