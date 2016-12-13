package com.mosioj.servlets.controllers;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.mosioj.servlets.IdeesCadeauxServlet;
import com.mosioj.utils.ParametersUtils;
import com.mosioj.utils.RootingsUtils;
import com.mosioj.utils.validators.ParameterValidator;

import nl.captcha.Captcha;

@WebServlet("/creation_compte")
public class CreationCompte extends IdeesCadeauxServlet {

	public static final String SUCCES_URL = "/public/succes_creation.jsp";
	public static final String FORM_URL = "/public/creation_compte.jsp";

	/**
	 * 
	 */
	private static final long serialVersionUID = -101081965549681889L;
	
	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		HttpSession session = request.getSession();
		Captcha captcha = (Captcha) session.getAttribute(Captcha.NAME);

		// Récupération des paramètres
		String pwd = ParametersUtils.readIt(request, "pwd");
		String email = ParametersUtils.readIt(request, "email").trim();
		String name = ParametersUtils.readIt(request, "pseudo").trim();

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
			RootingsUtils.rootToPage(FORM_URL, request, response);
			return;
		}

		// Les paramètres sont ok, on s'occupe de la requête
		try {
			users.addNewPersonne(email, hashPwd.toString(), name);
			request.setAttribute("user", email);
			session.invalidate();
			RootingsUtils.rootToPage(SUCCES_URL, request, response);
		} catch (SQLException e) {
			RootingsUtils.rootToGenericSQLError(e, request, response);
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
		validator.checkIsUnique("select count(*) from users where email = ?", validatorConnection);
		return validator.getErrors();
	}
}
