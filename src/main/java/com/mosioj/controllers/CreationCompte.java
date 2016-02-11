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
import javax.servlet.http.HttpSession;

import com.mosioj.model.Personnes;
import com.mosioj.utils.ParametersUtils;
import com.mosioj.utils.validators.ParameterValidator;

import nl.captcha.Captcha;

@WebServlet("/creation_compte")
public class CreationCompte extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -101081965549681889L;

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		HttpSession session = request.getSession();
		Captcha captcha = (Captcha) session.getAttribute(Captcha.NAME);

		// Récupération des paramètres
		String pwd = ParametersUtils.readIt(request, "pwd");
		String email = ParametersUtils.readIt(request, "email").trim();

		// Validation des paramètres
		ParameterValidator validator = new ParameterValidator(pwd, "mot de passe", "Le ");
		List<String> pwdErrors = checkPwd(validator);
		request.setAttribute("pwd_errors", pwdErrors);

		validator = new ParameterValidator(email, "email", "L'");
		List<String> emailErrors = checkEmail(validator);
		request.setAttribute("email_errors", emailErrors);

		request.setCharacterEncoding("UTF-8"); // Do this so we can capture non-Latin chars
		String answer = request.getParameter("answer");
		boolean captchaOk = captcha.isCorrect(answer);
		if (!captchaOk) {
			request.setAttribute("captcha_errors", "Le texte entré ne correspond pas.");
		}

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
		if (!pwdErrors.isEmpty() || !emailErrors.isEmpty() || !captchaOk) {
			RequestDispatcher rd = request.getRequestDispatcher("/public/creation_compte.jsp");
			rd.forward(request, response);
			return;
		}

		// Les paramètres sont ok, on s'occupe de la requête
		Personnes manager = Personnes.getInstance();
		try {
			manager.addNewPersonne(email, hashPwd.toString());
			request.setAttribute("user", email);
			RequestDispatcher rd = request.getRequestDispatcher("/public/succes_creation.jsp");
			rd.forward(request, response);
		} catch (SQLException e) {
			request.setAttribute("error", e.getMessage());
			RequestDispatcher rd = request.getRequestDispatcher("/public/server_error.jsp");
			rd.forward(request, response);
		}
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
