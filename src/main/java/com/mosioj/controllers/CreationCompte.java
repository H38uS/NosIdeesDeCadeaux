package com.mosioj.controllers;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mosioj.model.Personnes;
import com.mosioj.utils.validators.ParameterValidator;

@WebServlet("/creation_compte")
public class CreationCompte extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -101081965549681889L;

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		// Récupération des paramètres
		String user = request.getParameter("user").trim();
		String pwd = request.getParameter("pwd");
		String email = request.getParameter("email").trim();

		// Validation des paramètres
		ParameterValidator validator = new ParameterValidator(user, "identifiant", "L'");
		List<String> userErrors = checkUser(validator);
		request.setAttribute("user_errors", userErrors);

		validator = new ParameterValidator(pwd, "mot de passe", "Le ");
		List<String> pwdErrors = checkPwd(validator);
		request.setAttribute("pwd_errors", pwdErrors);

		validator = new ParameterValidator(email, "email", "L'");
		List<String> emailErrors = checkEmail(validator);
		request.setAttribute("email_errors", emailErrors);

		// Password hash
		StringBuffer hashPwd = new StringBuffer();
		try {
			MessageDigest md = MessageDigest.getInstance("SHA");
			md.update(pwd.getBytes());
			byte[] digest = md.digest();
			for (byte b : digest) {
				hashPwd.append(String.format("%02x", b & 0xff));
			}
		} catch (NoSuchAlgorithmException e) {
			pwdErrors.add("Echec du chiffrement du mot de passe. Erreur: " + e.getMessage());
		}

		// Retour au formulaire si un paramètre est incorrect
		if (!userErrors.isEmpty() || !pwdErrors.isEmpty() || !emailErrors.isEmpty()) {
			RequestDispatcher rd = request.getRequestDispatcher("/public/creation_compte.jsp");
			rd.forward(request, response);
			return;
		}

		// Les paramètres sont ok, on s'occupe de la requête
		Personnes manager = Personnes.getInstance();
		try {
			manager.addNewPersonne(user, hashPwd.toString(), email);
			request.setAttribute("user", user);
			RequestDispatcher rd = request.getRequestDispatcher("/public/succes_creation.jsp");
			rd.forward(request, response);
		} catch (SQLException e) {
			request.setAttribute("error", e.getMessage());
			RequestDispatcher rd = request.getRequestDispatcher("/public/server_error.jsp");
			rd.forward(request, response);
		}
	}

	/**
	 * Checks the validity of the user parameter.
	 * 
	 * @param validator
	 * @return The list of errors found.
	 */
	private List<String> checkUser(ParameterValidator validator) {
		validator.checkEmpty();
		validator.checkSize(0, 15);
		validator.checkIsUnique("select count(*) from personnes where login = ?");
		return validator.getErrors();
	}

	/**
	 * Checks the validity of the pwd parameter.
	 * 
	 * @param validator
	 * @return The list of errors found.
	 */
	private List<String> checkPwd(ParameterValidator validator) {
		validator.checkEmpty();
		validator.checkSize(8, 30);
		return validator.getErrors();
	}

	/**
	 * Checks the validity of the email parameter.
	 * 
	 * @param validator
	 * @return The list of errors found.
	 */
	private List<String> checkEmail(ParameterValidator validator) {
		validator.checkEmpty();
		validator.checkIsEmailValid();
		validator.checkIsUnique("select count(*) from personnes where email = ?");
		return validator.getErrors();
	}
}
